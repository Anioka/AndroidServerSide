package com.lmb_europa.campscoutserver.Model;

/**
 * Created by AleksandraPC on 12-Mar-18.
 */

public class Spots {

    private String Image;
    private String Number;
    private String Details;
    private String Price;
    private String Discount;

    public Spots() {
    }

    public Spots(String image, String number, String details, String price, String discount) {
        Image = image;
        Number = number;
        Details = details;
        Price = price;
        Discount = discount;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
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
