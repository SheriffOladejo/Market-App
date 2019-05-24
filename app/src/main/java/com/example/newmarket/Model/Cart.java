package com.example.newmarket.Model;

public class Cart {
    private String pid;
    private String Product_Name;
    private String Price;
    private String Quantity;
    private String Discount;
    private String OrderID;

    public Cart(String pid, String product_Name, String price, String quantity, String discount, String orderID) {
        this.pid = pid;
        Product_Name = product_Name;
        Price = price;
        Quantity = quantity;
        Discount = discount;
        OrderID = orderID;
    }

    public Cart() {
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
}


