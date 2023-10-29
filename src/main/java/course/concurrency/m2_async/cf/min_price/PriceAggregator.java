package course.concurrency.m2_async.cf.min_price;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    private ForkJoinPool fjp = new ForkJoinPool(50);

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        List<Double> prices = new ArrayList<>();
        CompletableFuture<Object> cf = new CompletableFuture<>();

        shopIds.parallelStream()
                .forEach(
                        shopId -> {
                            cf
                                    .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), fjp)
                                    .thenApply(prices::add);
                        }
                );

        try {
            cf.get(2900, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            prices.add(Double.NaN);
        }
        return prices.stream().min(Double::compareTo).orElse(Double.NaN);
    }
}
