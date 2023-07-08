package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBidRef = new AtomicReference<>(new Bid(0L, 0L, 0L));

    public boolean propose(Bid bid) {
        Bid latestBid = latestBidRef.get();
        if (bid.getPrice() > latestBid.getPrice()) {
            while (!latestBidRef.compareAndSet(latestBid, bid)) {
                latestBid = latestBidRef.get();
            }
            notifier.sendOutdatedMessage(latestBid);
            return true;
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBidRef.get();
    }
}
