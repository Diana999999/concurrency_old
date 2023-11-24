package course.concurrency.exams.auction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Notifier {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void sendOutdatedMessage(Bid bid) {
        executor.submit(this::imitateSending);
    }

    private void imitateSending() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("An exception was caught");
        }
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
