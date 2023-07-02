package course.concurrency.exams.auction;

import java.util.Objects;

public class AuctionPessimistic implements Auction {

    private final Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid;

    public boolean propose(Bid bid) {
        Objects.requireNonNull(bid);

        final Bid currentLatest = latestBid;

        synchronized (this) {
            if (latestBid == null || bid.getPrice() > latestBid.getPrice()) {
                latestBid = bid;
            } else {
                return false;
            }
        }

        notifier.sendOutdatedMessage(currentLatest);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
