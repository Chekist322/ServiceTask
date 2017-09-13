package com.example.batrakov.servicetask;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


public class MyService extends Service {



    static final int MSG_SAY_HELLO = 1;
    static final int MSG_SAVE_STR = 2;
    private String str;
    int i = 0;
    Messenger mActivity = null;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    mActivity = msg.replyTo;
                    changeStr();
                    break;
                case MSG_SAVE_STR:
                    str = msg.getData().getString("str");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }



    public MyService(){
        str = "sample";
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("Destroy", "destroyed");
        super.onDestroy();
    }

    @Override
    public void onCreate() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher);
        Notification notification;
        notification = builder.build();
        startForeground(777, notification);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Log.i("Service2", String.valueOf(i++));
                    try {
                        synchronized (this) {
                            this.wait(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
        super.onCreate();
    }


    public void changeStr(){
        Message msg = Message.obtain(null, MainActivity.MSG_CHANGE_STR);
        try {
            Log.i("str", str);
            Bundle b = new Bundle();
            b.putString("str", str);
            msg.setData(b);
            mActivity.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /** methods for clients */
    public String getString(){
        return str;
    }

    public void setString(String newStr){
        str = newStr;
    }
    }
