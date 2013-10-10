package com.vandalsoftware;

/**
 * @author Jonathan Le
 */
public class Car {
    public static void main(String[] args) {
        new Car().start();
    }

    public void start() {
        new CombustionEngine().run();
    }
}
