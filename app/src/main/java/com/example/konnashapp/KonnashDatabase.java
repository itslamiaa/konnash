package com.example.konnashapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class KonnashDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Konnash_db";   // this is the database
    private static final int DB_VERSION = 4;  // this changes when there has been an "altering" in the db

    public KonnashDatabase(@Nullable Context context) {  // this is a constructor method
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {  // this method is to create the db

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
    // and this one is to upgrade the db's version it is called when android detects the db version changed
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Category");
        db.execSQL("DROP TABLE IF EXISTS Customer");
        db.execSQL("DROP TABLE IF EXISTS CustomerCategory");
        db.execSQL("DROP TABLE IF EXISTS Transaction_table");
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    // category methods
    // method to add (insert) a category into the db
    public long insertCategory(String name, String color) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();  // this is called to store data before inserting it into the db
        values.put("name", name);
        values.put("color", color);

        return db.insert("Category", null, values);
    }

    // method to retrieve the categories from the db (fetching)
    public Cursor getAllCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Category", null);  // using rawQuery gives full control to writing the sql code
    }

    public int getClientCountByCategory(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // cursor is a pointer that goes thru the db one row at a time and returns the data
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM CustomerCategory WHERE category_id = ?",
                new String[]{String.valueOf(categoryId)});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
    public Cursor getCategoriesByCustomerId(int customerId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT Category.id, Category.name, Category.color " +
                        "FROM Category " +
                        "INNER JOIN CustomerCategory " +
                        "ON Category.id = CustomerCategory.category_id " +
                        "WHERE CustomerCategory.customer_id = ?",
                new String[]{String.valueOf(customerId)}
        );
    }

    public void assignCategoryToCustomer(int customerId, int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("customer_id", customerId);
        values.put("category_id", categoryId);

        db.insert("CustomerCategory", null, values);
    }

    public void updateCategory(int id, String name, String color) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("color", color);

        db.update("Category", values, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteCategory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("CustomerCategory", "category_id=?", new String[]{String.valueOf(id)});
        db.delete("Category", "id=?", new String[]{String.valueOf(id)});
    }

    // customer methods
    // insert customer into db

    public long insertCustomer(String name, String phone, String address, String city, String country) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("phone", phone != null ? phone : "");
        values.put("address", address != null ? address : "");
        values.put("city", city != null ? city : "");
        values.put("country", country != null ? country : "");

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
    public Cursor getCustomerById(int id) {
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
    public void deleteCustomer(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 1. delete relations first (important for many-to-many)
        db.delete("CustomerCategory", "customer_id=?", new String[]{String.valueOf(id)});

        // 2. delete customer
        db.delete("Customer", "id=?", new String[]{String.valueOf(id)});
    }
// ── ADD THESE 3 METHODS TO KonnashDatabase.java ──────────────────────

    // 1. Get all customers
    public Cursor getAllCustomers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Customer", null);
    }

    // 2. Get total amount by customer and transaction type (took / gave)
    public double getTotalByCustomerAndType(int customerId, String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(amount) FROM Transaction_table WHERE expression = ? AND type = ?",
                new String[]{String.valueOf(customerId), type}
        );
        double total = 0;
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    // 3. Get all transactions for a specific customer
    public Cursor getTransactionsByCustomer(int customerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM Transaction_table WHERE expression = ? ORDER BY date DESC",
                new String[]{String.valueOf(customerId)}
        );
    }
}

