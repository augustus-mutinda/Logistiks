package com.paul_karanu.logistiks.Data.Models;

import java.util.Date;

public class Order {
        private String BuyerID;
        private String BuyerName;
        private String SellerID;
        private String OrderID;
        private String FoodID;
        private String FoodName;
        private Date DateOrdered;
        private String OrderStatus;

    public Order() {
        BuyerID = "";
        BuyerName = "";
        SellerID = "";
        OrderID = "";
        FoodID = "";
        FoodName = "";
        DateOrdered = null;
        OrderStatus = "";
    }

    public Order(String buyerID, String buyerName, String sellerID, String orderID, String foodID, String foodName, Date dateOrdered, String orderStatus) {
        BuyerID = buyerID;
        BuyerName = buyerName;
        SellerID = sellerID;
        OrderID = orderID;
        FoodID = foodID;
        FoodName = foodName;
        DateOrdered = dateOrdered;
        OrderStatus = orderStatus;
    }

    public String getBuyerID() {
        return BuyerID;
    }

    public void setBuyerID(String buyerID) {
        BuyerID = buyerID;
    }

    public String getBuyerName() {
        return BuyerName;
    }

    public void setBuyerName(String buyerName) {
        BuyerName = buyerName;
    }

    public String getSellerID() {
        return SellerID;
    }

    public void setSellerID(String sellerID) {
        SellerID = sellerID;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getFoodID() {
        return FoodID;
    }

    public void setFoodID(String foodID) {
        FoodID = foodID;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public Date getDateOrdered() {
        return DateOrdered;
    }

    public void setDateOrdered(Date dateOrdered) {
        DateOrdered = dateOrdered;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }
}
