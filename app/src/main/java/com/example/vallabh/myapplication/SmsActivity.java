package com.example.vallabh.myapplication;

import android.app.AlertDialog;
import android.content.ContentResolver;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SmsActivity extends AppCompatActivity implements OnItemClickListener {

    private static SmsActivity inst;
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ListView smsListView;
    String categoryG;
    ArrayAdapter arrayAdapter;
    /*DATABASE HANDLES*/
    final DatabaseHandler db = new DatabaseHandler(this);
    String smsMessage = "", smsMessageStr = "", mAmount = "", smsAccNo = "", ReadAcc = "";
    public int StartApp=0;
    /*BANK SMS ADDRESSES*/
    public static int NoBank = 16;
    public static String stringArray[] = {"8451043280", "VM-HDFCBK", "VM-BOIIND", "BP-SBIMBS", "AM-HDFCBK", "VM-UnionB", "VM-UIICHO", "VM-CBSSBI", "VM-CorpBk", "VL-CENTBK", "VM-CENTBK", "BW-PNBSMS","VK-BOIIND","VM-CBSSBI","VM-BOIIND","BZ-ATMSBI","VK-AxisBk"};
    /*ACCOUNT NUMBER*/
    public ArrayList<String> accountNumbers = new ArrayList<String>();
    public int accountI = 0,ft;
    public String PushTime;
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

            /*
            /* Button for Database
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

            /* Button for Messages
            Button But2 = (Button) findViewById(R.id.messages);
            But2.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View view) {
                    refreshSmsInbox();
                }
            });
            */
        Button But3 = (Button) findViewById(R.id.submit);
        But3.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                //For selected account number
                arrayAdapter.clear();
                //for (int j = 0; j < accountI; j++) {
                  //  ArrayList<String> Val = db.Selected(accountNumbers.get(j));
               ArrayList<String> Val = db.getAllvalues();
                    for (int i = 0; i < Val.size(); i++) {
                        arrayAdapter.add(Val.get(i));
                   }
                //}
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
        Val=db.Select4();
        for(int j=0;j<Val.size();j++) {
            accountNumbers.add(Val.get(j));
            accountI++;
        }
        refreshSmsInbox();
        ft=0;
        db.UpdateDate(PushTime);
        StartApp=0;
        arrayAdapter.clear();
        //for (int j = 0; j < accountI; j++) {
         //   Val = db.Selected(accountNumbers.get(j));
        Val = db.getAllvalues();
            for (int i = 0; i < Val.size(); i++) {
                arrayAdapter.add(Val.get(i));
            }
       // }
    }

    public void refreshSmsInbox() {
        String Balance="";
        int balA=0;
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
            String month="",year="";
            float DB,SMS;
            String strAddress = smsInboxCursor.getString(indexAddress);
            //String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
            //        "\n" + smsInboxCursor.getString(indexBody).toLowerCase() + "\n";
            String Time = smsInboxCursor.getString(smsInboxCursor.getColumnIndex("date"));
            Long timestamp = Long.parseLong(Time);
            calendar.setTimeInMillis(timestamp);

            Date finalDate = calendar.getTime();
            String smsDate = finalDate.toString();
            month+=smsDate.substring(4,7);
            year+=smsDate.substring(30,34);
            //str+="Date : "+smsDate+"\nTimeStamp : "+Time;

            SMS=Float.parseFloat(Time);
            smsMessage += smsInboxCursor.getString(indexBody).toLowerCase();
            if(ft==0){
                ft=1;
                PushTime=Time;
            }
            for (i = 0; i <= NoBank; i++) {
                if (stringArray[i].equalsIgnoreCase(strAddress)) {
                    //arrayAdapter.add(str);

                    //SearchForAccountNumber
                    if (smsMessage.contains("a/c no.")) {
                        a = smsMessage.indexOf("a/c");
                        b = smsMessage.indexOf(" ", a + 8);
                        smsAccNo += smsMessage.substring(a + 7, b);
                    } else if (smsMessage.contains("a/c")) {
                        a = smsMessage.indexOf("a/c");
                        b = smsMessage.indexOf(" ", a +9);
                        smsAccNo += smsMessage.substring(a + 4, b);
                    } else if (smsMessage.contains("account number")) {
                        a = smsMessage.indexOf("account");
                        b = smsMessage.indexOf(" ", a + 15);
                        smsAccNo += smsMessage.substring(a + 15, b);
                    } else if (smsMessage.contains("account")) {
                        a = smsMessage.indexOf("account");
                        b = smsMessage.indexOf(" ", a + 8);
                        smsAccNo += smsMessage.substring(a + 8, b);
                    } else if (smsMessage.contains("ac")) {
                        a = smsMessage.indexOf("ac");
                        b = smsMessage.indexOf(" ", a + 3);
                        smsAccNo += smsMessage.substring(a + 3, b);
                    }
                    String smsAccNo2=smsAccNo.replace("x","");
                    smsAccNo=smsAccNo2.replace(" ","");
                    int found = 0;
                    String Temp = "";
                    for (int j = 0; j < accountI; j++) {
                        Temp = accountNumbers.get(j);
                        if (Temp.contains(smsAccNo)) {
                            found = 1;
                            break;
                        }
                        else if(smsAccNo.contains(Temp)){
                            found = 1;
                            break;
                        }
                    }

                    if (found != 1) {
                        accountNumbers.add(smsAccNo);
                        /*if(smsMessage.contains("balance"))
                        {
                            String Bal="";
                            a=smsMessage.indexOf("balance");
                            b=smsMessage.indexOf(" ",a+8);
                            Bal+=smsMessage.substring(a, b);
                            Toast.makeText(this, "Data : "+Bal, Toast.LENGTH_SHORT);
                        }
                        else
                        {..
                        }*/
                        db.firstAdd(smsAccNo);
                        accountI++;
                    }

                    if(StartApp==1) {
                        AddEntry(smsMessage,month+" "+year);
                    }
                    else {
                        ArrayList<String> Val = db.Selected2();
                        String S1 = Val.get(Val.size() - 1);
                        DB = Float.parseFloat(S1);
                        if (SMS > DB) {
                            AddEntry(smsMessage,month+" "+year);
                       }
                    }
                    break;
                }
            }
        } while (smsInboxCursor.moveToNext());
    }


    public void updateList(final String smsMessage, final String strAddress2) {
        /*int i;
        for (i = 0; i <= NoBank; i++) {
            if (stringArray[i].equalsIgnoreCase(strAddress2)) {

                Toast.makeText(this,"smsMessage : "+smsMessage +" add : " + strAddress2,Toast.LENGTH_SHORT);

                break;
            }
        }*/
    }

    public void AddEntry(String Message,String Time) {
        int a = 0, b = 0;
        smsMessage = "";
        smsMessageStr = "";
        String Category="";
        mAmount = "";
        smsAccNo = "";
        String Balance="";
        int balA=0;
        smsMessage += Message;

        //Status
        if (smsMessage.contains("credited")) {
            smsMessageStr += "Credited";
        } else if (smsMessage.contains("debited") || smsMessage.contains("withdraw")) {
            smsMessageStr += "Debited";
        }

        //AccountNumber
        if (smsMessage.contains("a/c no.")) {
            a = smsMessage.indexOf("a/c");
            b = smsMessage.indexOf(" ", a + 8);
            smsAccNo += smsMessage.substring(a + 7, b);
        } else if (smsMessage.contains("a/c")) {
            a = smsMessage.indexOf("a/c");
            b = smsMessage.indexOf(" ", a + 9);
            smsAccNo += smsMessage.substring(a + 4, b);
        } else if (smsMessage.contains("account number")) {
            a = smsMessage.indexOf("account");
            b = smsMessage.indexOf(" ", a + 15);
            smsAccNo += smsMessage.substring(a + 15, b);
        } else if (smsMessage.contains("account")) {
            a = smsMessage.indexOf("account");
            b = smsMessage.indexOf(" ", a + 8);
            smsAccNo += smsMessage.substring(a + 8, b);
        } else if (smsMessage.contains("ac")) {
            a = smsMessage.indexOf("ac");
            b = smsMessage.indexOf(" ", a + 3);
            smsAccNo += smsMessage.substring(a + 3, b);
        }
        String smsAccNo2 = smsAccNo.replace("x", "");
        smsAccNo = smsAccNo2.replace(" ", "");

        //Amount
        if (smsMessage.contains("rs.")) {
            a = smsMessage.indexOf("rs.");
            b = smsMessage.indexOf(" ", a + 4);
            mAmount += smsMessage.substring(a + 4, b);
        } else if (smsMessage.contains("rs")) {
            a = smsMessage.indexOf("rs");
            b = smsMessage.indexOf(" ", a + 4);
            mAmount += smsMessage.substring(a + 3, b);
        } else if (smsMessage.contains("inr")) {
            a = smsMessage.indexOf("inr");
            b = smsMessage.indexOf(" ", a + 4);
            mAmount += smsMessage.substring(a + 4, b);
        }

        //Balance
        if (smsMessage.contains("balance "))
        {
            a=smsMessage.indexOf("balance ");
            b=smsMessage.indexOf(".",a+8);
            Balance +=smsMessage.substring(a + 8, b + 2);
            balA=1;
        }

        String mAmount2 = mAmount.replace(",","");
        Toast.makeText(this, "Data Contain :\n|"+smsMessageStr+"|\n|"+smsAccNo+"|\n|"+mAmount2, Toast.LENGTH_SHORT).show();
        if(smsMessageStr.length()!=0 && smsAccNo.length()!=0 && mAmount2.length()!=0) {
            db.add(smsMessageStr, smsAccNo, mAmount2, Time,Category);
            db.Bank(smsMessageStr, smsAccNo, mAmount2);
            if(balA==1) {
                Toast.makeText(this, "Balance : "+Balance,Toast.LENGTH_SHORT).show();
              //  db.UpdateTotal(smsAccNo,Balance);
            }
        }
}

    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        try {
            String[] smsMessages = smsMessagesList.get(pos).split("\n");
            String ClickedItem="",Acc="";
            String _id,amo,time,sta,temp;
            int a=0,b=0;
            for (int i = 1; i < smsMessages.length; ++i) {
                ClickedItem += smsMessages[i];
            }
            a=ClickedItem.indexOf(" ");
            b=ClickedItem.indexOf(" ",a+2);
            Acc+=ClickedItem.substring(a+1, b);
            Toast.makeText(this,"Acc : |"+Acc+"|",Toast.LENGTH_SHORT);
            ArrayList<String> Val = db.Selected3(Acc);
            arrayAdapter.clear();
            for (int i = 0; i < Val.size(); i++) {
                temp=Val.get(i);
                a=temp.indexOf(" ");
                b=temp.indexOf(" ", a + 2);
                _id=temp.substring(a + 1, b);
                a=temp.indexOf(" ", b + 2);
                sta=temp.substring(b + 1, a);
                b=temp.indexOf(" ", a + 2);
                amo=temp.substring(a + 1, b);
                time=temp.substring(b + 1);
                String Tm="\n|" +_id+ "|\n|" + sta + "|\n|" + amo + "|\n|" + time + "|";
                arrayAdapter.add(Tm);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}