package course.concurrency.m2_async.cf.min_price;

import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.lang.Double.NaN;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    ExecutorService executorService = Executors.newCachedThreadPool();

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {

        /* ForkJoinPool по умолчанию использует уровень параллелизмы как CPU cores - 1.
        *  При этом значение core pool size по умолчанию равно уровню параллелизма.
        *  В следствии указанного настройки forkjoinpool по умолчанию не подходят для
        *  большого количество I/O задач с необходимым тайм-аутом исполнения.
        *
        *  Для того чтобы все тесты успешно завершились дефолтный forkjoinpool был скорерктирован.        *
        * */


        Executor executor = executor();

        Executor forkJoinPool = forkJoinPool();

        List<CompletableFuture<Double>> min = shopIds.stream()
                .map(shopId -> CompletableFuture
                        .supplyAsync(() ->
                        {
                            System.out.println("ThreadName = " + Thread.currentThread().getName());
                            return priceRetriever.getPrice(itemId, shopId);
                        }, forkJoinPool)
                        .handle((result, exception) -> result != null ? result : NaN)
                        .completeOnTimeout(NaN, 2900l, TimeUnit.MILLISECONDS))
                .collect(Collectors.toList());

        List<Double> prices = min.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        System.out.println(prices);

        return prices.stream()
                .filter(x-> !x.isNaN())
                .min(Comparator.comparing(Double::valueOf))
                .orElse(NaN);
    }

    private ForkJoinPool forkJoinPool() {

        return new ForkJoinPool(
                50,
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                null,
                false,
                50,
                60,
                1,
                null,
                60L,
                TimeUnit.SECONDS);
    }

    private TaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(60);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("Custom executor");
        executor.initialize();
        return executor;
    }
}
