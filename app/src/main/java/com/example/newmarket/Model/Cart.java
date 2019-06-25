package com.example.newmarket.Model;

public class Cart {
    private String Total_Price;
    private String pid;
    private String Product_Name;
    private String Price;
    private String Quantity;
    private String Discount;
    private String OrderID;
    private String Vendor;
    private String Buyer_Phone_Number;
    private String Buyer_Name;
    private String Extra_Details;

    public Cart(String total_Price, String pid, String product_Name, String price, String quantity, String discount, String orderID, String vendor, String buyer_Phone_Number, String buyer_Name, String extra_Details) {
        Total_Price = total_Price;
        this.pid = pid;
        Product_Name = product_Name;
        Price = price;
        Quantity = quantity;
        Discount = discount;
        OrderID = orderID;
        Vendor = vendor;
        Buyer_Phone_Number = buyer_Phone_Number;
        Buyer_Name = buyer_Name;
        Extra_Details = extra_Details;
    }

    public Cart() {
    }

    public String getTotal_Price() {
        return Total_Price;
    }

    public void setTotal_Price(String total_Price) {
        Total_Price = total_Price;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getProduct_Name() {
        return Product_Name;
    }

    public void setProduct_Name(String product_Name) {
        Product_Name = product_Name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getVendor() {
        return Vendor;
    }

    public void setVendor(String vendor) {
        Vendor = vendor;
    }

    public String getBuyer_Phone_Number() {
        return Buyer_Phone_Number;
    }

    public void setBuyer_Phone_Number(String buyer_Phone_Number) {
        Buyer_Phone_Number = buyer_Phone_Number;
    }

    public String getBuyer_Name() {
        return Buyer_Name;
    }

    public void setBuyer_Name(String buyer_Name) {
        Buyer_Name = buyer_Name;
    }

    public String getExtra_Details() {
        return Extra_Details;
    }

    public void setExtra_Details(String extra_Details) {
        Extra_Details = extra_Details;
    }
}


