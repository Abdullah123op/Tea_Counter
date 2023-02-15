package com.tea.counter.model;

public class ItemModel {
    private int id;
    private String itemName;
    private String price;
    private String qty = "0";
    private Boolean isClick = false;
    private String totalPrice = "0";


    public ItemModel(int id, String itemName, String price) {
        this.id = id;
        this.itemName = itemName;
        this.price = price;
    }


    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Boolean getClick() {
        return isClick;
    }

    public void setClick(Boolean click) {
        isClick = click;
    }
}
