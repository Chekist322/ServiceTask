package com.example.batrakov.servicetask;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class MyService extends Service {

    private final IBinder mBinder = new LocalBinder();
    String str;
    int i = 0;

    public MyService(){
        str = "sample";
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


    public class LocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    /** methods for clients */
    public String getString(){
        return str;
    }

    public void setString(String newStr){
        str = newStr;
    }
}
