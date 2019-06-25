package com.example.newmarket.Model;

public class Products {
    private String Product_Name;
    private String Description;
    private String Price;
    private String Vendor;
    private String Vendor_Phone;
    private String Image;
    private String Category;
    private String pid;
    private String Date;
    private String Time;
    private String Discount;

    public Products() {
    }

    public Products(String product_Name, String description, String price, String vendor, String vendor_Phone, String image, String category, String pid, String date, String time, String discount) {
        Product_Name = product_Name;
        Description = description;
        Price = price;
        Vendor = vendor;
        Vendor_Phone = vendor_Phone;
        Image = image;
        Category = category;
        this.pid = pid;
        Date = date;
        Time = time;
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

    public String getVendor_Phone() {
        return Vendor_Phone;
    }

    public void setVendor_Phone(String vendor_Phone) {
        Vendor_Phone = vendor_Phone;
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

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
