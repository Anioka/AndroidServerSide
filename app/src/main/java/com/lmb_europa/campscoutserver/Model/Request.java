package com.lmb_europa.campscoutserver.Model;

import java.util.List;

/**
 * Created by AleksandraPC on 13-Mar-18.
 */

public class Request {
    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private List<Reservation> spots;

    public Request() {
    }

    public Request(String phone, String name, String address, String total, List<Reservation> spots) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.spots = spots;
        this.status = "0"; //Default is 0, 0:place, 1:shipping, 2:shipped
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Reservation> getSpots() {
        return spots;
    }

    public void setSpots(List<Reservation> spots) {
        this.spots = spots;
    }
}
