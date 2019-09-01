package com.paul_karanu.logistiks.Data.Models;

import com.paul_karanu.logistiks.Utilities.Constants;

import java.util.Date;

public class FoodieChat {
    private String PrimaryID;
    private String SecondaryID;
    private String PrimaryName;
    private String SecondaryName;
    private String LastMessage;
    private String Image;
    private Date DateCreated;
    private Date DateModified;
    private String MessageState;
    private String ChatID;
    private String FoodID;

    public FoodieChat() {
        PrimaryID = "";
        SecondaryID = "";
        SecondaryName = "";
        LastMessage = "";
        Image = "";
        DateCreated = null;
        DateModified = null;
        MessageState = Constants.MESSAGEUNSENT;
        ChatID = "";
        FoodID = "";
        PrimaryName = "";
    }

    public FoodieChat(String primaryID, String secondaryID, String SecondaryName, String lastMessage, String image, Date dateCreated, Date dateModified, String MessageState, String ChatID, String FoodID, String PrimaryName) {
        PrimaryID = primaryID;
        SecondaryID = secondaryID;
        this.SecondaryName = SecondaryName;
        LastMessage = lastMessage;
        Image = image;
        DateCreated = dateCreated;
        DateModified = dateModified;
        this.MessageState = MessageState;
        this.ChatID = ChatID;
        this.FoodID = FoodID;
        this.PrimaryName = PrimaryName;
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

    public String getSecondaryName() {
        return SecondaryName;
    }

    public void setSecondaryName(String secondaryName) {
        SecondaryName = secondaryName;
    }

    public String getLastMessage() {
        return LastMessage;
    }

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
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

    public String getMessageState() {
        return MessageState;
    }

    public void setMessageState(String messageState) {
        MessageState = messageState;
    }

    public String getChatID() {
        return ChatID;
    }

    public void setChatID(String chatID) {
        ChatID = chatID;
    }

    public String getFoodID() {
        return FoodID;
    }

    public void setFoodID(String foodID) {
        FoodID = foodID;
    }

    public String getPrimaryName() {
        return PrimaryName;
    }

    public void setPrimaryName(String primaryName) {
        PrimaryName = primaryName;
    }
}