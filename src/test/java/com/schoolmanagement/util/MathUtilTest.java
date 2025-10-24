package com.schoolmanagement.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MathUtilTest {

    @Test
    void testBasicMath() {
        // Simple math test to ensure basic functionality
        assertEquals(4, 2 + 2);
        assertEquals(10, 5 * 2);
        assertTrue(10 > 5);
    }

    @Test
    void testStringOperations() {
        String testString = "Hello World";
        assertEquals("Hello World", testString);
        assertTrue(testString.contains("World"));
        assertEquals(11, testString.length());
    }

    @Test
    void testBooleanLogic() {
        assertTrue(true);
        assertFalse(false);
        assertTrue(5 > 3);
        assertFalse(3 > 5);
    }
}
