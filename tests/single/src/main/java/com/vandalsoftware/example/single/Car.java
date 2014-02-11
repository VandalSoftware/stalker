package com.vandalsoftware.example.single;

/**
 * @author Jonathan Le
 */
public class Car {
    private Engine engine;

    public Car() {
        engine = new FourStrokeEngine();
    }

    public static void main(String[] args) {
        new Car().start();
    }

    public void start() {
        engine.start();
    }

    public boolean isRunning() {
        return engine.isRunning();
    }
}
