package com.vandalsoftware;

/**
 * @author Jonathan Le
 */
public class CombustionEngine extends HeatEngine {
    @Override
    protected void compress() {
        System.out.println("Compressing.");
    }

    @Override
    protected void induce() {
        System.out.println("Injecting fuel.");
    }

    @Override
    protected void ignite() {
        System.out.println("Boom!");
    }

    @Override
    protected void output() {
        System.out.println("Running!");
    }
}
