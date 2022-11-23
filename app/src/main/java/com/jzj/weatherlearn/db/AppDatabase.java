package com.jzj.weatherlearn.db;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.jzj.weatherlearn.global.App;
import com.jzj.weatherlearn.model.City;

@Database(entities = {City.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "Info.db";

    private static AppDatabase databaseInstance;

    public static synchronized AppDatabase getInstance() {
        if (databaseInstance == null) {
            databaseInstance = Room.databaseBuilder(App.context, AppDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return databaseInstance;
    }

    public abstract CityDao cityDao();

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}
