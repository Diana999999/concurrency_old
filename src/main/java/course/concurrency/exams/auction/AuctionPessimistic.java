package course.concurrency.exams.auction;

import static java.lang.Thread.onSpinWait;

public class AuctionPessimistic implements Auction {

    private final Object lockObj = new Object();
    private final Notifier notifier;
    private Bid latestBid = new Bid(0L, 0L, 0L);
    private volatile boolean doPropose;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    public boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.getPrice()) {
            synchronized (lockObj) {
                doPropose = true;
                notifier.sendOutdatedMessage(latestBid);
                latestBid = bid;
                doPropose = false;
                return true;
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        while (doPropose) {
            onSpinWait();
        }
        return latestBid;
    }
}
