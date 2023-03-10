package com.tea.counter.model;

public class OrderModel {

    private  String OrderDate;
    boolean visibility;
    private String OrderTitle;
    private String OrderTime;
    private String OrderPrice;
    private StringBuilder OrderDetails;
    private String ImageUrl;
    private String AdditionalComment ;
    private String audioUrl ;

    private boolean isAudio=false;

    private boolean isPlayedOnce = false;


    public OrderModel(String orderTitle, String orderTime, String orderPrice, StringBuilder orderDetails, String imageUrl, String orderDate) {
        OrderTitle = orderTitle;
        OrderTime = orderTime;
        OrderPrice = orderPrice;
        OrderDetails = orderDetails;
        ImageUrl = imageUrl;
        OrderDate = orderDate;
        this.visibility = false;
    }



    public OrderModel(String customerName, StringBuilder customItemList, String orderTime, String additionalComment, String AudioUrl) {
        OrderTitle = customerName;
        OrderDetails = customItemList;
        OrderTime = orderTime;
        AdditionalComment = additionalComment;
        audioUrl = AudioUrl;
    }

    public boolean isPlayedOnce() {
        return isPlayedOnce;
    }

    public void setPlayedOnce(boolean playedOnce) {
        isPlayedOnce = playedOnce;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getAdditionalComment() {
        return AdditionalComment;
    }

    public void setAdditionalComment(String additionalComment) {
        this.AdditionalComment = additionalComment;
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

    public boolean isAudio() {
        return isAudio;
    }

    public void setAudio(boolean audio) {
        isAudio = audio;
    }
}
