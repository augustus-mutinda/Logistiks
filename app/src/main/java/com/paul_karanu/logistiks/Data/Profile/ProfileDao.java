package com.paul_karanu.logistiks.Data.Profile;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ProfileDao {

    @Query("SELECT * FROM Profile")
    List<Profile> getProfile();

    @Insert
    void insertProfile(Profile profile);

    @Update
    void updateProfile(Profile profile);

    @Delete
    void deleteProfile(Profile profile);
}
