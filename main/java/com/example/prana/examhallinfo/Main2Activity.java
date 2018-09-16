package com.example.prana.examhallinfo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    Button button;
    String  PathHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        button = (Button)findViewById(R.id.btn_picker);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 7);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        switch (requestCode) {
            case 7:
                if (resultCode == RESULT_OK) {
                    PathHolder = data.getData().getPath();
                    Toast.makeText(Main2Activity.this, PathHolder, Toast.LENGTH_LONG).show();
                }
                opensms();
                break;
        }
    }

    public void opensms() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }
    private List<Retrieve> ret = new ArrayList<>();

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults ) {
        //Read Retrieve
        // File info = new File(PathHolder);
        InputStream is;
            is=getResources().openRawResource(R.raw.examhallinfo);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";

       // try {
           // FileInputStream fileIn= openFileInput(PathHolder.);
           // InputStreamReader InputRead= new InputStreamReader(fileIn);
           // char[] inputBuffer= new char[100];

        try {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                Retrieve r = new Retrieve();
                r.setRollno(tokens[0]);
                r.setExamclass(tokens[1]);
                r.setPhoneno(tokens[2]);
                ret.add(r);
                switch (requestCode) {
                    case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(tokens[2], null, "your exam class is" + tokens[1], null, null);
                            Toast.makeText(getApplicationContext(), "SMS sent.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
            }

        } catch (IOException e) {
            Log.wtf("MyActivity", "error reading datafile on line" + line, e);
            e.printStackTrace();
        }

    }
}



