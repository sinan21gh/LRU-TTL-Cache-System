import java.util.HashMap;
import java.util.Map;

public class CacheEntry<V> {
    private final V value;
    private final long expirationTime;

    public CacheEntry(V value, long timeToLiveMs) {
        this.value = value;
        this.expirationTime = System.currentTimeMillis() + timeToLiveMs;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }
    public V getValue(){
        return value;
    }

    public long getExpirationTime() {
        return expirationTime;
    }
}

