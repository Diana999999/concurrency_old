package course.concurrency.m3_shared.collections;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

public class RestaurantService {

    private final Map<String, Restaurant> restaurantMap = new ConcurrentHashMap<>() {{
        put("A", new Restaurant("A"));
        put("B", new Restaurant("B"));
        put("C", new Restaurant("C"));
    }};

    private final Queue<String> stat = new LinkedBlockingQueue<>();

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return restaurantMap.get(restaurantName);
    }

    public void addToStat(String restaurantName) {
        stat.add(restaurantName);
    }

    public Set<String> printStat() {
        return stat.stream()
                .collect(groupingBy(identity()))
                .entrySet().stream()
                .map(entry -> entry.getKey() + " - " + entry.getValue().size())
                .collect(toSet());
    }
}
