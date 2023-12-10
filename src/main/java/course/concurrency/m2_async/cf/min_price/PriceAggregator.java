package course.concurrency.m2_async.cf.min_price;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.*;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        return Arrays.stream(shopIds.stream()
                .map(shopId -> CompletableFuture
                        .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), Executors.newFixedThreadPool(8))
                        .orTimeout( 2900, TimeUnit.MILLISECONDS)
                        .exceptionally(e -> Double.NaN))
                .toArray(CompletableFuture[]::new))
                .peek(CompletableFuture::join)
                .map(completableFuture -> Double.valueOf(completableFuture.getNow(Double.NaN).toString()))
                .min(Double::compareTo).orElse(Double.NaN);
    }
}
