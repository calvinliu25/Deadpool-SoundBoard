package com.calvinliu.deadpoolsoundboard;

public class SoundObject {

    private String itemName;
    private Integer itemId;

    public SoundObject (String itemName, Integer itemId){

        this.itemName = itemName;
        this.itemId = itemId;
    }

    public String getItemName(){
        return itemName;
    }

    public Integer getItemId(){
        return itemId;
    }
}
