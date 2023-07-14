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
        currentOrders.computeIfPresent(orderId, (id, existingOrder) -> {
            var order = existingOrder;

            if (existingOrder.checkStatus()) {
                order = deliver(order);
            }

            return order.withPaymentInfo(paymentInfo);
        });
    }

    public void setPacked(long orderId) {
        currentOrders.computeIfPresent(orderId, (id, existingOrder) -> {
            var order = existingOrder;

            if (existingOrder.checkStatus()) {
                order = deliver(order);
            }

            return order.withPacked(true);
        });
    }

    private Order deliver(Order order) {
        /* ... some async task, better submitted by some executor */
        return order.withStatus(Order.Status.DELIVERED);
    }

    public boolean isDelivered(long orderId) {
        return currentOrders.get(orderId).getStatus() == Order.Status.DELIVERED;
    }
}
