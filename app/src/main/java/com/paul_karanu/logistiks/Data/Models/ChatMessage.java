package com.paul_karanu.logistiks.Data.Models;

import java.util.Date;

public class ChatMessage {
    private String PrimaryID;
    private String SecondaryID;
    private String LastMessage;
    private String MessageState;
    private String FoodID;
    private String ChatID;
    private String Image;
    private String SecondaryName;
    private String PrimaryName;
    private Date DateCreated;
    private Date DateModified;

    public ChatMessage() {
    }

    public ChatMessage(String primaryID, String secondaryID, String lastMessage,
                       String messageState, String foodID, String chatID, String image,
                       String secondaryName, String primaryName, Date dateCreated, Date dateModified) {
        PrimaryID = primaryID;
        SecondaryID = secondaryID;
        LastMessage = lastMessage;
        MessageState = messageState;
        FoodID = foodID;
        ChatID = chatID;
        Image = image;
        SecondaryName = secondaryName;
        PrimaryName = primaryName;
        DateCreated = dateCreated;
        DateModified = dateModified;
    }

    public String getPrimaryID() {
        return PrimaryID;
    }

    public void setPrimaryID(String primaryID) {
        PrimaryID = primaryID;
    }

    public String getSecondaryID() {
        return SecondaryID;
    }

    public void setSecondaryID(String secondaryID) {
        SecondaryID = secondaryID;
    }

    public String getLastMessage() {
        return LastMessage;
    }

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }

    public String getMessageState() {
        return MessageState;
    }

    public void setMessageState(String messageState) {
        MessageState = messageState;
    }

    public String getFoodID() {
        return FoodID;
    }

    public void setFoodID(String foodID) {
        FoodID = foodID;
    }

    public String getChatID() {
        return ChatID;
    }

    public void setChatID(String chatID) {
        ChatID = chatID;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getSecondaryName() {
        return SecondaryName;
    }

    public void setSecondaryName(String secondaryName) {
        SecondaryName = secondaryName;
    }

    public String getPrimaryName() {
        return PrimaryName;
    }

    public void setPrimaryName(String primaryName) {
        PrimaryName = primaryName;
    }

    public Date getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        DateCreated = dateCreated;
    }

    public Date getDateModified() {
        return DateModified;
    }

    public void setDateModified(Date dateModified) {
        DateModified = dateModified;
    }
}
