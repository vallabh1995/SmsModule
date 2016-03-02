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
    public static final String TABLE_NAME = "triAcc"; //Table Name 1
    public static final String TABLE_NAME2 = "TimeStore";
    public static final String TABLE_NAME3 = "BankDetails";

    //Column Names 1
    public static final String ACCOUNT_NO = "accountNo";
    public static final String STATUS = "credit_debit";
    public static final String AMOUNT = "amount";
    public static final String KEY_ID = "_id";

    //Column Name 2
    public static final String TIME = "time";
    public static final String KEY_ID2 = "_id2";

    //Column Name 3
    public static final String KEY_ID3 = "_id3";
    public static final String CREDIT = "credit";
    public static final String DEBIT = "debit";
    public static final String TOTAL = "total";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

        CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME2 + "( "
                + KEY_ID2 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TIME + " TEXT )";
        db.execSQL(CREATE_CONTACTS_TABLE);

        CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME3 + "( "
                + KEY_ID3 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ACCOUNT_NO + " TEXT, "
                + CREDIT + " TEXT, "
                + DEBIT + " TEXT, "
                + TOTAL + " TEXT )";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(db);
    }

    //Insert Row into Message table
    void add(String smsMsgStr1, String smsAccNo1, String mAmount1) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(STATUS, smsMsgStr1.toString());
        values.put(ACCOUNT_NO, smsAccNo1.toString());
        values.put(AMOUNT, mAmount1.toString());

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    void firstAdd(String smsAccNo1)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ACCOUNT_NO,smsAccNo1.toString());
        values.put(CREDIT,"0");
        values.put(DEBIT,"0");
        values.put(TOTAL,"0");

        db.update(TABLE_NAME3, values, ACCOUNT_NO + " LIKE \"%"+smsAccNo1.toString()+"%\"", null);

        db.close();
    }
    void Bank(String smsMsgStr1,String smsAccNo1,String mAmount1) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(STATUS, smsMsgStr1.toString());
        values.put(ACCOUNT_NO, smsAccNo1.toString());
        values.put(AMOUNT, mAmount1.toString());


    }

    //Add Date to date table
   void AddFirstDate(String Data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIME, Data);

        db.insert(TABLE_NAME2, null, values);
        db.close();
    }

    void UpdateDate(String Data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIME, Data);
        db.update(TABLE_NAME2, values, KEY_ID2 + " LIKE \"%1%\"", null);
        db.close();
    }

    //Returns the last time
    public ArrayList<String> Selected2() {
        ArrayList<String> DataList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT "+TIME+" FROM " + TABLE_NAME2;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String id,Data,Entry1;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Entry1 = "";
                //id="";
                Data="";
                //id+=cursor.getString(0);
                Data+=cursor.getString(0);
                Entry1+=/*id+" "+*/Data;
                DataList.add(Entry1);
            } while (cursor.moveToNext());
        }
        return DataList;
    }

    //Temp for displaying time data
    public ArrayList<String> Selected3() {
        ArrayList<String> DataList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME3;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String id,deb,cre,tot,acc,Entry1;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Entry1 = "";
                id= "";
                deb= "";
                cre="";
                tot="";
                acc="";
                id+=cursor.getString(0);
                acc+=cursor.getString(1);
                cre+=cursor.getString(2);
                deb+=cursor.getString(3);
                tot+=cursor.getString(4);
                Entry1+="\n "+id+" "+acc+" "+cre+" "+deb+" "+tot;
                DataList.add(Entry1);
            } while (cursor.moveToNext());
        }
        return DataList;
    }

    //Reading entries from table 1
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

    //selecting data from specific account
    public ArrayList<String> Selected(String Account) {
        float Sum1=0,Sum2=0;
        ArrayList<String> DataList = new ArrayList<String>();
        //Select AMOUNT from table where status is CREDITED and account number matches
        String selectQuery = "SELECT  "+ AMOUNT +" FROM " + TABLE_NAME + " WHERE " + ACCOUNT_NO + " LIKE \"" + Account + "\" AND "+ STATUS + " LIKE \"CREDITED\"";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String amo,Entry1="";

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                amo="";
                amo+=cursor.getString(0);
                Sum1+=Float.parseFloat(amo.toString());
            } while (cursor.moveToNext());
            Entry1+="\n "+Account+" CREDITED : "+Sum1;
            DataList.add(Entry1);
        }

        //Select AMOUNT from table where status is CREDITED and account number matches
        selectQuery = "SELECT  "+ AMOUNT +" FROM " + TABLE_NAME + " WHERE " + ACCOUNT_NO + " LIKE \"" + Account + "\" AND "+ STATUS + " LIKE \"DEBITED\"";
        cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                amo="";
                amo+=cursor.getString(0);
                Sum2+=Float.parseFloat(amo.toString());
            } while (cursor.moveToNext());
            Entry1="\n "+Account+" DEBITED : "+Sum2;
            DataList.add(Entry1);
        }
        Entry1="\n "+Account+" Total : "+(Sum1-Sum2);
        DataList.add(Entry1);
        return DataList;
    }
}