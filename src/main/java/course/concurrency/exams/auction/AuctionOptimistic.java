package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBid = new AtomicReference<>(new Bid(0L, 0L, Long.MIN_VALUE));

    public boolean propose(Bid bid) {
        Bid latestBidActual;
       do {
            latestBidActual = latestBid.get();
            if (bid.getPrice() <= latestBidActual.getPrice()) {
                return false;
            }
        } while (!latestBid.compareAndSet(latestBidActual, bid));
        notifier.sendOutdatedMessage(latestBidActual);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
