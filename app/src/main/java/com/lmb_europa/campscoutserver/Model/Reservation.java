package com.lmb_europa.campscoutserver.Model;

/**
 * Created by AleksandraPC on 13-Mar-18.
 */

public class Reservation {

    private String SpotId;
    private String SpotNum;
    private String Price;
    private String Discount;

    public Reservation() {
    }

    public Reservation(String spotId, String spotNum, String price, String discount) {
        SpotId = spotId;
        SpotNum = spotNum;
        Price = price;
        Discount = discount;
    }

    public String getSpotId() {
        return SpotId;
    }

    public void setSpotId(String spotId) {
        SpotId = spotId;
    }

    public String getSpotNum() {
        return SpotNum;
    }

    public void setSpotNum(String spotNum) {
        SpotNum = spotNum;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
