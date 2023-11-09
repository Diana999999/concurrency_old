package course.concurrency.m3_shared.collections;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RestaurantService {

    private Map<String, Restaurant> restaurantMap = new ConcurrentHashMap<>() {{
        put("A", new Restaurant("A"));
        put("B", new Restaurant("B"));
        put("C", new Restaurant("C"));
    }};

    private ConcurrentHashMap<String, Integer> stat = new ConcurrentHashMap<>() {{
        put("A", 0);
        put("B", 0);
        put("C", 0);
    }};

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return restaurantMap.get(restaurantName);
    }

    public void addToStat(String restaurantName) {
        stat.computeIfPresent(restaurantName, (key, value) -> {
            value += 1;
            return value;
        });
    }

    public Set<String> printStat() {
        HashSet<String> strings = new HashSet<>();
        strings.add("A - " + stat.get("A"));
        strings.add("B - " + stat.get("B"));
        strings.add("C - " + stat.get("C"));
        return strings;
    }
}
