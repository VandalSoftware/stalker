package com.vandalsoftware;

/**
 * @author Jonathan Le
 */
public class TwoStrokeEngine extends CombustionEngine {
    @Override
    protected boolean run() {
        compress();
        exhaust();
        return true;
    }

    private void compress() {
        System.out.println("Intake, compress, ignite.");
    }

    private void exhaust() {
        System.out.println("Power!");
    }
}
