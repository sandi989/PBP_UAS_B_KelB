package com.laundry.laundry.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.laundry.laundry.model.Order;


@Database(entities = {Order.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract OrderDao orderDao();
}