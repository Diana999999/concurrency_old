package course.concurrency.exams.auction;

import java.util.concurrent.*;

public class Notifier {

    private final ExecutorService executorService =
            new ThreadPoolExecutor(50,100,0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    public void sendOutdatedMessage(Bid bid) {
        imitateSending();
    }

    private void imitateSending() {
        executorService.submit(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
