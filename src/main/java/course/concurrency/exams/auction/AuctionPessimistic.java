package course.concurrency.exams.auction;

public class AuctionPessimistic implements Auction {
    private final Object lock = new Object();
    private final Notifier notifier;
    private volatile Bid latestBid = new Bid(-1L, -1L, -1L);

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    public boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.getPrice()) {
            synchronized (lock) {
                if (bid.getPrice() > latestBid.getPrice()) {
                    notifier.sendOutdatedMessage(latestBid);
                    latestBid = bid;
                    return true;
                }
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
