package com.example.magic.models.action;

public class RunnableAction implements Action {

    private Runnable runnable;

    public RunnableAction(Runnable runnable) {
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }
}
