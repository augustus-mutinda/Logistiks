package com.paul_karanu.logistiks.Data.Profile;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.paul_karanu.logistiks.Utilities.DateConverter;

import java.util.Date;

@Entity
public class Profile {

    @NonNull
    @PrimaryKey
    public String UserID;

    @ColumnInfo(name = "FirstName")
    public String FirstName;

    @ColumnInfo(name = "SecondName")
    public String SecondName;

    @ColumnInfo(name = "DateCreated")
    @TypeConverters(DateConverter.class)
    public Date dateCreated;

    @ColumnInfo(name = "PhoneNumber")
    public String phoneNumber;

    @ColumnInfo(name = "Location")
    public String Location;

    @ColumnInfo(name = "SecondaryLocation")
    public String SecondaryLocation;

    @ColumnInfo(name = "Email")
    public String Email;

    @ColumnInfo(name = "ProfilePicture")
    public  String profilePicture;

    public Profile() {
        UserID = "";
        FirstName = "";
        SecondName = "";
        this.dateCreated = null;
        this.phoneNumber = null;
        Location = "";
        SecondaryLocation = "";
        Email = "";
        profilePicture = "";
    }

    public Profile(@NonNull String userID, String firstName, String secondName, Date dateCreated, String phoneNumber, String location, String secondaryLocation, String email, String ProfilePicture) {
        UserID = userID;
        FirstName = firstName;
        SecondName = secondName;
        this.dateCreated = dateCreated;
        this.phoneNumber = phoneNumber;
        Location = location;
        SecondaryLocation = secondaryLocation;
        Email = email;
        this.profilePicture = ProfilePicture;
    }

    @NonNull
    public String getUserID() {
        return UserID;
    }

    public void setUserID(@NonNull String userID) {
        UserID = userID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getSecondName() {
        return SecondName;
    }

    public void setSecondName(String secondName) {
        SecondName = secondName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getSecondaryLocation() {
        return SecondaryLocation;
    }

    public void setSecondaryLocation(String secondaryLocation) {
        SecondaryLocation = secondaryLocation;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
