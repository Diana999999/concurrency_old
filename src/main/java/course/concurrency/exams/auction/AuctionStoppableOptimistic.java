package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private final Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBidRef = new AtomicReference<>(new Bid(0L, 0L, 0L));
    private volatile boolean run = true;

    public boolean propose(Bid bid) {
        Bid latestBid = latestBidRef.get();
        if (run && bid.getPrice() > latestBid.getPrice()) {
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

    public Bid stopAuction() {
        this.run = false;
        return latestBidRef.get();
    }

    public void run(){
        this.run = true;
    }
}
