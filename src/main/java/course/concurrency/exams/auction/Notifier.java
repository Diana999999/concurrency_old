package course.concurrency.exams.auction;

import java.util.concurrent.*;

public class Notifier {

    private final Executor executor = ForkJoinPool.commonPool();

    public void sendOutdatedMessage(Bid bid) {
        executor.execute(this::imitateSending);
    }

    private void imitateSending() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
    }

    public void shutdown() {}
}
