package course.concurrency.exams.auction;

public class AuctionPessimistic implements Auction {

    private final Object lockObj = new Object();
    private final Notifier notifier;
    private volatile Bid latestBid = new Bid(0L, 0L, 0L);

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    public boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.getPrice()) {
            synchronized (lockObj) {
                latestBid = bid;
                notifier.sendOutdatedMessage(latestBid);
                return true;
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
