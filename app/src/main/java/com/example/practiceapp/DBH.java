package com.example.practiceapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DBH extends SQLiteOpenHelper {

    private final static String DB_Name = "Practice.db";
    // INCREMENTED VERSION TO 5: Required to trigger onUpgrade for the new 'role' column
    private final static int DB_Version = 5;

    public DBH(Context context) {
        super(context, DB_Name, null, DB_Version);
    }

    // --- TABLE & COLUMN NAMES ---
    private final static String TB_Name = "sample_table";
    private final static String TB_ID = "id";
    private final static String TB_Username = "username";
    private final static String TB_Password = "password";
    private final static String TB_Role = "role"; // RBAC Column

    private final static String TB_Book = "book_table";
    private final static String BK_ID = "book_id";
    private final static String BK_Title = "title";
    private final static String BK_Author = "author";
    private final static String BK_Image = "image_res";
    private final static String BK_Desc = "description";
    private final static String BK_Date = "date_published";
    private final static String BK_Genre = "genre";
    private final static String BK_Purpose = "purpose";
    private final static String BK_Views = "view_count";

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Create Users Table with Role Column
        String createTable = " CREATE TABLE " + TB_Name+ " ( " +
                TB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TB_Username + " TEXT, " +
                TB_Password + " TEXT, " +
                TB_Role + " TEXT) ";
        db.execSQL(createTable);

        // 2. SEED ADMIN ACCOUNT: Automatically create the Admin with a HASHED password
        String adminPass = hashPassword("tanginamo");
        db.execSQL("INSERT INTO " + TB_Name + " (" + TB_Username + ", " + TB_Password + ", " + TB_Role +
                ") VALUES ('hello', '" + adminPass + "', 'ADMIN')");

        // 3. Create Books Table
        String createBookTable = " CREATE TABLE " + TB_Book + " ( " +
                BK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BK_Title + " TEXT, " +
                BK_Author + " TEXT, " +
                BK_Image + " INTEGER, " +
                BK_Desc + " TEXT, " +
                BK_Date + " TEXT, " +
                BK_Genre + " TEXT, " +
                BK_Purpose + " TEXT, " +
                BK_Views + " INTEGER DEFAULT 0) ";
        db.execSQL(createBookTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_Name);
        db.execSQL("DROP TABLE IF EXISTS " + TB_Book);
        onCreate(db);
    }

    // --- SECURITY: PASSWORD HASHING (SHA-256) ---
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // --- USER METHODS ---

    public boolean InsertAcc(String name, String pass){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(TB_Username, name);
        // Protect user privacy: Only store the HASH
        val.put(TB_Password, hashPassword(pass));
        val.put(TB_Role, "USER");
        long res = db.insert(TB_Name, null, val);
        return res != -1;
    }

    public String getUserRole(String user, String pass){
        SQLiteDatabase db = this.getReadableDatabase();
        // We hash the input password to see if it matches the hash in the DB
        String hashedInput = hashPassword(pass);

        Cursor cursor = db.rawQuery("SELECT " + TB_Role + " FROM " + TB_Name +
                        " WHERE " + TB_Username + " = ? AND " + TB_Password + " = ?",
                new String[]{user, hashedInput});

        String role = null;
        if (cursor.moveToFirst()) {
            role = cursor.getString(0);
        }
        cursor.close();
        return role; // Returns "ADMIN", "USER", or null
    }

    // --- BOOK METHODS ---

    public long InsertBook(String title, String author, int imageRes, String desc, String date, String genre, String purpose){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(BK_Title, title);
        val.put(BK_Author, author);
        val.put(BK_Image, imageRes);
        val.put(BK_Desc, desc);
        val.put(BK_Date, date);
        val.put(BK_Genre, genre);
        val.put(BK_Purpose, purpose);
        val.put(BK_Views, 0);
        return db.insert(TB_Book, null, val);
    }

    public Cursor ReadAllBooks(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TB_Book, null);
    }

    public boolean DeleteBook(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete(TB_Book, BK_ID + " = ? ", new String[]{String.valueOf(id)});
        return res > 0;
    }

    public void IncrementBookView(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TB_Book + " SET " + BK_Views + " = " + BK_Views + " + 1 WHERE " + BK_ID + " = " + id);
    }

    public Cursor GetRecentBooks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TB_Book + " ORDER BY " + BK_ID + " DESC LIMIT 10", null);
    }
}