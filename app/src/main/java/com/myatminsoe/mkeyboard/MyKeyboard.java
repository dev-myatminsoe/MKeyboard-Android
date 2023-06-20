package com.myatminsoe.mkeyboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.inputmethodservice.Keyboard;
import android.preference.PreferenceManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

public class MyKeyboard extends Keyboard {

    private Context context;
    private int resid;
    private SharedPreferences settings;

    public static final int KEYCODE_NUMPAD = -2;
    public static final int KEYCODE_SWITCH = -100;
    public static final int KEYCODE_EMOJIKB = -200;
    public static final int KEYCODE_SPACE = 32;
    public static final int KEYCODE_PREV = -201;
    public static final int KEYCODE_NEXT = -202;

    public MyKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);

        this.context = context;
        this.resid = xmlLayoutResId;
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        if (settings.getBoolean("height", false)) {
            changeKeyHeight(1.8);
        }
    }

    public void changeKeyHeight(double height_modifier) {
        int height = 0;
        for (Key key : getKeys()) {
            key.height *= height_modifier;
            key.y *= height_modifier;
            height = key.height;
        }
        setKeyHeight(height);
        getNearestKeys(0, 0); //somehow adding this fixed a weird bug where bottom row keys could not be pressed if keyboard height is too tall.. from the Keyboard source code seems like calling this will recalculate some values used in keypress detection calculation
    }

    private boolean emoji = false;

    public void setEmoji(boolean b) {
       /* try {
            if (!MainActivity.drm.Arm()) {
                showToast("Please purchase to support developer or you can still use the free version.");
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                getHeight();
            }
        } catch (DRM.AuthenticationException ex) {*/
        this.emoji = b;
        //}
    }

    @Override
    public int getHeight() {
        if (settings.getBoolean("height", false)) {
            if (emoji) {
                return (getKeyHeight() * 5);
            }
            return (getKeyHeight() * 4) + getKeyHeight() / 2;
        } else {
            return super.getHeight();
        }
    }

    @Override
    protected Key createKeyFromXml(Resources res, Row parent, int x, int y, XmlResourceParser parser) {
        Key key = new Key(res, parent, x, y, parser);
        if (key.codes[0] == -4) {
            mEnterKey = key;
        }
        if (key.codes[0] == -100) {
            mSwitchKey = key;
        }
        if (key.codes[0] == -5) {
            mDelKey = key;
        }
        if (key.codes[0] == -1) {
            mShiftKey = key;
        }
        if (key.codes[0] == -200) {
            mEmojiKey = key;
        }
        if (key.codes[0] == -5005) {
            mDoubleKey = key;
        }
        return key;
    }

    private Key mEnterKey, mDelKey, mShiftKey, mSwitchKey, mEmojiKey, mDoubleKey;

    void setImeOptions(Resources res, int options) {

        int currentTheme = settings.getInt("theme", 0);
        if (currentTheme == 1 || currentTheme == 3) {
            if (mDelKey != null) {
                mDelKey.icon = res.getDrawable(R.drawable.sym_keyboard_delete_lxx_dark);
                mDelKey.label = null;
            }
            if (mShiftKey != null) {
                mShiftKey.icon = res.getDrawable(R.drawable.sym_keyboard_shift_lxx_dark);
                mShiftKey.label = null;
            }
            if (mSwitchKey != null) {
                mSwitchKey.icon = res.getDrawable(R.drawable.sym_keyboard_language_switch_lxx_dark);
                mSwitchKey.label = null;
            }
            if (mEmojiKey != null) {
                mEmojiKey.icon = res.getDrawable(R.drawable.sym_keyboard_smiley_lxx_dark);
                mEmojiKey.label = null;
            }
            if (mDoubleKey != null) {
                mDoubleKey.icon = res.getDrawable(R.drawable.sym_keyboard_double_dark);
                mDoubleKey.label = null;
            }
        }

        if (mEnterKey != null) {
            switch (options & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {

                case EditorInfo.IME_ACTION_DONE:
                    mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_done);
                    mEnterKey.label = null;
                    break;
                case EditorInfo.IME_ACTION_GO:
                    mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_go);
                    mEnterKey.label = null;
                    break;
                case EditorInfo.IME_ACTION_NEXT:
                    mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_next);
                    mEnterKey.label = null;
                    break;
                case EditorInfo.IME_ACTION_PREVIOUS:
                    mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_previous);
                    mEnterKey.label = null;
                    break;
                case EditorInfo.IME_ACTION_SEARCH:
                    mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_search);
                    mEnterKey.label = null;
                    break;
                case EditorInfo.IME_ACTION_SEND:
                    mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_send);
                    mEnterKey.label = null;
                    break;
                default:
                    mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_return);
                    mEnterKey.label = null;
                    break;
            }
        }
    }

    Toast m_currentToast;

    public void showToast(String text) {
        if (m_currentToast == null) {
            m_currentToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        }
        m_currentToast.setText(text);
        m_currentToast.setDuration(Toast.LENGTH_LONG);
        m_currentToast.show();
    }
}
