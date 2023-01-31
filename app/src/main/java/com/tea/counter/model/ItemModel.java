package com.tea.counter.model;

public class ItemModel {
    private int Id;
    private String ItemName;
    private String Price;

    public ItemModel(int id, String itemName, String itemPrice) {
        Id = id;
        ItemName = itemName;
        Price = itemPrice;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }



}
