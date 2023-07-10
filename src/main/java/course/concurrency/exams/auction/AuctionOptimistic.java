package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }
    private final AtomicReference<Bid> latestBidRef = new AtomicReference<>(new Bid(-1L, -1L, -1L));

    public boolean propose(Bid bid) {
        Bid expectedLatestBid;
        do {
            expectedLatestBid = latestBidRef.get();
            if (bid.getPrice() <= expectedLatestBid.getPrice()) {
                return false;
            }
        } while (!latestBidRef.compareAndSet(expectedLatestBid, bid));
        notifier.sendOutdatedMessage(expectedLatestBid);
        return true;
    }

    public Bid getLatestBid() {
        return latestBidRef.get();
    }
}
