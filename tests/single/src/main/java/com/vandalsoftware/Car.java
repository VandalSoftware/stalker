package com.vandalsoftware;

/**
 * @author Jonathan Le
 */
public class Car {
    private Engine engine;

    public Car() {
        engine = new CombustionEngine();
    }

    public static void main(String[] args) {
        new Car().start();
    }

    public void start() {
        engine.run();
    }

    public boolean isRunning() {
        return engine.isRunning();
    }
}
