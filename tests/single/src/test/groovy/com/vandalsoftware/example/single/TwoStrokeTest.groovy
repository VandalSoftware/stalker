package com.vandalsoftware.example.single

import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * @author Jonathan Le
 */
class TwoStrokeTest {
    @Test
    public void testEngineStarts() {
        TwoStrokeEngine engine = new TwoStrokeEngine()
        engine.start()
        assertTrue engine.isRunning()
    }
}
