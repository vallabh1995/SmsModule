package com.example.vallabh.myapplication;

import android.content.ContentResolver;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SmsActivity extends AppCompatActivity implements OnItemClickListener {

    private static SmsActivity inst;
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ListView smsListView;
    ArrayAdapter arrayAdapter;
    /*DATABASE HANDLES*/
    final DatabaseHandler db = new DatabaseHandler(this);
    String smsMessage = "", smsMessageStr = "", mAmount = "", smsAccNo = "", ReadAcc = "";
    public int StartApp=0;
    /*BANK SMS ADDRESSES*/
    public static int NoBank = 12;
    public static String stringArray[] = {"8451043280", "VM-HDFCBK", "VM-BOIIND", "BP-SBIMBS", "BP-ATMSBI", "AM-HDFCBK", "VM-UnionB", "VM-UIICHO", "VM-CBSSBI", "VM-CorpBk", "VL-CENTBK", "VM-CENTBK", "BW-PNBSMS"};
    /*ACCOUNT NUMBER*/
    public ArrayList<String> accountNumbers = new ArrayList<String>();
    public int accountI = 0;
    /*CALENDER*/
    Calendar calendar = Calendar.getInstance();


    public static SmsActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        /* Button for Database */
        Button But1 = (Button) findViewById(R.id.buttonDb);
        But1.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> Val = db.getAllvalues();
                arrayAdapter.clear();
                for (int i = 0; i < Val.size(); i++) {
                    arrayAdapter.add(Val.get(i));
                }
            }
        });

        /* Button for Messages */
        Button But2 = (Button) findViewById(R.id.messages);
        But2.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshSmsInbox();
            }
        });

        Button But3 = (Button) findViewById(R.id.submit);
        final EditText mEdit;
        mEdit = (EditText) findViewById(R.id.accRead);
        But3.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReadAcc = "";
                ReadAcc += mEdit.getText().toString();

                //For selected account number
                if (ReadAcc != "") {
                    ArrayList<String> Val = db.Selected(ReadAcc);
                    arrayAdapter.clear();
                    for (int i = 0; i < Val.size(); i++) {
                        arrayAdapter.add(Val.get(i));
                    }
                }
                //For all accounts in the string
                else {
                    arrayAdapter.clear();
                    for (int j = 0; j < accountI; j++) {
                        ArrayList<String> Val = db.Selected(accountNumbers.get(j));
                        for (int i = 0; i < Val.size(); i++) {
                            arrayAdapter.add(Val.get(i));
                        }
                    }
                }
            }
        });

        smsListView = (ListView) findViewById(R.id.SMSList);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        smsListView.setAdapter(arrayAdapter);
        smsListView.setOnItemClickListener(this);

        ArrayList<String> Val = db.Selected2();
        if(Val.size()==0)
        {
            db.AddFirstDate("0000000000000");
            StartApp=1;
        }

        refreshSmsInbox();
        StartApp=0;
    }

    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        int i;
        int a, b;
        do {
            smsMessage = "";
            smsAccNo = "";
            float DB,SMS;
            String strAddress = smsInboxCursor.getString(indexAddress);
            String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody).toLowerCase() + "\n";
            String Time = smsInboxCursor.getString(smsInboxCursor.getColumnIndex("date"));
            Long timestamp = Long.parseLong(Time);
            calendar.setTimeInMillis(timestamp);
            Date finalDate = calendar.getTime();
            String smsDate = finalDate.toString();
            str+="Date : "+smsDate+"\nTimeStamp : "+Time;
            SMS=Float.parseFloat(Time);
            smsMessage += smsInboxCursor.getString(indexBody).toLowerCase();
            for (i = 0; i <= NoBank; i++) {
                if (stringArray[i].equalsIgnoreCase(strAddress)) {
                    arrayAdapter.add(str);

                    //SearchForAccountNumber
                    if (smsMessage.contains("a/c no.")) {
                        a = smsMessage.indexOf("a/c");
                        b = smsMessage.indexOf(" ", a + 8);
                        smsAccNo += smsMessage.substring(a + 7, b);
                    } else if (smsMessage.contains("a/c")) {
                        a = smsMessage.indexOf("a/c");
                        b = smsMessage.indexOf(" ", a + 4);
                        smsAccNo += smsMessage.substring(a + 4, b);
                    } else if (smsMessage.contains("account number")) {
                        a = smsMessage.indexOf("account");
                        b = smsMessage.indexOf(" ", a + 15);
                        smsAccNo += smsMessage.substring(a + 15, b);
                    } else if (smsMessage.contains("account")) {
                        a = smsMessage.indexOf("account");
                        b = smsMessage.indexOf(" ", a + 8);
                        smsAccNo += smsMessage.substring(a + 8, b);
                    }
                    int found = 0;
                    String Temp = "";
                    for (int j = 0; j < accountI; j++) {
                        Temp = accountNumbers.get(j);
                        if (Temp.equalsIgnoreCase(smsAccNo)) {
                            found = 1;
                            break;
                        }
                    }
                    if (found != 1) {
                        accountNumbers.add(smsAccNo);
                        accountI++;
                    }

                    if(StartApp==1) {
                        AppOpenAction(smsMessage);
                        ArrayList<String> Val = db.Selected2();
                        String S1 = Val.get(Val.size() - 1);
                        DB = Float.parseFloat(S1);
                        if (SMS > DB) {
                            Toast.makeText(this, "Added : |" + DB + "|", Toast.LENGTH_SHORT).show();
                            db.AddFirstDate(Time);
                        }
                    }
                    else {
                        ArrayList<String> Val = db.Selected2();
                        String S1 = Val.get(Val.size() - 1);
                        DB = Float.parseFloat(S1);
                        if (SMS > DB) {
                            Toast.makeText(this, "Added : |" + DB + "|", Toast.LENGTH_SHORT).show();
                            db.AddFirstDate(Time);
                            AppOpenAction(smsMessage);
                        }
                    }
                    break;
                }
            }
        } while (smsInboxCursor.moveToNext());
    }

    public void updateList(final String smsMessage, final String strAddress2) {
        int i;
        for (i = 0; i <= NoBank; i++) {
            if (stringArray[i].equalsIgnoreCase(strAddress2)) {
                arrayAdapter.insert(smsMessage, 0);
                arrayAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    public void AppOpenAction(String Message) {
        int a=0,b=0;
        smsMessage = ""; smsMessageStr= ""; mAmount = ""; smsAccNo = "";

        smsMessage+=Message;

        //Status
        if(smsMessage.contains("credited")) {
            smsMessageStr +="Credited";
        }
        else if(smsMessage.contains("debited") || smsMessage.contains("withdraw")) {
            smsMessageStr +="Debited";
        }

        //AccountNumber
        if(smsMessage.contains("a/c no.")) {
            a=smsMessage.indexOf("a/c");
            b=smsMessage.indexOf(" ",a+8);
            smsAccNo  += smsMessage.substring(a+7,b);
        }
        else if(smsMessage.contains("a/c")) {
            a=smsMessage.indexOf("a/c");
            b=smsMessage.indexOf(" ",a+4);
            smsAccNo  += smsMessage.substring(a+4,b);
        }
        else if(smsMessage.contains("account number")) {
            a=smsMessage.indexOf("account");
            b=smsMessage.indexOf(" ",a+15);
            smsAccNo += smsMessage.substring(a+15,b);
        }
        else if (smsMessage.contains("account")) {
            a=smsMessage.indexOf("account");
            b=smsMessage.indexOf(" ",a+8);
            smsAccNo += smsMessage.substring(a+8,b);
        }

        //Amount
        if(smsMessage.contains("rs.")) {
            a=smsMessage.indexOf("rs.");
            b=smsMessage.indexOf(" ",a+4);
            mAmount += smsMessage.substring(a+4,b);
        }
        else if(smsMessage.contains("rs")) {
            a=smsMessage.indexOf("rs");
            b=smsMessage.indexOf(" ",a+4);
            mAmount += smsMessage.substring(a+3,b);
        }
        else if(smsMessage.contains("inr")) {
            a=smsMessage.indexOf("inr");
            b=smsMessage.indexOf(" ",a+4);
            mAmount += smsMessage.substring(a+4,b);
        }

        Toast.makeText(this, "Data Contain :\n|"+smsMessageStr+"\n|"+smsAccNo+"\n|"+mAmount, Toast.LENGTH_SHORT).show();
        db.add(smsMessageStr,smsAccNo,mAmount);
}

    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}