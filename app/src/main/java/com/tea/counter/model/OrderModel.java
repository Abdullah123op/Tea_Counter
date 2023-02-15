package com.tea.counter.model;

public class OrderModel {

    private final String OrderDate;
    boolean visibility;
    private String OrderTitle;
    private String OrderTime;
    private String OrderPrice;
    private StringBuilder OrderDetails;
    private String ImageUrl;




    public OrderModel(String orderTitle, String orderTime, String orderPrice, StringBuilder orderDetails, String imageUrl, String orderDate) {
        OrderTitle = orderTitle;
        OrderTime = orderTime;
        OrderPrice = orderPrice;
        OrderDetails = orderDetails;
        ImageUrl = imageUrl;
        OrderDate = orderDate;
        this.visibility = false;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public StringBuilder getOrderDetails() {
        return OrderDetails;
    }

    public void setOrderDetails(StringBuilder orderDetails) {
        OrderDetails = orderDetails;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public String getOrderTitle() {
        return OrderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        OrderTitle = orderTitle;
    }

    public String getOrderTime() {
        return OrderTime;
    }

    public void setOrderTime(String orderTime) {
        OrderTime = orderTime;
    }

    public String getOrderPrice() {
        return OrderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        OrderPrice = orderPrice;
    }


}
