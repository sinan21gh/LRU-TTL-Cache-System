import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class LRUTTLCacheTest {

    private LRUTTLCache<String, String> cache;

    @BeforeEach
    void setUp() {
        cache = new LRUTTLCache<>(3, 2000);
    }

    @AfterEach
    void tearDown() {
        cache.shutdown();
    }

    @Test
    void testPutAndGet() {
        cache.put("A", "Apple");
        assertEquals("Apple", cache.get("A"));
        assertEquals(1, cache.size());
    }

    @Test
    void testTTLExpiration() throws InterruptedException {
        cache.put("B", "Banana");
        Thread.sleep(2500);
        assertNull(cache.get("B"), "Expired item should return null");
    }

    @Test
    void testCleanupRemovesExpiredEntries() throws InterruptedException {
        cache.put("C", "Cherry");
        assertEquals(1, cache.size());

        Thread.sleep(2500);
        Thread.sleep(6000);

        assertEquals(0, cache.size(), "Expired entry should be cleaned up automatically");
    }

    @Test
    void testLRUEviction() {
        cache.put("A", "Apple");
        cache.put("B", "Banana");
        cache.put("C", "Cherry");

        cache.get("A");

        cache.put("D", "Date");

        assertNull(cache.get("B"), "Least recently used entry should be evicted");
        assertEquals(3, cache.size(), "Cache should maintain capacity limit");
    }

    @Test
    void testShutdown() {
        cache.shutdown();
        assertTrue(true, "Scheduler shutdown executed without exception");
    }
}

