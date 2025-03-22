package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class StopwatchService extends Service {
    private static final String TAG = "StopwatchService";
    private static final String PREFS_NAME = "StopwatchPrefs";
    private static final String KEY_ELAPSED_TIME = "elapsedTime";
    private static final String KEY_START_TIME = "startTime";
    private static final int NOTIFICATION_ID = 1;

    private Handler handler = new Handler(Looper.getMainLooper());
    private long startTime = 0;
    private long elapsedTime = 0;
    private boolean isRunning = false;
    private boolean isReceiverRegistered = false;

    private BroadcastReceiver resetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handler.removeCallbacks(runnable);
            elapsedTime = 0;
            startTime = System.currentTimeMillis();
            saveElapsedTime(elapsedTime);
            sendTimeUpdate(elapsedTime);
            stopSelf();
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                elapsedTime = System.currentTimeMillis() - startTime;

                String formattedTime = formatTime(elapsedTime);

                Notification notification = createNotification("Автомобиль открыт: " + formattedTime);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(NOTIFICATION_ID, notification);

                sendTimeUpdate(elapsedTime);
                saveElapsedTime(elapsedTime);
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter("com.example.RESET_TIME");
        registerReceiver(resetReceiver, filter);
        isReceiverRegistered = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        elapsedTime = prefs.getLong(KEY_ELAPSED_TIME, 0);
        startTime = prefs.getLong(KEY_START_TIME, System.currentTimeMillis() - elapsedTime);
        isRunning = true;
        startForeground(NOTIFICATION_ID, createNotification("Автомобиль открыт: 00:00:00"));
        handler.post(runnable);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        handler.removeCallbacksAndMessages(null);
        stopForeground(true);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
        manager.cancelAll();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_ELAPSED_TIME, 0);
        editor.putLong(KEY_START_TIME, System.currentTimeMillis());
        editor.apply();

        if (isReceiverRegistered) {
            unregisterReceiver(resetReceiver);
            isReceiverRegistered = false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void saveElapsedTime(long elapsedTime) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_ELAPSED_TIME, elapsedTime);
        editor.putLong(KEY_START_TIME, startTime);
        editor.apply();
    }

    private void sendTimeUpdate(long elapsedTime) {
        Intent intent = new Intent("com.example.UPDATE_TIME");
        intent.putExtra("elapsedTime", elapsedTime);
        sendBroadcast(intent);
    }

    private Notification createNotification(String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "stopwatch_service",
                    "Stopwatch Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Stopwatch Service Channel");
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(this, "stopwatch_service")
                .setContentTitle("CarSharing")
                .setContentText(content)
                .setSmallIcon(R.drawable.car_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.car_icon))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();
    }

    public String formatTime(long milliseconds) {
        int hours = (int) (milliseconds / 3600000);
        int minutes = (int) (milliseconds % 3600000) / 60000;
        int seconds = (int) (milliseconds % 60000) / 1000;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
