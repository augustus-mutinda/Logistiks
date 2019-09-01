package com.paul_karanu.logistiks.Data.Models;

import java.util.Date;

public class CartItem {
    private String FoodName;
    private String FoodID;
    private String SellerID;
    private String BuyerID;
    private Date DateCreated;
    private String Price;
    private String Delivery;
    private String BuyerLocation;
    private String SellerLocation;
    private String Picture;
    private String CartID;

    public CartItem() {
        FoodName = "";
        FoodID = "";
        DateCreated = null;
        SellerID = "";
        BuyerID = "";
        Price = "";
        Delivery = "";
        BuyerLocation = "";
        SellerLocation = "";
        Picture = "";
        CartID = "";
    }

    public CartItem(String foodName, String foodID, Date dateCreated, String price, String delivery, String buyerLocation, String sellerLocation, String picture, String sellerID, String buyerID, String cartID) {
        FoodName = foodName;
        FoodID = foodID;
        Price = price;
        SellerID = sellerID;
        BuyerID = buyerID;
        DateCreated = dateCreated;
        Delivery = delivery;
        BuyerLocation = buyerLocation;
        SellerLocation = sellerLocation;
        Picture = picture;
        CartID = cartID;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public String getFoodID() {
        return FoodID;
    }

    public void setFoodID(String foodID) {
        FoodID = foodID;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDelivery() {
        return Delivery;
    }

    public void setDelivery(String delivery) {
        Delivery = delivery;
    }

    public String getBuyerLocation() {
        return BuyerLocation;
    }

    public void setBuyerLocation(String buyerLocation) {
        BuyerLocation = buyerLocation;
    }

    public String getSellerLocation() {
        return SellerLocation;
    }

    public void setSellerLocation(String sellerLocation) {
        SellerLocation = sellerLocation;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public Date getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        DateCreated = dateCreated;
    }

    public String getSellerID() {
        return SellerID;
    }

    public void setSellerID(String sellerID) {
        SellerID = sellerID;
    }

    public String getBuyerID() {
        return BuyerID;
    }

    public void setBuyerID(String buyerID) {
        BuyerID = buyerID;
    }

    public String getCartID() {
        return CartID;
    }

    public void setCartID(String cartID) {
        CartID = cartID;
    }
}
