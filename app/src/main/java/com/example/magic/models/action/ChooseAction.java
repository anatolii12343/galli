package com.example.magic.models.action;

public class ChooseAction implements Action{

    private String title;

    private Runnable firstAction;

    private String firstTitle;

    private Runnable secondAction;

    private String secondTitle;

    public ChooseAction(String title, Runnable firstAction, String firstTitle, Runnable secondAction, String secondTitle) {
        this.title = title;
        this.firstAction = firstAction;
        this.firstTitle = firstTitle;
        this.secondAction = secondAction;
        this.secondTitle = secondTitle;
    }

    public Runnable getFirstAction() {
        return firstAction;
    }

    public void setFirstAction(Runnable firstAction) {
        this.firstAction = firstAction;
    }

    public String getFirstTitle() {
        return firstTitle;
    }

    public void setFirstTitle(String firstTitle) {
        this.firstTitle = firstTitle;
    }

    public Runnable getSecondAction() {
        return secondAction;
    }

    public void setSecondAction(Runnable secondAction) {
        this.secondAction = secondAction;
    }

    public String getSecondTitle() {
        return secondTitle;
    }

    public void setSecondTitle(String secondTitle) {
        this.secondTitle = secondTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
