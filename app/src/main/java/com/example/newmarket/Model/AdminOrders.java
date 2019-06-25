package com.example.newmarket.Model;

public class AdminOrders {
    private String Address, City, Date, Name, Phone, State, Time, Total_Amount, Extras, PID;

    public AdminOrders(String address, String city, String date, String name, String phone, String state, String time, String total_Amount, String extras, String PID) {
        Address = address;
        City = city;
        Date = date;
        Name = name;
        Phone = phone;
        State = state;
        Time = time;
        Total_Amount = total_Amount;
        Extras = extras;
        this.PID = PID;
    }

    public AdminOrders() {
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getTotal_Amount() {
        return Total_Amount;
    }

    public void setTotal_Amount(String total_Amount) {
        Total_Amount = total_Amount;
    }

    public String getExtras() {
        return Extras;
    }

    public void setExtras(String extras) {
        Extras = extras;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }
}
