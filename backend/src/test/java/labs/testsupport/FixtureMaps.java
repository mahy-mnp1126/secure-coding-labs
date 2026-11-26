package labs.testsupport;

import java.util.Map;

import org.apache.commons.collections.map.LRUMap;

// SEEDED ALERT 3 of 3 (Module 4, Task 5): a test-scoped dependency with a known
// advisory (commons-collections 3.2.1). It is used here, so it is reachable from tests.
public final class FixtureMaps {
    private FixtureMaps() {
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> smallCache() {
        return new LRUMap(16);
    }
}
