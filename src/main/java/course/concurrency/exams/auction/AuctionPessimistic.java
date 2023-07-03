package course.concurrency.exams.auction;

import java.util.Objects;

public class AuctionPessimistic extends AbstractAuction {

    private final Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid;

    @Override
    public boolean propose(Bid bid) {
        Objects.requireNonNull(bid);

        final Bid currentLatest = latestBid;

        if (isChallengerBid(bid, currentLatest)) {
            synchronized (this) {
                if (isChallengerBid(bid, latestBid)) {
                    latestBid = bid;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }

        notifier.sendOutdatedMessage(currentLatest);
        return true;
    }

    @Override
    public Bid getLatestBid() {
        return latestBid;
    }
}
