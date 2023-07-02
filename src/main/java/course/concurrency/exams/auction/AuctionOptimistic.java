package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBidRef = new AtomicReference<>(null);

    public boolean propose(Bid bid) {
        Bid fromRef = latestBidRef.get();
        if (fromRef == null || bid.getPrice() > fromRef.getPrice()) {
            notifier.sendOutdatedMessage(fromRef);
            latestBidRef.set(bid);
            return true;
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBidRef.get();
    }
}
