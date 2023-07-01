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

        synchronized(this) {
            if (latestBid == null || bid.getPrice() > latestBid.getPrice()) {
                latestBid = bid;
            }
        }

        if (bid.equals(latestBid)) {
            notifier.sendOutdatedMessage(latestBid);
            return true;
        }

        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
