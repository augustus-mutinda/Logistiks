package com.paul_karanu.logistiks.Data.Models;

import com.paul_karanu.logistiks.Utilities.Constants;

import java.util.Date;

public class Transaction {
    private String TransactionID;
    private String SellerID;
    private String BuyerID;
    private String FoodID;
    private String OrderID;
    private String BuyerProfile;
    private boolean PayWithMpesa;
    private String MpesaCode;
    private String Value;
    private String FoodName;
    private Date DateCreated;
    private String OrderState;

    public Transaction() {
        TransactionID = "";
        FoodID = "";
        OrderID = "";
        BuyerID ="";
        SellerID = "";
        Value = "";
        BuyerProfile = "";
        PayWithMpesa = false;
        MpesaCode = "";
        FoodName = "";
        DateCreated = null;
        OrderState = Constants.ORDERREGISTERED;
    }

    public Transaction(String transactionID,String value,  String buyerProfile, String sellerID, String buyerID, String foodID, String orderID, boolean payWithMpesa, String mpesaCode, String foodName, Date dateCreated, String orderState) {
        TransactionID = transactionID;
        SellerID = sellerID;
        BuyerID = buyerID;
        FoodID = foodID;
        BuyerProfile = buyerProfile;
        OrderID = orderID;
        Value = value;
        PayWithMpesa = payWithMpesa;
        MpesaCode = mpesaCode;
        FoodName = foodName;
        DateCreated = dateCreated;
        OrderState = orderState;
    }

    public String getTransactionID() {
        return TransactionID;
    }

    public void setTransactionID(String transactionID) {
        TransactionID = transactionID;
    }

    public String getFoodID() {
        return FoodID;
    }

    public void setFoodID(String foodID) {
        FoodID = foodID;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public boolean isPayWithMpesa() {
        return PayWithMpesa;
    }

    public void setPayWithMpesa(boolean payWithMpesa) {
        PayWithMpesa = payWithMpesa;
    }

    public String getMpesaCode() {
        return MpesaCode;
    }

    public void setMpesaCode(String mpesaCode) {
        MpesaCode = mpesaCode;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public Date getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        DateCreated = dateCreated;
    }

    public String getOrderState() {
        return OrderState;
    }

    public void setOrderState(String orderState) {
        OrderState = orderState;
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

    public String getBuyerProfile() {
        return BuyerProfile;
    }

    public void setBuyerProfile(String buyerProfile) {
        BuyerProfile = buyerProfile;
    }
}
