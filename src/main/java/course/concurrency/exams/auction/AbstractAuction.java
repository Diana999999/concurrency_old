package course.concurrency.exams.auction;

public abstract class AbstractAuction implements Auction {

    protected boolean isChallengerBid(Bid bid, Bid latestBid) {
        return latestBid == null || bid.getPrice() > latestBid.getPrice();
    }
}
