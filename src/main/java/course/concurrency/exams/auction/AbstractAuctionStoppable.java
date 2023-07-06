package course.concurrency.exams.auction;

public abstract class AbstractAuctionStoppable implements AuctionStoppable {

    protected static final Bid START_BID = new Bid(null, null, 0L);

    protected boolean isChallengerBid(Bid bid, Bid latestBid) {
        return bid.getPrice() > latestBid.getPrice();
    }
}
