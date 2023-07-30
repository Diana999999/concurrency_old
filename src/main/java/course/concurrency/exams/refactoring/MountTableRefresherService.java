package course.concurrency.exams.refactoring;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;


public class MountTableRefresherService {

    private Others.RouterStore routerStore = new Others.RouterStore();
    private long cacheUpdateTimeout;

    /**
     * All router admin clients cached. So no need to create the client again and
     * again. Router admin address(host:port) is used as key to cache RouterClient
     * objects.
     */
    private Others.LoadingCache<String, Others.RouterClient> routerClientsCache;

    private final ThreadFactory cleanThreadFactory = runnable -> {
        Thread thread = new Thread();
        thread.setName("ClientsCacheCleaner");
        thread.setDaemon(true);
        return thread;
    };

    /**
     * Removes expired RouterClient from routerClientsCache.
     */
    private final ScheduledExecutorService clientCacheCleanerScheduler =
        Executors.newSingleThreadScheduledExecutor(cleanThreadFactory);

    private final ForkJoinPool.ForkJoinWorkerThreadFactory refreshThreadFactory = pool -> {
        ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
        worker.setName("MountTableRefresh_" + worker.getPoolIndex());
        return worker;
    };

    private final ForkJoinPool refreshExecutor =
        new ForkJoinPool(Runtime.getRuntime().availableProcessors(), refreshThreadFactory, null, false);

    public void serviceInit() {
        long routerClientMaxLiveTime = 15L;
        this.cacheUpdateTimeout = 10L;
        routerClientsCache = new Others.LoadingCache<>();
        routerStore.getCachedRecords().stream().map(Others.RouterState::getAdminAddress)
            .forEach(addr -> routerClientsCache.add(addr, new Others.RouterClient()));

        initClientCacheCleaner(routerClientMaxLiveTime);
    }

    public void serviceStop() {
        clientCacheCleanerScheduler.shutdown();
        // remove and close all admin clients
        routerClientsCache.cleanUp();
    }

    private void initClientCacheCleaner(long routerClientMaxLiveTime) {
        /*
         * When cleanUp() method is called, expired RouterClient will be removed and
         * closed.
         */
        clientCacheCleanerScheduler.scheduleWithFixedDelay(
            () -> routerClientsCache.cleanUp(),
            routerClientMaxLiveTime,
            routerClientMaxLiveTime,
            TimeUnit.MILLISECONDS
        );
    }

    /**
     * Refresh mount table cache of this router as well as all other routers.
     */
    public void refresh() {
        List<Others.RouterState> cachedRecords = routerStore.getCachedRecords();

        ConcurrentHashMap<String, Boolean> results = new ConcurrentHashMap(cachedRecords.size());

        var tasks = cachedRecords
            .stream()
            .map(routerState -> {
                String adminAddress = routerState.getAdminAddress();

                // this router has not enabled router admin.
                if (adminAddress == null || adminAddress.length() == 0) return null;

                String managerAddress = isLocalAdmin(adminAddress) ? "local" : adminAddress;
                Others.MountTableManager manager = getManager(managerAddress);

                return CompletableFuture.runAsync(
                    () -> results.put(adminAddress, manager.refresh()),
                    refreshExecutor
                ).exceptionally(throwable -> null);
            })
            .filter(Objects::nonNull)
            .toArray(CompletableFuture[]::new);

        try {
            CompletableFuture.allOf(tasks)
                .completeOnTimeout(null, cacheUpdateTimeout, TimeUnit.MILLISECONDS)
                .get();
        } catch (InterruptedException e) {
            log("Mount table cache refresher was interrupted.");
        } catch (ExecutionException e) {
            log("Mount table cache refresher was completed with error");
        }

        if (results.size() != tasks.length) {
            log("Not all router admins updated their cache");
        }

        logResults(results);
    }

    public Others.MountTableManager getManager(String managerAddress) {
        return new Others.MountTableManager(managerAddress);
    }

    private void logResults(ConcurrentHashMap<String, Boolean> results) {
        int successCount = 0;
        int failureCount = 0;

        for (Map.Entry<String, Boolean> result : results.entrySet()) {
            if (result.getValue()) {
                successCount++;
            } else {
                failureCount++;
                removeFromCache(result.getKey());
            }
        }

        log(String.format(
            "Mount table entries cache refresh successCount=%d,failureCount=%d", successCount, failureCount
        ));
    }

    private void removeFromCache(String adminAddress) {
        routerClientsCache.invalidate(adminAddress);
    }

    private boolean isLocalAdmin(String adminAddress) {
        return adminAddress.contains("local");
    }

    public void log(String message) {
        System.out.println(message);
    }

    public void setCacheUpdateTimeout(long cacheUpdateTimeout) {
        this.cacheUpdateTimeout = cacheUpdateTimeout;
    }

    public void setRouterClientsCache(Others.LoadingCache cache) {
        this.routerClientsCache = cache;
    }

    public void setRouterStore(Others.RouterStore routerStore) {
        this.routerStore = routerStore;
    }
}