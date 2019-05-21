package com.example.newmarket.Model;

public class Users {

    private static String Email;
    private static String Password;
    private static String Image;
    private static String Address;
    private static String Phone;
    private static String Firstname;
    private static String Lastname;
    private String Nickname;

    public Users(){}

    public Users(String email, String password, String image, String address, String phone, String firstname, String lastname, String nickname) {
        Email = email;
        Password = password;
        Image = image;
        Address = address;
        Phone = phone;
        Firstname = firstname;
        Lastname = lastname;
        Nickname = nickname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getFirstname() {
        return Firstname;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public String getNickname() {
        if(Nickname.equals(""))
            return "";
        else{return Nickname;}

    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }
}
