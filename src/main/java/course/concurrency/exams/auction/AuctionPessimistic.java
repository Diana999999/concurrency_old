package course.concurrency.exams.auction;

public class AuctionPessimistic implements Auction {

    private final Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private Bid latestBid = new Bid(0L, 0L, 0L);

    public boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.getPrice()) {
            synchronized (this) {
                notifier.sendOutdatedMessage(latestBid);
                latestBid = bid;
                return true;
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
