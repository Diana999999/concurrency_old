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

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public double getMinPrice(long itemId) {
        return Arrays.stream(shopIds.stream()
                .map(shopId -> CompletableFuture
                        .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executorService)
                        .orTimeout( 2900, TimeUnit.MILLISECONDS)
                        .exceptionally(e -> Double.NaN))
                .toArray(CompletableFuture[]::new))
                .map(CompletableFuture::join)
                        .map(String::valueOf)
                        .map(Double::valueOf)
                .min(Double::compareTo).orElse(Double.NaN);
    }
}
