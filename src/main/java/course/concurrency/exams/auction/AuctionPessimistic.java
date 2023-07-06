package course.concurrency.exams.auction;

public class AuctionPessimistic implements Auction {

    private final Object lockObj = new Object();
    private final Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private Bid latestBid = new Bid(0L, 0L, 0L);

    public synchronized boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.getPrice()) {
            synchronized (lockObj) {
                notifier.sendOutdatedMessage(latestBid);
                latestBid = bid;
                return true;
            }
        }
        return false;
    }

    public synchronized Bid getLatestBid() {
        synchronized (lockObj) {
            return latestBid;
        }
    }
}
