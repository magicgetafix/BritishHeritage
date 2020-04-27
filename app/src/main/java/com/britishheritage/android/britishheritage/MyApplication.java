package com.britishheritage.android.britishheritage;

import android.app.Application;

import com.britishheritage.android.britishheritage.Database.LandmarkDatabase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        migrateDB();
    }

    public void migrateDB(){
        LandmarkDatabase database = Room.databaseBuilder(getApplicationContext(), LandmarkDatabase.class, "database-name")
                .fallbackToDestructiveMigration().build();
    }


}
