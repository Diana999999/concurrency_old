package course.concurrency.m2_async.cf.report;

import java.util.concurrent.ExecutorService;

public interface ReportService {
    ExecutorService getExecutor();

    AbstractReport.Report getReportSleep();
    AbstractReport.Report getReportCompute();
}
