package course.concurrency.m2_async.cf.report;

import course.concurrency.m2_async.cf.LoadGenerator;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ReportServiceCF implements ReportService {

    private final ExecutorService executor;

    private LoadGenerator loadGenerator = new LoadGenerator();

    public ReportServiceCF(ExecutorService executor) {
        this.executor = executor;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public AbstractReport.Report getReportSleep() {
        CompletableFuture<Collection<AbstractReport.Item>> itemsCF =
                CompletableFuture.supplyAsync(this::getItemsSleep, executor);

        CompletableFuture<Collection<AbstractReport.Customer>> customersCF =
                CompletableFuture.supplyAsync(this::getActiveCustomersSleep, executor);

        CompletableFuture<AbstractReport.Report> reportTask =
                customersCF.thenCombine(itemsCF,
                        (customers, orders) -> combineResults(orders, customers));

        return reportTask.join();
    }

    public AbstractReport.Report getReportCompute() {
        CompletableFuture<Collection<AbstractReport.Item>> itemsCF =
                CompletableFuture.supplyAsync(this::getItemsCompute, executor);

        CompletableFuture<Collection<AbstractReport.Customer>> customersCF =
                CompletableFuture.supplyAsync(this::getActiveCustomersCompute, executor);

        CompletableFuture<AbstractReport.Report> reportTask =
                customersCF.thenCombine(itemsCF,
                        (customers, orders) -> combineResults(orders, customers));

        return reportTask.join();
    }

    private AbstractReport.Report combineResults(Collection<AbstractReport.Item> items, Collection<AbstractReport.Customer> customers) {
        return new AbstractReport.Report();
    }

    private Collection<AbstractReport.Customer> getActiveCustomersSleep() {
        LoadGenerator.sleep();
        LoadGenerator.sleep();
        return List.of(new AbstractReport.Customer(), new AbstractReport.Customer());
    }

    private Collection<AbstractReport.Customer> getActiveCustomersCompute() {
        LoadGenerator.compute();
        LoadGenerator.compute();
        return List.of(new AbstractReport.Customer(), new AbstractReport.Customer());
    }

    private Collection<AbstractReport.Item> getItemsSleep() {
        LoadGenerator.sleep();
        return List.of(new AbstractReport.Item(), new AbstractReport.Item());
    }

    private Collection<AbstractReport.Item> getItemsCompute() {
        LoadGenerator.compute();
        return List.of(new AbstractReport.Item(), new AbstractReport.Item());
    }

    public void shutdown() {
        executor.shutdown();
    }
}
