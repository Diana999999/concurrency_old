package course.concurrency.m2_async.cf;

import course.concurrency.m2_async.cf.report.ReportService;
import course.concurrency.m2_async.cf.report.ReportServiceCF;
import course.concurrency.m2_async.cf.report.ReportServiceExecutors;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

public class ReportServiceTests {

    @Test
    public void testReportServiceExecutors() throws InterruptedException {
        final ExecutorService singleExecutor = Executors.newSingleThreadExecutor();
        final ExecutorService cached = Executors.newCachedThreadPool();
        testMultipleTasks(new ReportServiceExecutors(singleExecutor), true);
        testMultipleTasks(new ReportServiceExecutors(cached), true);
        for (int i = 2; i <= 48; i = i * 2) {
            testMultipleTasks(new ReportServiceExecutors(Executors.newFixedThreadPool(i)), true);
        }
        testMultipleTasks(new ReportServiceExecutors(singleExecutor), false);
        testMultipleTasks(new ReportServiceExecutors(cached), false);
        for (int i = 2; i <= 48; i = i * 2) {
            testMultipleTasks(new ReportServiceExecutors(Executors.newFixedThreadPool(i)), false);
        }
    }

    @Test
    public void testReportServiceCF() throws InterruptedException {
        final ForkJoinPool commonPool = ForkJoinPool.commonPool();
        testMultipleTasks(new ReportServiceCF(commonPool), true);
        for (int i = 2; i <= 48; i = i * 2) {
            testMultipleTasks(new ReportServiceCF(new ForkJoinPool(i)), true);
        }
        testMultipleTasks(new ReportServiceCF(commonPool), false);
        for (int i = 2; i <= 48; i = i * 2) {
            testMultipleTasks(new ReportServiceCF(new ForkJoinPool(i)), false);
        }
    }

    private void testMultipleTasks(ReportService reportService, boolean isSleep) throws InterruptedException {
        int poolSize = Runtime.getRuntime().availableProcessors() * 3;
        int iterations = 5;

        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        for (int i = 0; i < poolSize; i++) {
            executor.submit(() -> {
                try {
                    latch.await();
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
                for (int it = 0; it < iterations; it++) {
                    if (isSleep) {
                        reportService.getReportSleep();
                    } else {
                        reportService.getReportCompute();
                    }
                }
            });
        }

        long start = System.currentTimeMillis();
        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);
        long end = System.currentTimeMillis();
        Result result = new Result(start, end);

        System.out.println("Execution time: " + (double) (result.end - result.start) / 1000 + " seconds");
        if (reportService instanceof ReportServiceExecutors) {
            System.out.println("There were " + ((ThreadPoolExecutor) reportService.getExecutor()).getPoolSize() + " treads created");
        } else if (reportService instanceof ReportServiceCF) {
            System.out.println("There were " + ((ForkJoinPool) reportService.getExecutor()).getPoolSize() + " treads created");
        }
    }

    private static class Result {
        public final long start;
        public final long end;

        public Result(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }
}
