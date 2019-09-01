package com.paul_karanu.logistiks.Data.Models;

public class NewFood {

    String name;
    String UserID;
    int price;
    String about;
    String availability;
    String picture;
    String [] categories;
    int rating;

    public NewFood() {
        this.name = "";
        this.UserID = "";
        this.price = 0;
        this.about = "";
        this.availability = "";
        this.picture = "";
        this.categories = null;
        this.rating = 5;
    }

    public NewFood(String name, String UserID, int price, String about, String availability, String picture, String[] categories, int rating) {
        this.name = name;
        this.UserID = UserID;
        this.price = price;
        this.about = about;
        this.availability = availability;
        this.picture = picture;
        this.categories = categories;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
