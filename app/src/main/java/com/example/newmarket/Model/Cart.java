package com.example.newmarket.Model;

public class Cart {
    private String pid, Product_Name, Price, Quantity, Discount;

    public Cart() {
    }

    public Cart(String pid, String product_Name, String price, String quantity, String discount) {
        this.pid = pid;
        Product_Name = product_Name;
        Price = price;
        Quantity = quantity;
        Discount = discount;
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
}


