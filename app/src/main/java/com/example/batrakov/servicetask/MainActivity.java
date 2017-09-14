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
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    static final int MSG_CHANGE_STR = 1;
    private final Messenger mMessenger = new Messenger(new IncomingHandler());
    private Messenger serviceMessenger = null;
    private boolean mBound = false;
    private EditText textField;
    private Intent intent;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBound = true;
            serviceMessenger = new Messenger(service);
            Message msg = Message.obtain(null, MyService.MSG_CONNECT);
            msg.replyTo = mMessenger;
            try {
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceMessenger = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textField = (EditText) findViewById(R.id.editText);
        intent = new Intent(this, MyService.class);
        startService(intent);
    }

    @Override
    protected void onStart() {
        if (!mBound) {
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        sendString();
        if (mBound) {
            unbindService(serviceConnection);
            mBound = false;
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        if (!mBound) {
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        super.onResume();
    }

    private void sendString() {
        Message msg = Message.obtain(null, MyService.MSG_UPDATE_STR);
        try {
            Bundle b = new Bundle();
            b.putString("str", textField.getText().toString());
            msg.setData(b);
            serviceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CHANGE_STR:
                    if (msg.getData().getString("str") != null) {
                        textField.setText(msg.getData().getString("str"));
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
