package com.example.batrakov.servicetask;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {
    MyService mService;
    EditText text;
    Button button;
    String str;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        text = (EditText) findViewById(R.id.editText);

        button = (Button) findViewById(R.id.save);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str = text.getText().toString();
                mService.setString(str);
            }
        });


        intent = new Intent(this, MyService.class);
        startService(intent);


    }

    @Override
    protected void onStart() {
        bindService(intent, mConnection, BIND_AUTO_CREATE);
        super.onStart();
    }

    @Override
    protected void onStop() {
 //       unbindService(mConnection);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }

    @Override
    protected void onResume(){
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            mService = binder.getService();
            str = mService.getString();
            text.setText(str);
            Log.i("Service1", "connected");
            Log.i("Service1", str +"_");

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.i("Service1", "disconnected");
        }
    };




}
