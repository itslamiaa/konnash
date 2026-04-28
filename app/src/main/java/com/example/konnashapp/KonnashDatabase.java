package com.example.konnashapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class KonnashDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Konnash_db";
    private static final int DB_VERSION = 4;

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

        // the relation between customer and category (many to many)
        db.execSQL(
                "CREATE TABLE CustomerCategory (" +
                        "customer_id INTEGER, " +
                        "category_id INTEGER, " +
                        "PRIMARY KEY(customer_id, category_id))"
        );

        // transactions table
        db.execSQL(
                "CREATE TABLE Transaction_table (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "type TEXT, " +
                        "amount REAL, " +
                        "expression TEXT, " +
                        "note TEXT, " +
                        "date TEXT)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Category");
        db.execSQL("DROP TABLE IF EXISTS Customer");
        db.execSQL("DROP TABLE IF EXISTS CustomerCategory");
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS Transaction_table");
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

    // customer methods
    // insert customer into db

    public long insertCustomer(String name,String phone, String address,String city,String country){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("phone",phone != null ? phone :"");
        values.put("address",address != null ? address:"");
        values.put("city",city != null ? city:"");
        values.put("country",country !=null ?country:"");

        String fullAddress = "";

        if (address != null && !address.isEmpty())
            fullAddress += address;

        if (city != null && !city.isEmpty())
            fullAddress += (fullAddress.isEmpty() ? "" : ", ") + city;

        if (country != null && !country.isEmpty())
            fullAddress += (fullAddress.isEmpty() ? "" : ", ") + country;

        values.put("full_address", fullAddress);

        return db.insert("Customer", null, values);
    }
    // method to retrieve customer data from db and display it in the profile
    public Cursor getCustomerById(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM Customer WHERE id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // inserer solde by amani

    public long insertTransaction(String type, double amount, String expression, String note, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", type);
        values.put("amount", amount);
        values.put("expression", expression);
        values.put("note", note);
        values.put("date", date);
        return db.insert("Transaction_table", null, values);
    }
}

