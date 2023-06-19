package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.lang.Double.NaN;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();
    private static final long PRICE_RETRIEVE_TIMEOUT = 2_900L;

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    ExecutorService executorService = Executors.newCachedThreadPool();

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {

        /* ForkJoinPool по умолчанию использует уровень параллелизмы как CPU cores - 1.
        *  При этом значение core pool size по умолчанию равно уровню параллелизма.
        *  В следствии указанного настройки forkjoinpool по умолчанию не подходят для
        *  большого количество I/O задач с необходимым тайм-аутом исполнения.
        *
        *  Для того чтобы все тесты успешно завершились дефолтный forkjoinpool был скорерктирован.        *
        * */

        Executor forkJoinPool = forkJoinPool();

        List<CompletableFuture<Double>> minShopPrices = shopIds.stream()
                .map(shopId -> CompletableFuture
                        .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), forkJoinPool)
                        .exceptionally(e -> Double.NaN)
                        .completeOnTimeout(NaN, PRICE_RETRIEVE_TIMEOUT, TimeUnit.MILLISECONDS))
                .collect(Collectors.toList());

        return minShopPrices.stream()
                .map(CompletableFuture::join)
                .filter(price-> !price.isNaN())
                .min(Double::compareTo)
                .orElse(NaN);
    }

    private ForkJoinPool forkJoinPool() {

        return new ForkJoinPool(
                50,
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                null,
                false,
                50,
                60,
                1,
                null,
                60L,
                TimeUnit.SECONDS);
    }
}
