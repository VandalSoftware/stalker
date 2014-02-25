package com.vandalsoftware.example.single;

import junit.framework.TestCase;

/**
 * @author Jonathan Le
 */
public class CombustionEngineTests extends TestCase {
    public void testEngineStart() {
        final CombustionEngine combustionEngine = new FourStrokeEngine();
        combustionEngine.start();
        assertTrue(combustionEngine.isRunning());
    }
}
