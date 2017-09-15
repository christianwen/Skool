package com.example.tiena.amsconnection.item;

/**
 * Created by tiena on 15/09/2017.
 */

public class ItemMore {
    private String itemName;
    private int itemIcon;
    public ItemMore(String itemName, int itemIcon){
        this.itemName = itemName;
        this.itemIcon = itemIcon;
    }


    public String getItemName() {
        return itemName;
    }

    public int getItemIcon() {
        return itemIcon;
    }
}
