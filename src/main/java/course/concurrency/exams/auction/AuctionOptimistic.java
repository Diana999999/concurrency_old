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
            if (latestBidRef.compareAndSet(latestBid, bid)) {
                notifier.sendOutdatedMessage(latestBid);
                return true;
            }
            propose(bid);
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBidRef.get();
    }
}
