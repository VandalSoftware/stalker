package com.vandalsoftware.test;

/**
 * @author Jonathan Le
 */
public abstract class CombustionEngine implements Engine {
    private boolean running;

    protected abstract boolean run();

    @Override
    public void start() {
        running = run();
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
