package com.myatminsoe.mkeyboard;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.regex.Pattern;

public class Background extends Service {

    String pasteData = "";
    SharedPreferences settings;
    ClipboardManager clipboard;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(Build.VERSION.SDK_INT + "", "copied");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            settings = PreferenceManager.getDefaultSharedPreferences(this);
            settings.edit().putBoolean("copycon", true).apply();
            clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipboardManager.OnPrimaryClipChangedListener mPrimaryChangeListener = new ClipboardManager.OnPrimaryClipChangedListener() {
                public void onPrimaryClipChanged() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                        pasteData = item.getText() + "";
                        if (settings.getBoolean("copycon", false) && Pattern.compile("[က-အ]").matcher(pasteData).find()) {
                            displayNotification();
                        }
                    }
                }

            };
            clipboard.addPrimaryClipChangedListener(mPrimaryChangeListener);

        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        cancelNotification();
        settings.edit().putBoolean("copycon", false).apply();
        super.onDestroy();
    }

    protected void displayNotification() {
        Log.i("Start", "notification");

      /* Invoking the default notification service */
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setContentTitle("M Keyboard Converter");
        mBuilder.setContentText("Touch here to convert your text");
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setTicker("Touch to convert");
        mBuilder.setOngoing(false);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(notification);
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("isNoti", true);
        if (Build.VERSION.SDK_INT >= 16) {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);

      /* Adds the Intent that starts the Activity to the top of the stack */
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);

        }
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificationID, mBuilder.build());
    }

    protected void cancelNotification() {
        displayNotification();
        mNotificationManager.cancel(notificationID);
    }

    private NotificationManager mNotificationManager;
    private int notificationID = 100;


}
