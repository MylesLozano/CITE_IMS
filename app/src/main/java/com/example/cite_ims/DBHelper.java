package com.example.cite_ims;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventoryManagement.db";
    private static final int DATABASE_VERSION = 1;

    // User Table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    protected static final String COLUMN_PASSWORD = "password";
    protected static final String COLUMN_ROLE = "role";

    // Inventory Table
    private static final String TABLE_INVENTORY = "inventory";
    private static final String COLUMN_ITEM_ID = "id";
    private static final String COLUMN_ITEM_NAME = "name";
    private static final String COLUMN_ITEM_DESCRIPTION = "description";
    private static final String COLUMN_ITEM_QUANTITY = "quantity";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_ROLE + " TEXT)");

        // Create Inventory Table
        db.execSQL("CREATE TABLE " + TABLE_INVENTORY + " (" +
                COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM_NAME + " TEXT, " +
                COLUMN_ITEM_DESCRIPTION + " TEXT, " +
                COLUMN_ITEM_QUANTITY + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    // Add User
    public long addUser(String username, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);
        return db.insert(TABLE_USERS, null, values);
    }

    // Authenticate User (Login)
    public boolean authenticateUser(String username, String password, String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + " = ? AND " +
                COLUMN_PASSWORD + " = ? AND " +
                COLUMN_ROLE + " = ?", new String[]{username, password, role});
        boolean isAuthenticated = cursor.getCount() > 0;
        cursor.close();
        return isAuthenticated;
    }

    // Fetch All Users
    public ArrayList<User> getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        ArrayList<User> userList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String username = cursor.getString(1);
                String role = cursor.getString(3); // Password is excluded for privacy
                userList.add(new User(id, username, role));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }

    // Add Inventory Item
    public long addInventoryItem(String name, String description, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, name);
        values.put(COLUMN_ITEM_DESCRIPTION, description);
        values.put(COLUMN_ITEM_QUANTITY, quantity);
        return db.insert(TABLE_INVENTORY, null, values);
    }

    // Fetch All Inventory Items
    public ArrayList<InventoryItem> getAllInventoryItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_INVENTORY, null);
        ArrayList<InventoryItem> itemList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String description = cursor.getString(2);
                int quantity = cursor.getInt(3);
                itemList.add(new InventoryItem(id, name, description, quantity));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    // Get Inventory Count
    public int getInventoryCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_INVENTORY, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Get Total Quantity
    public int getInventoryTotalQuantity() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_ITEM_QUANTITY + ") FROM " + TABLE_INVENTORY, null);
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    // Fetch user by username
    public Cursor getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
    }

}
