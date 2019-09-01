package com.paul_karanu.logistiks.Data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.paul_karanu.logistiks.Data.Food.Food;
import com.paul_karanu.logistiks.Data.Food.FoodDao;
import com.paul_karanu.logistiks.Data.Profile.Profile;
import com.paul_karanu.logistiks.Data.Profile.ProfileDao;

@Database(entities = {Profile.class, Food.class}, version = 3)
public abstract class FoodieDB extends RoomDatabase {
    public abstract ProfileDao profileDao();
    public abstract FoodDao foodDao();
}
