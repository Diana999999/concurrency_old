package course.concurrency.m3_shared.collections;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RestaurantService {

    private final Map<String, Restaurant> restaurantMap = new ConcurrentHashMap<>() {{
        put("A", new Restaurant("A"));
        put("B", new Restaurant("B"));
        put("C", new Restaurant("C"));
    }};

    private final Map<String, Integer> stat = new ConcurrentHashMap<>();

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return restaurantMap.get(restaurantName);
    }

    public void addToStat(String restaurantName) {
        stat.compute(restaurantName, (k, v) -> (v == null) ? 1 : v + 1);
    }

    public Set<String> printStat() {
        final Set<String> statsSet = new HashSet<>();
        for (Map.Entry<String, Integer> entry : stat.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            String pair = key + " - " + value;
            statsSet.add(pair);
        }
        return statsSet;
    }
}
