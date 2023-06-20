package com.myatminsoe.mkeyboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import java.util.List;

public class MyKeyboardView extends KeyboardView {

    Context context;
    AttributeSet attrs;
    SharedPreferences settings;

    public MyKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable dr;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
        List<Keyboard.Key> keys = getKeyboard().getKeys();
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        int currentTheme = settings.getInt("theme", 0);

        if (currentTheme < 2 || isEmoji) {
            paint.setTextSize(sptopx(23, context));
            for (Keyboard.Key key : keys) {
                if (key.codes[0] == 32) {
                    dr = context.getResources().getDrawable(R.drawable.space);
                    dr.setBounds(key.x + dpToPx(6), key.y + dpToPx(6), key.x + key.width - dpToPx(6), key.y + key.height - dpToPx(6));
                    dr.draw(canvas);
                } else if (key.codes[0] == -4) {
                    int width = key.width;
                    int height = key.height;
                    int left = key.x + width / 2 - height / 2;
                    int top = key.y;
                    int right = left + height;
                    int bottom = top + height;
                    int padding = dpToPx(5);
                    dr = context.getResources().getDrawable(R.drawable.circle);
                    dr.setBounds(left + padding, top + padding, right - padding, bottom - padding);
                    dr.draw(canvas);
                    padding = dpToPx(6);
                    key.icon.setBounds(left + padding, top + padding, right - padding, bottom - padding);
                    key.icon.draw(canvas);
                }
            }
        } else {
            for (Keyboard.Key key : keys)
                if (key.codes[0] == -4) {
                    int width = key.width;
                    int height = key.height;
                    int left = key.x + width / 2 - height / 2;
                    int top = key.y;
                    int right = left + height;
                    int bottom = top + height;
                    dr = context.getResources().getDrawable(R.drawable.key_blue);
                    int padding = 0;
                    if (settings.getBoolean("height", false)) {
                        padding = dpToPx(10);
                    }
                    dr.setBounds(key.x + padding, key.y + padding, key.x + width - padding, key.y + height - padding);
                    dr.draw(canvas);
                    padding += dpToPx(6);
                    key.icon.setBounds(left + padding, top + padding, right - padding, bottom - padding);
                    key.icon.draw(canvas);
                }
        }
    }

    private boolean isEmoji = false;

    public void setKeyboard(MyKeyboard keyboard, EditorInfo info) {
        keyboard.setImeOptions(getResources(), info.imeOptions);
        this.isEmoji = false;
        keyboard.setEmoji(false);
        super.setKeyboard(keyboard);
    }

    public void setKeyboard(MyKeyboard keyboard, boolean b) {
        keyboard.setEmoji(true);
        keyboard.setImeOptions(getResources(), EditorInfo.IME_ACTION_DONE);
        this.isEmoji = true;
        super.setKeyboard(keyboard);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static float sptopx(float sp, Context context) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scaledDensity;
    }
}