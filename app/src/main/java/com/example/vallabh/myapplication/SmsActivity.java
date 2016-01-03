package com.example.vallabh.myapplication;

import android.app.Activity;
import android.content.ContentResolver;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Button;
import java.util.ArrayList;


//kkk
public class SmsActivity extends AppCompatActivity implements OnItemClickListener {

    private static SmsActivity inst;
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ListView smsListView;
    ArrayAdapter arrayAdapter;
    final DatabaseHandler db = new DatabaseHandler(this);
    String smsMessage = "", smsMessageStr= "", mAmount = "",smsAccNo = "";
    public static int NoBank=12;
    public static String stringArray[] = {"8451043280","VM-HDFCBK","VM-BOIIND","BP-SBIMBS","BP-ATMSBI","AM-HDFCBK","VM-UnionB","VM-UIICHO","VM-CBSSBI","VM-CorpBk","VL-CENTBK","VM-CENTBK","BW-PNBSMS"};

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
        Button But1= (Button) findViewById(R.id.buttonDb);
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
        Button But2= (Button) findViewById(R.id.messages);
        But2.setOnClickListener(new AdapterView.OnClickListener(){
            @Override
            public void onClick(View view){
                refreshSmsInbox();
            }
        });

        smsListView = (ListView) findViewById(R.id.SMSList);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        smsListView.setAdapter(arrayAdapter);
        smsListView.setOnItemClickListener(this);

        refreshSmsInbox();
    }

    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        int i;
        do {
            String strAddress = smsInboxCursor.getString(indexAddress);
            String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody).toLowerCase() + "\n";
            for(i=0;i<=NoBank;i++) {
                if (stringArray[i].equalsIgnoreCase(strAddress)) {
                    arrayAdapter.add(str);
                    break;
                }
            }
        } while (smsInboxCursor.moveToNext());
    }

    public void updateList(final String smsMessage,final String strAddress2) {
        int i;
        for(i=0;i<=NoBank;i++) {
            if (stringArray[i].equalsIgnoreCase(strAddress2)) {
                arrayAdapter.insert(smsMessage, 0);
                arrayAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        try {
            String[] smsMessages = smsMessagesList.get(pos).split("\n");
            int a=0,b=0;
            smsMessage = ""; smsMessageStr= ""; mAmount = ""; smsAccNo = "";

            for (int i = 1; i < smsMessages.length; ++i) {
               smsMessage += smsMessages[i];
            }

            if(smsMessage.contains("credited")) {
                smsMessageStr +="Credited";
            }
            else if(smsMessage.contains("debited")) {
                smsMessageStr +="Debited";
            }

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
            else if (smsMessage.contains("account")) {
                a=smsMessage.indexOf("account");
                b=smsMessage.indexOf(" ",a+8);
                smsAccNo += smsMessage.substring(a+8,b);
            }

            if(smsMessage.contains("rs")) {
                a=smsMessage.indexOf("rs");
                b=smsMessage.indexOf(" ",a+4);
                mAmount += smsMessage.substring(a+4,b);
            }
            else if(smsMessage.contains("inr")) {
                a=smsMessage.indexOf("inr");
                b=smsMessage.indexOf(" ",a+4);
                mAmount += smsMessage.substring(a+4,b);
            }

            Toast.makeText(this, "Data Contain :\n|"+smsMessageStr+"\n|"+smsAccNo+"\n|"+mAmount, Toast.LENGTH_SHORT).show();
            db.add(smsMessageStr,smsAccNo,mAmount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}