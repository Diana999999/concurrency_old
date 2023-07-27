package course.concurrency.exams.auction;

import java.util.concurrent.locks.StampedLock;

public class AuctionPessimistic implements Auction {

    private final Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private Bid latestBid = new Bid(0L, 0L, 0L);
    private StampedLock lock = new StampedLock();

    public boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.getPrice()) {
            long stamp = lock.writeLock();
            try {
                notifier.sendOutdatedMessage(latestBid);
                latestBid = bid;
                return true;
            } finally {
                lock.unlockWrite(stamp);
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        long stamp = lock.readLock();
        try {
            return latestBid;
        } finally {
            lock.unlockRead(stamp);
        }
    }
}
