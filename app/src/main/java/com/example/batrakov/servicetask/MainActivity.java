package com.example.batrakov.servicetask;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    static final int MSG_CHANGE_STR = 1;
    boolean mBound = false;

    Messenger mService = null;
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
                sendString();
                str = text.getText().toString();
            }
        });


        intent = new Intent(this, MyService.class);
        startService(intent);

        bindService(intent, mConnection, BIND_AUTO_CREATE);


    }




    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_CHANGE_STR:
                    Toast.makeText(getApplicationContext(), "ActivityHandler", Toast.LENGTH_SHORT).show();
                    text.setText(msg.getData().getString("str"));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    protected void onStart() {
        bindService(intent, mConnection, BIND_AUTO_CREATE);
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
  //      unbindService(mConnection);
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

            mService = new Messenger(service);
            mBound = true;
            Message msg = Message.obtain(null, MyService.MSG_SAY_HELLO);
            msg.replyTo = mMessenger;
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.i("Service1", "connected");
            Log.i("Service1", str +"_");

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            mService = null;
            mBound = false;
        }
    };


    private void sendString(){
        Message msg = Message.obtain(null, MyService.MSG_SAVE_STR);
        Bundle b = new Bundle();
        b.putString("str", text.getText().toString());
        msg.setData(b);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }




}
