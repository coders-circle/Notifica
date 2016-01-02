package com.lipi.notifica.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

// Class to wrap a SQLite database table
// i.e. the Model for ORM
public class Model {
    // Every table has an '_id' field which is the primary key
    public long _id = -1;

    // Get the CREATE TABLE sql query
    public String getCreateTableSql() {
        Class myClass = this.getClass();
        Field[] fields = myClass.getFields();

        // Parse each field and get the equivalent SQLite type
        String cols = "";
        for (Field field: fields) {
            String sqlType = "";
            String typeName = field.getType().getSimpleName();

            // String maps to TEXT; long, int and boolean to INTEGER; float and double to REAL
            switch (typeName) {
                case "String":
                    sqlType = "TEXT";
                    break;
                case "int":
                case "long":
                    sqlType = "INTEGER";
                    break;
                case "boolean":
                    sqlType = "INTEGER";
                    break;
                case "float":
                case "double":
                    sqlType = "REAL";
                    break;
            }

            if (!sqlType.equals("")) {
                // separate column names by comma
                if (!cols.equals(""))
                    cols += ", ";
                // add the column
                cols += field.getName() + " " + sqlType;
                // _id is the PRIMARY KEY
                if (field.getName().equals("_id"))
                    cols += " PRIMARY KEY AUTOINCREMENT";
            }
        }
        return "CREATE TABLE " + myClass.getSimpleName() + " ("
                + cols + ")";
    }

    // Get DROP TABLE sql query
    public String getDestroyTableSql() {
        Class myClass = this.getClass();
        return "DROP TABLE IF EXISTS " + myClass.getSimpleName();
    }

    // Save (insert/update) the object as a row in the table
    public void save(SQLiteOpenHelper helper) {
        Class myClass = this.getClass();
        Field[] fields = myClass.getFields();

        ContentValues values = new ContentValues();

        // Parse each field and put values for each field
        for (Field field: fields) {

            // We won't set the _id field if it is -1
            if (!(field.getName().equals("_id")) || this._id >= 0) {
                String typeName = field.getType().getSimpleName();
                try {
                    switch (typeName) {
                        case "String":
                            values.put(field.getName(), (String) field.get(this));
                            break;
                        case "int":
                            values.put(field.getName(), field.getInt(this));
                            break;
                        case "long":
                            values.put(field.getName(), field.getLong(this));
                            break;
                        case "boolean":
                            values.put(field.getName(), field.getBoolean(this) ? 1 : 0);
                            break;
                        case "float":
                            values.put(field.getName(), field.getFloat(this));
                            break;
                        case "double":
                            values.put(field.getName(), field.getDouble(this));
                            break;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        // finally insert and in case unique fields (e.g. _id) conflicts, replace the row in the table
        SQLiteDatabase db = helper.getWritableDatabase();
        _id = db.insertWithOnConflict(myClass.getSimpleName(), null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }


    // Get list of rows from database table with given sql query
    public static <T extends Model> List<T> query(Class<T> myClass, SQLiteOpenHelper helper, String selection, String[] args, String groupBy, String having, String orderBy) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Field[] fields = myClass.getFields();
        String[] cols = new String[fields.length];

        // query
        Cursor c = db.query(myClass.getSimpleName(), null, selection, args, groupBy, having, orderBy);

        // Create object from each row in the result/cursor
        List<T> list = new ArrayList<>(c.getCount());
        c.moveToPosition(-1);
        while (c.moveToNext()) {
            try {
                T object = myClass.newInstance();

                // For each field, set the value from the cursor
                for (Field field: fields) {
                    String typeName = field.getType().getSimpleName();
                    switch (typeName) {
                        case "String":
                            field.set(object, c.getString(c.getColumnIndex(field.getName())));
                            break;
                        case "int":
                            field.setInt(object, c.getInt(c.getColumnIndex(field.getName())));
                            break;
                        case "long":
                            field.setLong(object, c.getLong(c.getColumnIndex(field.getName())));
                            break;
                        case "boolean":
                            field.setBoolean(object, c.getInt(c.getColumnIndex(field.getName())) != 0);
                            break;
                        case "float":
                            field.setFloat(object, c.getFloat(c.getColumnIndex(field.getName())));
                            break;
                        case "double":
                            field.setDouble(object, c.getDouble(c.getColumnIndex(field.getName())));
                            break;
                    }
                }

                list.add(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        c.close();
        db.close();
        return list;
    }

    public static <T extends Model> T get(Class<T> myClass, SQLiteOpenHelper helper, long id) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Field[] fields = myClass.getFields();
        String[] cols = new String[fields.length];

        // query
        Cursor c = db.query(myClass.getSimpleName(), null, "_id=?", new String[]{id+""}, null, null, null);

        // Create object from each row in the result/cursor
        c.moveToPosition(-1);
        T object = null;
        while (c.moveToNext()) {
            try {
                object = myClass.newInstance();

                // For each field, set the value from the cursor
                for (Field field: fields) {
                    String typeName = field.getType().getSimpleName();
                    switch (typeName) {
                        case "String":
                            field.set(object, c.getString(c.getColumnIndex(field.getName())));
                            break;
                        case "int":
                            field.setInt(object, c.getInt(c.getColumnIndex(field.getName())));
                            break;
                        case "long":
                            field.setLong(object, c.getLong(c.getColumnIndex(field.getName())));
                            break;
                        case "boolean":
                            field.setBoolean(object, c.getInt(c.getColumnIndex(field.getName())) != 0);
                            break;
                        case "float":
                            field.setFloat(object, c.getFloat(c.getColumnIndex(field.getName())));
                            break;
                        case "double":
                            field.setDouble(object, c.getDouble(c.getColumnIndex(field.getName())));
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        c.close();
        db.close();
        return object;
    }

    // Get the number of rows with given query
    public static int count(Class myClass, SQLiteOpenHelper helper, String selection, String[] args) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(true, myClass.getSimpleName(), new String[]{"_id"}, selection, args, null, null, null, null);
        return cursor.getCount();
    }

    // Delete everything
    public static void deleteAll(Class myClass, SQLiteOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(myClass.getSimpleName(), null, null);
        db.close();
    }

    // Delete with query
    public static void delete(Class myClass, SQLiteOpenHelper helper, String selection, String[] args) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(myClass.getSimpleName(), selection, args);
        db.close();
    }
}
