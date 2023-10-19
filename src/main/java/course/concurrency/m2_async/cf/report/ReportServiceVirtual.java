package course.concurrency.m2_async.cf.report;

import java.util.concurrent.ExecutorService;

public class ReportServiceVirtual implements ReportService {
    @Override
    public ExecutorService getExecutor() {
        return null;
    }

    @Override
    public AbstractReport.Report getReportSleep() {
        return null;
    }

    @Override
    public AbstractReport.Report getReportCompute() {
        return null;
    }

//    private ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
//
//    private LoadGenerator loadGenerator = new LoadGenerator();
//
//    public AbstractReport.Report getReport() {
//        Future<Collection<AbstractReport.Item>> iFuture =
//                executor.submit(() -> getItems());
//        Future<Collection<AbstractReport.Customer>> customersFuture =
//                executor.submit(() -> getActiveCustomers());
//
//        try {
//            Collection<AbstractReport.Customer> customers = customersFuture.get();
//            Collection<AbstractReport.Item> items = iFuture.get();
//            return combineResults(items, customers);
//        } catch (ExecutionException | InterruptedException ex) {}
//
//        return new AbstractReport.Report();
//    }
//
//    private AbstractReport.Report combineResults(Collection<AbstractReport.Item> items, Collection<AbstractReport.Customer> customers) {
//        return new AbstractReport.Report();
//    }
//
//    private Collection<AbstractReport.Customer> getActiveCustomers() {
//        loadGenerator.work();
//        loadGenerator.work();
//        return List.of(new AbstractReport.Customer(), new AbstractReport.Customer());
//    }
//
//    private Collection<AbstractReport.Item> getItems() {
//        loadGenerator.work();
//        return List.of(new AbstractReport.Item(), new AbstractReport.Item());
//    }
//
//    public void shutdown() {
//        executor.shutdown();
//    }
}
