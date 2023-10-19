package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.Double.NaN;

public class PriceAggregator {

    private final ExecutorService executor;

    public PriceAggregator() {
        this.executor = Executors.newCachedThreadPool();
    }

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10L, 45L, 66L, 345L, 234L, 333L, 67L, 123L, 768L);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice() {
        final List<CompletableFuture<Double>> futures = shopIds.stream()
                .map(id ->
                        CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(), executor)
                                .exceptionally(e -> NaN)
                                .completeOnTimeout(NaN, 2990, TimeUnit.MILLISECONDS)
                                .thenApply(result -> result))
                .collect(Collectors.toList());

        return futures.stream()
                .peek(CompletableFuture::join)
                .mapToDouble(future -> future.getNow(NaN))
                .filter(Double::isFinite)
                .min()
                .orElse(NaN);
    }
}
