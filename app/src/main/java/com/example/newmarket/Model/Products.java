package com.example.newmarket.Model;

public class Products {
    private String Product_Name;
    private String Description;
    private String Price;
    private String Vendor;
    private String Image;
    private String Category;
    private String pid;
    private String Date;
    private String Time;
    private String Discount;

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getProduct_Name() {
        return Product_Name;
    }

    public void setProduct_Name(String product_Name) {
        Product_Name = product_Name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getVendor() {
        return Vendor;
    }

    public void setVendor(String vendor) {
        Vendor = vendor;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public Products(String product_Name, String description, String price, String vendor, String image, String category, String pid, String date, String time) {
        Product_Name = product_Name;
        Description = description;
        Price = price;
        Vendor = vendor;
        Image = image;
        Category = category;
        this.pid = pid;
        Date = date;
        Time = time;
    }

    public Products(){

    }
}
