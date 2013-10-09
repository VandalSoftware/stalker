package com.vandalsoftware;

/**
 * @author Jonathan Le
 */
public abstract class HeatEngine implements Engine {
    @Override
    public void run() {
        induce();
        compress();
        ignite();
        output();
    }

    protected abstract void induce();
    protected abstract void compress();
    protected abstract void ignite();
    protected abstract void output();
}
