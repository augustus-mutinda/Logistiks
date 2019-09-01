package com.paul_karanu.logistiks.Data.Food;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Food {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "FoodID")
    String FoodID;

    @ColumnInfo(name = "SellerID")
    String SellerID;

    @ColumnInfo(name = "DateCreated")
    int DateCreated;

    @ColumnInfo(name = "Price")
    int Price;

    public Food() {
        FoodID = "";
        SellerID = "";
        DateCreated = 0;
        Price = 0;
    }

    public Food(String foodID, String sellerID, int dateCreated, int price, String[] categories) {
        FoodID = foodID;
        SellerID = sellerID;
        DateCreated = dateCreated;
        Price = price;
    }

    public String getFoodID() {
        return FoodID;
    }

    public void setFoodID(String foodID) {
        FoodID = foodID;
    }

    public String getSellerID() {
        return SellerID;
    }

    public void setSellerID(String sellerID) {
        SellerID = sellerID;
    }

    public int getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(int dateCreated) {
        DateCreated = dateCreated;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }
}
