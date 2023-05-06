package com.example.magic.models.action;

public class UserMessage implements Action {

    private String text;

    private float npcCenter;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getNpcCenter() {
        return npcCenter;
    }

    public void setNpcCenter(float npcCenter) {
        this.npcCenter = npcCenter;
    }

    public UserMessage(String text, float npcCenter) {
        this.text = text;
        this.npcCenter = npcCenter;
    }
}
