package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBidRef = new AtomicReference<>(new Bid(0L, 0L, 0L));

    public boolean propose(Bid bid) {
        Bid latest;
        do {
            latest = latestBidRef.get();
            if (bid.getPrice() < latest.getPrice()) {
                return false;
            }
        } while (!latestBidRef.compareAndSet(latest, bid));
        notifier.sendOutdatedMessage(latest);
        return true;
    }

    public Bid getLatestBid() {
        return latestBidRef.get();
    }
}