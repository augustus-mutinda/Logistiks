package com.paul_karanu.logistiks.Data.Models;

public class FoodieFirestore {

    private String UserID;
    private String FoodID;
    private String Name;
    private String About;
    private int Price;
    private String availability;
    private String Picture;
    private int Rating;

    public FoodieFirestore() {
        this.UserID = "";
        this.FoodID = "";
        this.Name = "";
        this.About = "";
        this.Price = 0;
        this.availability = "";
        this.Picture = "";
        this.Rating = 0;
    }

    public FoodieFirestore(String userID, String FoodID, String name, String about, int price, String availability, String picture, int Rating) {
        this.UserID = userID;
        this.FoodID  = FoodID;
        this.Name = name;
        this.About = about;
        this.Price = price;
        this.availability = availability;
        this.Picture = picture;
        this.Rating = Rating;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getFoodID() {
        return FoodID;
    }

    public void setFoodID(String foodID) {
        FoodID = foodID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAbout() {
        return About;
    }

    public void setAbout(String about) {
        About = about;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public int getRating() {
        return Rating;
    }

    public void setRating(int rating) {
        Rating = rating;
    }
}
