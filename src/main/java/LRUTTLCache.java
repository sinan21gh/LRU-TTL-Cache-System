import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LRUTTLCache<K, V> {
    private static final long CLEANUP_INTERVAL_MS = 1000 * 5;
    private final long defaultTTLMs;

    private final Map<K, CacheEntry<V>> cacheMap;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public LRUTTLCache(int capacity, long defaultTTLMs) {
        this.defaultTTLMs = defaultTTLMs;

        Map<K, CacheEntry<V>> lruMap = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                return size() > capacity;
            }
        };

        this.cacheMap = Collections.synchronizedMap(lruMap);

        scheduler.scheduleAtFixedRate(
            this::cleanupExpiredEntries,
            CLEANUP_INTERVAL_MS,
            CLEANUP_INTERVAL_MS,
            TimeUnit.MILLISECONDS
        );
    }



    public void put(K key, V value) {
        CacheEntry<V> entry = new CacheEntry<>(value, defaultTTLMs);
        cacheMap.put(key, entry);
    }

    public V get(K key) {
        CacheEntry<V> entry = cacheMap.get(key);

        if (entry == null) {
            return null;
        }

        if (entry.isExpired()) {
            cacheMap.remove(key);
            return null;
        }


        return entry.getValue();
    }

    public int size() {
        return cacheMap.size();
    }


    private void cleanupExpiredEntries() {
        synchronized (cacheMap) {
            System.out.println("Running background cache cleanup. Size before: " + cacheMap.size());
            cacheMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
            System.out.println("Size after cleanup: " + cacheMap.size());
        }
    }

    public void shutdown() {
        scheduler.shutdown();
        System.out.println("Cache cleanup scheduler shut down.");
    }
}
