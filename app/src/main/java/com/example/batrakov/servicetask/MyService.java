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

    static final int MSG_CONNECT = 1;
    static final int MSG_SAVE_STR = 2;
    private static String str;
    private static Messenger activityMessenger = null;
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    private static class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CONNECT:
                    activityMessenger = msg.replyTo;
                    changeString();
                    break;
                case MSG_SAVE_STR:
                    str = msg.getData().getString("str");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public MyService() {
        str = "sample";
    }

    @Override
    public void onCreate() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher);
        Notification notification;
        notification = builder.build();
        startForeground(777, notification);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }

    private static void changeString() {
        Message msg = Message.obtain(null, MainActivity.MSG_CHANGE_STR);
        try {
            Log.i("str", str);
            Bundle b = new Bundle();
            b.putString("str", str);
            msg.setData(b);
            activityMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
