package course.concurrency.m2_async.cf.min_price;

import java.util.*;
import java.util.concurrent.*;

public class PriceAggregator {


    private ExecutorService executor = Executors.newFixedThreadPool(64);
    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        // place for your code
        CompletableFuture<Double>[] features = shopIds.stream()
                .map(shopId ->
                        CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executor)
                                .completeOnTimeout(Double.NaN, 2900, TimeUnit.MILLISECONDS)
                                .handleAsync((result, ex) -> Objects.isNull(result) ? Double.NaN : result)
                ).toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(features).join();

        return Arrays.stream(features)
                .map(CompletableFuture::join)
                .min(Comparator.comparingDouble(d -> Double.isNaN(d) ? Double.POSITIVE_INFINITY : d))
                .orElse(Double.NaN);
    }

    private double httpImitate(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
            return new Random().nextDouble();
        } catch (InterruptedException ex) {
            return Double.NaN;
        }
    }
}
