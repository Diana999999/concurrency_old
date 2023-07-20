package course.concurrency.m3_shared.immutable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderService {

    private final Map<Long, Order> currentOrders = new ConcurrentHashMap<>();
    private long nextId = 0L;

    private synchronized long nextId() {
        return nextId++;
    }

    public synchronized long createOrder(List<Item> items) {
        long id = nextId();
        Order order = Order.newOrder(id, items);
        currentOrders.put(id, order);
        return id;
    }

    public void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        final Order order =
            currentOrders.computeIfPresent(orderId, (id, existingOrder) -> existingOrder.paid(paymentInfo));

        if (order != null && order.checkStatus()) deliver(order);
    }

    public void setPacked(long orderId) {
        final Order order = currentOrders.computeIfPresent(orderId, (id, existingOrder) -> existingOrder.packed());

        if (order != null && order.checkStatus()) deliver(order);
    }

    private void deliver(Order order) {
        /* ... some async task, better submitted by some executor */
        currentOrders.computeIfPresent(
            order.getId(),
            (id, existingOrder) -> existingOrder.delivered()
        );
    }

    public boolean isDelivered(long orderId) {
        return currentOrders.get(orderId).getStatus() == Order.Status.DELIVERED;
    }
}
