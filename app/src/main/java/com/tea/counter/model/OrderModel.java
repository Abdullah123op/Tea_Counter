package com.tea.counter.model;

public class OrderModel {
    private String Time;
    private String Cup;
    private String Price;

    public OrderModel(String time, String cup, String price) {
        this.Time = time;
        this.Cup = cup;
        this.Price = price;
    }


    public String getTime() {
        return Time;
    }

    public String getCup() {
        return Cup;
    }

    public void setCup(String cup) {
        Cup = cup;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public void setTime(String time) {
        Time = time;
    }


}
