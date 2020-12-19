package com.example.filledexpandablerecyclerviewtutorial;

public class ItemObject{
    public String name, itemPrice, itemCategory;
    Boolean isItemSelected = false;

    public ItemObject(String itemCategory, String name, String itemPrice) {
        this.itemCategory = itemCategory;
        this.name = name;
        this.itemPrice = itemPrice;
    }

    public String getName() {
        return name;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemSelected(Boolean itemSelected) {
        isItemSelected = itemSelected;
    }

}

