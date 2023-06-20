package com.myatminsoe.mkeyboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootCompletedIntentReceiver extends BroadcastReceiver {

    public static final String PREFS_NAME = "MMFontConverterPREFS";
    SharedPreferences settings;

    @Override
    public void onReceive(Context context, Intent intent) {
        settings = context.getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean("copycon", false)) {
            context.startService(new Intent(context, Background.class));
        }
    }
}
