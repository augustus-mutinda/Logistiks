package com.paul_karanu.logistiks.Data.Models;

import com.paul_karanu.logistiks.Utilities.Constants;

import java.util.Date;

public class FoodieMessage {
    private String Message;
    private Date DateCreated;
    private String MessageState;
    private String PrimaryID;

    public FoodieMessage() {
        Message = "";
        DateCreated = null;
        MessageState = Constants.MESSAGEUNSENT;
        PrimaryID = "";
    }

    public FoodieMessage(String message, Date dateCreated, String messageState, String primaryID) {
        Message = message;
        DateCreated = dateCreated;
        MessageState = messageState;
        PrimaryID = primaryID;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Date getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        DateCreated = dateCreated;
    }

    public String getMessageState() {
        return MessageState;
    }

    public void setMessageState(String messageState) {
        MessageState = messageState;
    }

    public String getPrimaryID() {
        return PrimaryID;
    }

    public void setPrimaryID(String primaryID) {
        PrimaryID = primaryID;
    }

}
