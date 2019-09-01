package com.paul_karanu.logistiks.Data.Food;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface FoodDao {

    @Query("SELECT * FROM Food")
    List<Food> getFoods();

    @Insert
    void insertProfile(Food food);

    @Update
    void updateProfile(Food food);

    @Delete
    void deleteProfile(Food food);
}
