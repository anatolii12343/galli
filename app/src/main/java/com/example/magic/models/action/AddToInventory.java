package com.example.magic.models.action;

import com.example.magic.models.Item;

public class AddToInventory implements Action{

    private Item item;

    public AddToInventory(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
