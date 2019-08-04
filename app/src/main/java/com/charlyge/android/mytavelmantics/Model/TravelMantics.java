package com.charlyge.android.mytavelmantics.Model;

public class TravelMantics {
    private String dealTitle;
    private String dealBody;
    private String price;
    private String dealImageUrl;

    public TravelMantics(String dealTitle, String dealBody,String price, String dealImageUrl) {
        this.dealTitle = dealTitle;
        this.dealBody = dealBody;
        this.price =price;
        this.dealImageUrl = dealImageUrl;
    }

    public String getDealTitle() {
        return dealTitle;
    }

    public String getPrice() {
        return price;
    }

    public void setDealTitle(String dealTitle) {
        this.dealTitle = dealTitle;
    }

    public String getDealBody() {
        return dealBody;
    }

    public void setDealBody(String dealBody) {
        this.dealBody = dealBody;
    }

    public String getDealImageUrl() {
        return dealImageUrl;
    }

    public void setDealImageUrl(String dealImageUrl) {
        this.dealImageUrl = dealImageUrl;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
