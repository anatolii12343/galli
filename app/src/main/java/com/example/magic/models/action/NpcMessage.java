package com.example.magic.models.action;

public class NpcMessage implements Action {

    private String text;

    private float x;

    private float y;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public NpcMessage(String text, float x, float y) {
        this.text = text;
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
