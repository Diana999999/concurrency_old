package course.concurrency.exams.auction;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBid = new AtomicReference<>(null);

    public boolean propose(Bid bid) {
        Objects.requireNonNull(bid);

        Bid currentLatest;
        Bid newLatest;

        do {
            currentLatest = latestBid.get();

            if (currentLatest == null || bid.getPrice() > currentLatest.getPrice()) {
                newLatest = bid;
            } else {
                return false;
            }
        } while (!latestBid.compareAndSet(currentLatest, newLatest));

        notifier.sendOutdatedMessage(currentLatest);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
