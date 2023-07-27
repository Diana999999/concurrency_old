package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBidRef = new AtomicReference<>(new Bid(0L, 0L, 0L));

    public boolean propose(Bid bid) {
        Bid latest = latestBidRef.get();
        while (bid.getPrice() > latest.getPrice() &&
                !latestBidRef.compareAndSet(latest, bid) &&
                !send(latest)) {
            latest = latestBidRef.get();
        }
        return bid.getPrice() > latest.getPrice();
    }

    public Bid getLatestBid() {
        return latestBidRef.get();
    }

    private boolean send(Bid latest){
        notifier.sendOutdatedMessage(latest);
        return true;
    }
}