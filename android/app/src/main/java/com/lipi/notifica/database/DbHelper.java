package com.lipi.notifica.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// The SQLite database handler class
public class DbHelper extends SQLiteOpenHelper {

    // Database name and version
    public static final String DB_NAME = "Notifica.db";
    public static final int DB_VERSION = 1;

    // Create the helper object
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Create all tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(new Organization().getCreateTableSql());
        db.execSQL(new Department().getCreateTableSql());
        db.execSQL(new Subject().getCreateTableSql());
        db.execSQL(new Teacher().getCreateTableSql());
        db.execSQL(new PClass().getCreateTableSql());
        db.execSQL(new PGroup().getCreateTableSql());
        db.execSQL(new Student().getCreateTableSql());
        db.execSQL(new User().getCreateTableSql());
    }

    // Destroy and re-create all tables
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(new Organization().getDestroyTableSql());
        db.execSQL(new Department().getDestroyTableSql());
        db.execSQL(new Subject().getDestroyTableSql());
        db.execSQL(new Teacher().getDestroyTableSql());
        db.execSQL(new PClass().getDestroyTableSql());
        db.execSQL(new PGroup().getDestroyTableSql());
        db.execSQL(new Student().getDestroyTableSql());
        db.execSQL(new User().getDestroyTableSql());
        onCreate(db);
    }
}
