package com.vandalsoftware;

/**
 * @author Jonathan Le
 */
public abstract class HeatEngine implements Engine {
    private boolean running;

    @Override
    public void run() {
        induce();
        compress();
        ignite();
        output();
        running = true;
    }

    protected abstract void induce();
    protected abstract void compress();
    protected abstract void ignite();
    protected abstract void output();

    @Override
    public boolean isRunning() {
        return running;
    }
}
