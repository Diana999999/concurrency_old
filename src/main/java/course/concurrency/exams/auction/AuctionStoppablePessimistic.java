package course.concurrency.exams.auction;

import java.util.Objects;

public class AuctionStoppablePessimistic extends AbstractAuctionStoppable {

    private final Notifier notifier;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = START_BID;
    private volatile boolean isStopped = false;

    @Override
    public boolean propose(Bid bid) {
        Objects.requireNonNull(bid);

        if (!isChallengerBid(bid, latestBid) || isStopped) return false;

        synchronized (this) {
            if (isStopped) return false;

            if (isChallengerBid(bid, latestBid)) {
                notifier.sendOutdatedMessage(latestBid);
                latestBid = bid;
                return true;
            }

            return false;
        }
    }

    @Override
    public Bid getLatestBid() {
        return latestBid;
    }

    @Override
    public Bid stopAuction() {
        isStopped = true;
        return latestBid;
    }

}
