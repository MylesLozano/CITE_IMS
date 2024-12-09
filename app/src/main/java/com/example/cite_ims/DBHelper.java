package com.example.cite_ims;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import javax.crypto.SecretKey;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "inventory.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_USERNAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_ROLE = "role";

    public static final String TABLE_INVENTORY = "inventory";
    public static final String COLUMN_ITEM_ID = "item_id";
    public static final String COLUMN_ITEM_NAME = "name";
    public static final String COLUMN_ITEM_QUANTITY = "quantity";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_USERNAME + " TEXT, " +
                COLUMN_USER_PASSWORD + " TEXT, " +
                COLUMN_USER_ROLE + " TEXT)";

        String createInventoryTable = "CREATE TABLE " + TABLE_INVENTORY + " (" +
                COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM_NAME + " TEXT, " +
                COLUMN_ITEM_QUANTITY + " INTEGER)";

        db.execSQL(createUserTable);
        db.execSQL(createInventoryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    public void addUser(String username, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_USERNAME, username);
        try {
            SecretKey key = EncryptionUtils.generateKeyFromPassword(username); // Dynamic key based on username
            String encryptedPassword = EncryptionUtils.encrypt(password, key);
            contentValues.put(COLUMN_USER_PASSWORD, encryptedPassword);
        } catch (Exception e) {
            Log.e("DBHelper", "Error encrypting password", e);
        }
        contentValues.put(COLUMN_USER_ROLE, role);

        db.insert(TABLE_USERS, null, contentValues);
        db.close();
    }

    public Cursor getUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USER_USERNAME + " = ?", new String[]{username}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                int passwordIndex = cursor.getColumnIndex(COLUMN_USER_PASSWORD);
                if (passwordIndex >= 0) {
                    SecretKey key = EncryptionUtils.generateKeyFromPassword(username); // Dynamic key based on username
                    String storedPassword = cursor.getString(passwordIndex);
                    String decryptedPassword = EncryptionUtils.decrypt(storedPassword, key);
                    if (decryptedPassword.equals(password)) {
                        return cursor;
                    }
                }
            } catch (Exception e) {
                Log.e("DBHelper", "Error decrypting password", e);
            }
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public boolean updateUsername(String oldUsername, String newUsername) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_USERNAME, newUsername);

        int result = db.update(TABLE_USERS, contentValues, COLUMN_USER_USERNAME + " = ?", new String[]{oldUsername});
        db.close();
        return result > 0;
    }

    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            SecretKey key = EncryptionUtils.generateKeyFromPassword(username); // Dynamic key based on username
            String encryptedPassword = EncryptionUtils.encrypt(newPassword, key);
            contentValues.put(COLUMN_USER_PASSWORD, encryptedPassword);
        } catch (Exception e) {
            Log.e("DBHelper", "Error encrypting password", e);
        }

        int result = db.update(TABLE_USERS, contentValues, COLUMN_USER_USERNAME + " = ?", new String[]{username});
        db.close();
        return result > 0;
    }

    public boolean deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_USERS, COLUMN_USER_USERNAME + " = ?", new String[]{username});
        db.close();
        return result > 0;
    }

    public void addItem(String name, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ITEM_NAME, name);
        contentValues.put(COLUMN_ITEM_QUANTITY, quantity);

        db.insert(TABLE_INVENTORY, null, contentValues);
        db.close();
    }

    public Cursor getAllItemsCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_INVENTORY, null);
    }

    public List<InventoryItem> getAllItems() {
        List<InventoryItem> items = new ArrayList<>();
        Cursor cursor = getAllItemsCursor();
        if (cursor.moveToFirst()) {
            do {
                int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_QUANTITY));
                items.add(new InventoryItem(itemId, name, quantity));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public void updateItem(int itemId, String name, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ITEM_NAME, name);
        contentValues.put(COLUMN_ITEM_QUANTITY, quantity);

        db.update(TABLE_INVENTORY, contentValues, COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(itemId)});
        db.close();
    }

    public void deleteItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INVENTORY, COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(itemId)});
        db.close();
    }

    public boolean checkUserExists(String username, String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_USERNAME + " = ? AND " + COLUMN_USER_ROLE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, role});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return !exists;
    }
}
