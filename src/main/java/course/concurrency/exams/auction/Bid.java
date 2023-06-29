package course.concurrency.exams.auction;

import java.util.Objects;

public class Bid {
    private Long id;
    private Long participantId;
    private Long price;

    public Bid(Long id, Long participantId, Long price) {
        this.id = id;
        this.participantId = participantId;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public Long getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bid bid = (Bid) o;

        if (!Objects.equals(id, bid.id)) return false;
        if (!Objects.equals(participantId, bid.participantId)) return false;
        return Objects.equals(price, bid.price);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (participantId != null ? participantId.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        return result;
    }
}
