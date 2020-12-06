package com.laundry.laundry.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseOrder {

    private Context context;
    private static DatabaseOrder databaseOrder;

    private AppDatabase database;

    private DatabaseOrder(Context context){
        this.context = context;
        database = Room.databaseBuilder(context, AppDatabase.class, "order").build();
    }

    public static synchronized DatabaseOrder getInstance(Context context){
        if (databaseOrder ==null){
            databaseOrder = new DatabaseOrder(context);
        }
        return databaseOrder;
    }

    public AppDatabase getDatabase(){
        return database;
    }
}
