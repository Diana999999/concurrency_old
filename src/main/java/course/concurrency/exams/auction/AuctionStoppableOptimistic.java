package course.concurrency.exams.auction;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class AuctionStoppableOptimistic extends AbstractAuctionStoppable {

    private final Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicMarkableReference<Bid> latestBid = new AtomicMarkableReference<>(START_BID, false);

    public boolean propose(Bid bid) {
        Objects.requireNonNull(bid);

        Bid currentLatest;

        do {
            currentLatest = latestBid.getReference();

            if (latestBid.isMarked() || !isChallengerBid(bid, currentLatest) ) return false;
        } while (!latestBid.compareAndSet(currentLatest, bid, false, false));

        notifier.sendOutdatedMessage(currentLatest);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.getReference();
    }

    public Bid stopAuction() {
        Bid currentLatest;

        do {
            boolean[] markHolder = new boolean[1];
            currentLatest = latestBid.get(markHolder);

            if (markHolder[0]) return currentLatest;
        } while (!latestBid.compareAndSet(currentLatest, currentLatest, false, true));

        return currentLatest;
    }
}
