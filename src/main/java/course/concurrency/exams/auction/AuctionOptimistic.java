package course.concurrency.exams.auction;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic extends AbstractAuction {

    private final Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBid = new AtomicReference<>(null);

    @Override
    public boolean propose(Bid bid) {
        Objects.requireNonNull(bid);

        Bid currentLatest = latestBid.get();
        Bid newLatest;

        if (isChallengerBid(bid, currentLatest)) {
            do {
                currentLatest = latestBid.get();

                if (isChallengerBid(bid, currentLatest)) {
                    newLatest = bid;
                } else {
                    return false;
                }
            } while (!latestBid.compareAndSet(currentLatest, newLatest));
        } else {
            return false;
        }

        notifier.sendOutdatedMessage(currentLatest);
        return true;
    }

    @Override
    public Bid getLatestBid() {
        return latestBid.get();
    }
}
