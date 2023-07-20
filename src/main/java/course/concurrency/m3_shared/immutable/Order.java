package course.concurrency.m3_shared.immutable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Order {

    public enum Status {NEW, IN_PROGRESS, DELIVERED}

    private final Long id;
    private final List<Item> items;
    private final PaymentInfo paymentInfo;
    private final boolean isPacked;
    private final Status status;

    private Order(Long id, List<Item> items, PaymentInfo paymentInfo, boolean isPacked, Status status) {
        this.id = id;
        this.items = new ArrayList<>(items); // OK if Item is immutable
        this.paymentInfo = paymentInfo;
        this.isPacked = isPacked;
        this.status = status;
    }

    public synchronized boolean checkStatus() {
        if (!items.isEmpty() && paymentInfo != null && isPacked) {
            return true;
        }
        return false;
    }

    public Long getId() {
        return id;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo; // if PaymentInfo is not immutable, a copy of this.paymentInfo should be returned
    }

    public Order paid(PaymentInfo paymentInfo) {
        return new Order(id, items, paymentInfo, isPacked, status);
    }

    public boolean isPacked() {
        return isPacked;
    }

    public Order packed() {
        return new Order(id, items, paymentInfo, true, Status.IN_PROGRESS);
    }

    public Status getStatus() {
        return status;
    }

    public Order delivered() {
        return new Order(id, items, paymentInfo, isPacked, Status.DELIVERED);
    }

    public static Order newOrder(Long id, List<Item> items) {
        return new Order(id, items, null, false, Status.NEW);
    }

}
