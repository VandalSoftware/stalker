package com.vandalsoftware.test

import com.vandalsoftware.test.OhvV8
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * @author Jonathan Le
 */
class OhvV8Test {
    @Test
    public void testCarStart() {
        OhvV8 engine = new OhvV8()
        engine.start()
        assertTrue engine.isRunning()
    }
}
