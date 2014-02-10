package com.vandalsoftware.test;

/**
 * @author Jonathan Le
 */
public class FourStrokeEngine extends CombustionEngine {
    @Override
    protected boolean run() {
        induce();
        compress();
        ignite();
        exhaust();
        return true;
    }

    protected void compress() {
        System.out.println("Compressing.");
    }

    protected void induce() {
        System.out.println("Injecting fuel.");
    }

    protected void ignite() {
        System.out.println("Boom!");
    }

    protected void exhaust() {
        System.out.println("Running!");
    }
}
