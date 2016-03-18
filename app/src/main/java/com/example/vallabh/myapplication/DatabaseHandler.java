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
    public static final String TIMESTAMP = "timestamp";
    public static final String CATEGORY = "category";

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
                + AMOUNT + " TEXT, "
                + TIMESTAMP + " TEXT, "
                + CATEGORY + " TEXT)";
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
    void add(String smsMsgStr1, String smsAccNo1, String mAmount1,String mTimeStamp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(STATUS, smsMsgStr1.toString());
        values.put(ACCOUNT_NO, smsAccNo1.toString());
        values.put(AMOUNT, mAmount1.toString());
        values.put(TIMESTAMP,mTimeStamp.toString());

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }
    //Initialize values for bank details in table3
    void firstAdd(String smsAccNo1) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ACCOUNT_NO,smsAccNo1.toString());
        values.put(CREDIT,"0");
        values.put(DEBIT,"0");
        values.put(TOTAL,"0");

        db.insert(TABLE_NAME3, null, values);
        db.close();
    }
    //Update Table3 values
    void Bank(String smsMsgStr1,String smsAccNo1,String mAmount1) {
        SQLiteDatabase db = this.getWritableDatabase();
        String total="";
        ContentValues values = new ContentValues();
        String selectQuery = "SELECT * FROM " + TABLE_NAME3 + " WHERE " + ACCOUNT_NO + " LIKE \"%" + smsAccNo1 + "%\" ";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(smsMsgStr1.equalsIgnoreCase("Debited")) {
            String deb="";
            float temp;
            if (cursor.moveToFirst()) {
                do {
                    deb+=cursor.getString(3);
                    temp = Float.parseFloat(deb.toString()) + Float.parseFloat(mAmount1.toString());
                    deb="";
                    deb+=String.valueOf(temp);
                    values.put(DEBIT,deb);
                    total+=cursor.getString(4);
                    temp = Float.parseFloat(total.toString()) - Float.parseFloat(mAmount1.toString());
                    values.put(TOTAL,temp);
                    db.update(TABLE_NAME3,values,ACCOUNT_NO + " LIKE \"%" +smsAccNo1+ "%\"",null);
                } while (cursor.moveToNext());
            }
        }
        if(smsMsgStr1.equalsIgnoreCase("Credited")) {
            String cre="";
            float temp;
            if (cursor.moveToFirst()) {
                do {
                    cre+=cursor.getString(2);
                    temp = Float.parseFloat(cre.toString()) + Float.parseFloat(mAmount1.toString());
                    cre="";
                    cre+=String.valueOf(temp);
                    values.put(CREDIT,cre);
                    total+=cursor.getString(4);
                    temp = Float.parseFloat(total.toString())+Float.parseFloat(mAmount1.toString());
                    values.put(TOTAL,temp);
                    db.update(TABLE_NAME3, values, ACCOUNT_NO + " LIKE \"%" + smsAccNo1 + "%\"", null);
                } while (cursor.moveToNext());
            }
        }

        db.close();
    }

    //Add Date to date table
    void AddFirstDate(String Data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIME, Data);

        db.insert(TABLE_NAME2, null, values);
        db.close();
    }
    //Update Date in the table
    void UpdateDate(String Data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIME, Data);
        db.update(TABLE_NAME2, values, KEY_ID2 + " LIKE \"%1%\"", null);
        db.close();
    }
    //Returns the last timestamp
    public ArrayList<String> Selected2() {
        ArrayList<String> DataList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT "+TIME+" FROM " + TABLE_NAME2;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String Data,Entry1;

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

    //Returns all transaction for specific banks
    public ArrayList<String> Selected3(String Account) {
        ArrayList<String> DataList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME+ " WHERE " + ACCOUNT_NO + " LIKE \"" + Account+"\"";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String sta,acc,amo,time,Entry1;
        int id=0;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                id++;
                Entry1 = "";
                sta="";
                acc="";
                amo="";
                time="";
                sta+=cursor.getString(1);
                acc+=cursor.getString(2);
                amo+=cursor.getString(3);
                time+=cursor.getString(4);
                Entry1+="\n "+id+" "+sta+" "+amo+" "+time;
                DataList.add(Entry1);
            } while (cursor.moveToNext());
        }
        return DataList;
    }

    //Reading entries from table 3
    public ArrayList<String> getAllvalues() {
        ArrayList<String> DataList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME3;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String id,acc,cre,deb,tot,Entry1;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Entry1 = "";
                id="";
                cre="";
                acc="";
                deb="";
                tot="";
                id+=cursor.getString(0);
                acc+=cursor.getString(1);
                cre+=cursor.getString(2);
                deb+=cursor.getString(3);
                tot+=cursor.getString(4);
                Entry1+="\n "+acc+
                        "\n CREDITED : "+cre+
                        "\n DEBITED : "+deb+
                        "\n TOTAL : "+tot;
                DataList.add(Entry1);
            } while (cursor.moveToNext());
        }
        return DataList;
    }
    //Reading all account numbers
    public ArrayList<String> Select4() {
        ArrayList<String> DataList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME3;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String acc,Entry1;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Entry1 = "";
                acc="";
                acc+=cursor.getString(1);
                Entry1+=acc;
                DataList.add(Entry1);
            } while (cursor.moveToNext());
        }
        return DataList;
    }
}