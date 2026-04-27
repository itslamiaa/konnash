package com.example.konnashapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class KonnashDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Konnash_db";
    private static final int DB_VERSION = 2;

    public KonnashDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // category table
        db.execSQL(
                "CREATE TABLE Category (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT, " +
                        "color TEXT)"
        );
        // customer table
        db.execSQL(
                "CREATE TABLE Customer (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT, " +
                        "phone TEXT, " +
                        "address TEXT, " +
                        "city TEXT, " +
                        "country TEXT," +
                        "full_address TEXT)"
         );

        // the relation between customer and category
        db.execSQL(
                "CREATE TABLE CustomerCategory (" +
                        "customer_id INTEGER, " +
                        "category_id INTEGER, " +
                        "PRIMARY KEY(customer_id, category_id))"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Category");
        db.execSQL("DROP TABLE IF EXISTS Customer");
        db.execSQL("DROP TABLE IF EXISTS CustomerCategory");
        onCreate(db);
    }


    // category methods
    // method to add (insert) a category into the db
    public long insertCategory(String name, String color){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("color",color);

        return db.insert("Category",null,values);
    }

    // method to retrieve the categories from the db (fetching)
    public Cursor getAllCategories(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM Category",null);
    }

    public int getClientCountByCategory(int categoryId){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM CustomerCategory WHERE category_id = ?",
                new String[]{String.valueOf(categoryId)});

        int count = 0;
        if (cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
    public void assignCategoryToCustomer(int customerId, int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("customer_id", customerId);
        values.put("category_id", categoryId);

        db.insert("CustomerCategory", null, values);
    }

    public void updateCategory(int id, String name, String color){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("color",color);

        db.update("Category",values,"id=?",new String[]{String.valueOf(id)});
    }

    public void deleteCategory(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("CustomerCategory","category_id=?",new String[]{String.valueOf(id)});
        db.delete("Category","id=?",new String[]{String.valueOf(id)});
    }

    // address method (customer)
}

