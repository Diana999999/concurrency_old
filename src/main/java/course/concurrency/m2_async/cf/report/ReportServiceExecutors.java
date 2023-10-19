package course.concurrency.m2_async.cf.report;

import course.concurrency.m2_async.cf.LoadGenerator;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ReportServiceExecutors extends AbstractReport implements ReportService {

    private final ExecutorService executor;

    public ReportServiceExecutors(ExecutorService executor) {
        this.executor = executor;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public AbstractReport.Report getReportSleep() {
        Future<Collection<AbstractReport.Item>> itemFuture =
                executor.submit(this::getItemsSleep);
        Future<Collection<AbstractReport.Customer>> customersFuture =
                executor.submit(this::getActiveCustomersSleep);

        Report results = getCombinedResults(customersFuture, itemFuture);
        if (results != null) return results;

        return new AbstractReport.Report();
    }

    public AbstractReport.Report getReportCompute() {
        Future<Collection<AbstractReport.Item>> itemFuture =
                executor.submit(this::getItemsCompute);
        Future<Collection<AbstractReport.Customer>> customersFuture =
                executor.submit(this::getActiveCustomersCompute);

        Report items = getCombinedResults(customersFuture, itemFuture);
        if (items != null) return items;

        return new AbstractReport.Report();
    }

    private Report getCombinedResults(Future<Collection<Customer>> customersFuture, Future<Collection<Item>> itemFuture) {
        try {
            Collection<Customer> customers = customersFuture.get();
            Collection<Item> items = itemFuture.get();
            return combineResults(items, customers);
        } catch (ExecutionException | InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private AbstractReport.Report combineResults(Collection<AbstractReport.Item> items, Collection<AbstractReport.Customer> customers) {
        return new AbstractReport.Report();
    }

    private Collection<AbstractReport.Customer> getActiveCustomersSleep() {
        LoadGenerator.sleep();
        LoadGenerator.sleep();
        return List.of(new AbstractReport.Customer(), new AbstractReport.Customer());
    }

    private Collection<AbstractReport.Item> getItemsSleep() {
        LoadGenerator.sleep();
        return List.of(new AbstractReport.Item(), new AbstractReport.Item());
    }

    private Collection<AbstractReport.Customer> getActiveCustomersCompute() {
        LoadGenerator.compute();
        LoadGenerator.compute();
        return List.of(new AbstractReport.Customer(), new AbstractReport.Customer());
    }

    private Collection<AbstractReport.Item> getItemsCompute() {
        LoadGenerator.compute();
        return List.of(new AbstractReport.Item(), new AbstractReport.Item());
    }

    public void shutdown() {
        executor.shutdown();
    }
}
