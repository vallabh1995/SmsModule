package com.example.vallabh.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "SMSData";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "triAcc"; //Table Name

    //Column Names
    public static final String ACCOUNT_NO = "accountNo";
    public static final String STATUS = "credit_debit";
    public static final String AMOUNT = "amount";
    public static final String KEY_ID = "_id";

    public DatabaseHandler (Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE;

        CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + STATUS + " TEXT, "
                + ACCOUNT_NO + " TEXT, "
                + AMOUNT + " TEXT )";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    void add(String smsMsgStr1,String smsAccNo1,String mAmount1) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(STATUS, smsMsgStr1.toString());
        values.put(ACCOUNT_NO, smsAccNo1.toString());
        values.put(AMOUNT, mAmount1.toString());

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    public ArrayList<String> getAllvalues() {
        ArrayList<String> DataList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String sta,acc,amo,id,Entry1;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Entry1 = "";
                id="";
                sta="";
                acc="";
                amo="";
                id+=cursor.getString(0);
                sta+=cursor.getString(1);
                acc+=cursor.getString(2);
                amo+=cursor.getString(3);
                Entry1+="\n "+id+" "+sta+" "+acc+" "+amo;
                DataList.add(Entry1);
            } while (cursor.moveToNext());
        }
        return DataList;
    }
}