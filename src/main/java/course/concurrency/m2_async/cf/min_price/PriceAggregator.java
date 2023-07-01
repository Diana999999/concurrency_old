package course.concurrency.m2_async.cf.min_price;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PriceAggregator {

    private static final int MIN_PRICE_TIMEOUT = 2900;

    private ExecutorService executor = Executors.newFixedThreadPool(128);
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
        List<CompletableFuture<Double>> futures = new ArrayList<>();

        for (Long shopId : shopIds) {
            CompletableFuture<Double> future = CompletableFuture
                    .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executor)
                    .exceptionally(it -> Double.NaN)
                    .completeOnTimeout(Double.NaN, MIN_PRICE_TIMEOUT, TimeUnit.MILLISECONDS);
            futures.add(future);
        }

        CompletableFuture<Void> completableFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return completableFutures.completeOnTimeout(null, MIN_PRICE_TIMEOUT, TimeUnit.MILLISECONDS)
                .thenApply(it -> futures.stream()
                        .map(CompletableFuture::join)
                        .min(Double::compareTo)).join().orElse(Double.NaN);
    }
}
