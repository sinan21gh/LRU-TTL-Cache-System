import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CacheEntryTest {

    @Test
    void testValueIsStoredCorrectly() {
        CacheEntry<String> entry = new CacheEntry<>("Hello", 1000);
        assertEquals("Hello", entry.getValue(), "Stored value should match the input");
    }

    @Test
    void testNotExpiredInitially() {
        CacheEntry<String> entry = new CacheEntry<>("Data", 2000);
        assertFalse(entry.isExpired(), "Entry should not be expired immediately after creation");
    }

    @Test
    void testExpiresAfterTTL() throws InterruptedException {
        CacheEntry<String> entry = new CacheEntry<>("Temp", 500);
        Thread.sleep(600);
        assertTrue(entry.isExpired(), "Entry should expire after TTL has passed");
    }

    @Test
    void testExpirationTimeIsCalculatedCorrectly() {
        long start = System.currentTimeMillis();
        long ttl = 1000;
        CacheEntry<String> entry = new CacheEntry<>("Check", ttl);

        assertTrue(
                entry.getExpirationTime() >= start + ttl && entry.getExpirationTime() <= start + ttl + 50,
                "Expiration time should be roughly currentTime + TTL"
        );
    }

    @Test
    void testMultipleEntriesExpireIndependently() throws InterruptedException {
        CacheEntry<String> fast = new CacheEntry<>("Fast", 300);
        CacheEntry<String> slow = new CacheEntry<>("Slow", 1000);

        Thread.sleep(400);
        assertTrue(fast.isExpired(), "Fast entry should expire first");
        assertFalse(slow.isExpired(), "Slow entry should still be valid");
    }
}
