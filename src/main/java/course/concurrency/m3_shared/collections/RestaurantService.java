package course.concurrency.m3_shared.collections;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

public class RestaurantService {

    private final Map<String, Restaurant> restaurantMap = new ConcurrentHashMap<>() {{
        put("A", new Restaurant("A"));
        put("B", new Restaurant("B"));
        put("C", new Restaurant("C"));
    }};

    private final Map<String, Long> stat = new ConcurrentHashMap<>() {{
        restaurantMap.keySet().forEach(name -> put(name, 0L));
    }};

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return restaurantMap.get(restaurantName);
    }

    public void addToStat(String restaurantName) {
        stat.computeIfPresent(restaurantName, (name, counter) -> ++counter);
    }

    public Set<String> printStat() {
        return stat.entrySet().stream()
                .map(entry -> entry.getKey() + " - " + entry.getValue())
                .collect(toSet());
    }
}
