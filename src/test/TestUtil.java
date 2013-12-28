package test;

import static org.junit.Assert.*;

import java.util.Collection;

/** Globally useful utility functions for testing. */
public class TestUtil {
    public static <T> void assertSameElements(Collection<T> expected, Collection<T> actual) {
        assertTrue(expected.containsAll(actual));
        assertTrue(actual.containsAll(expected));
    }
}
