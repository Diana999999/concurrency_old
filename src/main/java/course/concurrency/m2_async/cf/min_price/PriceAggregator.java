package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        ExecutorService executor = Executors.newCachedThreadPool();
        return shopIds.stream().map(
                        shopId -> CompletableFuture.supplyAsync(
                                        () -> priceRetriever.getPrice(itemId, shopId), executor
                                ).completeOnTimeout(null, 2950, TimeUnit.MILLISECONDS)
                                .handle((result, ex) -> {
                                    if (ex != null) {
                                        return null;
                                    } else {
                                        return result;
                                    }
                                })
                ).collect(Collectors.toList()).stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .min(Double::compareTo)
                .orElse(Double.NaN);
    }
}
