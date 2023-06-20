package com.myatminsoe.mkeyboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import com.myatminsoe.smartzawgyi.SmartZawgyi;

import java.util.ArrayList;

import mm.technomation.dinga.DRM;

public class MyatMinIME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    public static final int eng_keyboard = 0;
    public static final int zg_keyboard = 1;
    public static final int uni_keyboard = 2;

    private MyKeyboardView kv;
    private boolean isTabbed = false;
    private boolean isShift = false;
    private boolean isSpace = false;
    private boolean isDown = false;
    private MyKeyboard keyboard;
    LocalBroadcastManager broadcaster;
    private int currentKeyboard = 0;
    private int language = 0;
    private Vibrator vi;
    public Toast m_currentToast = null;
    private boolean caps = false;
    InputConnection ic;
    private String txt = "";
    private String faketxt = "";
    ArrayList<Integer> languages = new ArrayList<>();
    SharedPreferences settings;

    private int emojipage = 0;
    EditorInfo info;

    @Override
    public void onPress(int primaryCode) {

        if (primaryCode == -4 || primaryCode == -2 || primaryCode == -32 || primaryCode == -1 || primaryCode == 32 || primaryCode == -5005 || primaryCode == -100 || primaryCode == MyKeyboard.KEYCODE_PREV || primaryCode == MyKeyboard.KEYCODE_NEXT) {
            kv.setPreviewEnabled(false);
        }

        if (settings.getBoolean("sound", true)) {
            playClick(primaryCode);
        }

        if (settings.getBoolean("vibrate", true)) {
            vi.vibrate(settings.getInt("vibDuration", 10));
        }

        if (!settings.getBoolean("popup", true)) {
            kv.setPreviewEnabled(false);
        }
    }

    @Override
    public void onRelease(int primaryCode) {
        if (settings.getBoolean("key_popup", true)) {
            kv.setPreviewEnabled(true);
        }
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeUp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        vi = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        broadcaster = LocalBroadcastManager.getInstance(getApplicationContext());
        sendServiceState(true);
        MainActivity.z(getApplicationContext());
        try {
            if (!MainActivity.drm.Arm()) {
                showToast("Please purchase to support developer or you can still use the free version.");
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } catch (DRM.AuthenticationException ex) {
            isCrack = true;
        }
    }

    boolean isCrack = false;

    @Override
    public View onCreateInputView() {

        int currentTheme = settings.getInt("theme", 0);
        switch (currentTheme) {
            case 0:
                kv = (MyKeyboardView) getLayoutInflater().inflate(R.layout.material_light, null);
                break;
            case 1:
                kv = (MyKeyboardView) getLayoutInflater().inflate(R.layout.material_dark, null);
                break;
            case 2:
                kv = (MyKeyboardView) getLayoutInflater().inflate(R.layout.ios_light, null);
                break;
            case 3:
                kv = (MyKeyboardView) getLayoutInflater().inflate(R.layout.ios_dark, null);
                break;
        }
        keyboard = new MyKeyboard(this, R.xml.qwerty);
        if (info != null) {
            kv.setKeyboard(keyboard, info);
        } else {
            kv.setKeyboard(keyboard);
        }
        kv.setOnKeyboardActionListener(this);
        kv.invalidateAllKeys();

        return kv;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        if (isCrack) {
            super.onStartInputView(info, restarting);
            this.info = info;
            setInputView(onCreateInputView());
            getLanguages();
        }
        currentKeyboard = 0;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

        ic = getCurrentInputConnection();
        switch (language) {
            case eng_keyboard:

                handleEng(primaryCode);
                break;
            case zg_keyboard:

                handleZg(primaryCode);
                break;
            case uni_keyboard:

                handleUni(primaryCode);
                break;
            case 3:
                handleEmoji(primaryCode);
        }
        //}
    }

    private void handleEmoji(int primaryCode) {
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(2, 0);
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_ENTER));
                break;
            case MyKeyboard.KEYCODE_SPACE:
                ic.commitText(" ", 1);
                break;
            case MyKeyboard.KEYCODE_PREV:
                emojipage--;
                if (emojipage == -1) emojipage = 2;
                switch (emojipage) {
                    case 0:
                        keyboard = new MyKeyboard(this, R.xml.emoji_one);
                        kv.setKeyboard(keyboard, true);
                        kv.invalidateAllKeys();
                        break;
                    case 1:
                        keyboard = new MyKeyboard(this, R.xml.emoji_two);
                        kv.setKeyboard(keyboard, true);
                        kv.invalidateAllKeys();
                        break;
                    case 2:
                        keyboard = new MyKeyboard(this, R.xml.emoji_three);
                        kv.setKeyboard(keyboard, true);
                        kv.invalidateAllKeys();
                        break;
                }
                break;
            case MyKeyboard.KEYCODE_NEXT:
                emojipage++;
                if (emojipage == 3) emojipage = 0;
                switch (emojipage) {
                    case 0:
                        keyboard = new MyKeyboard(this, R.xml.emoji_one);
                        kv.setKeyboard(keyboard, true);
                        kv.invalidateAllKeys();
                        break;
                    case 1:
                        keyboard = new MyKeyboard(this, R.xml.emoji_two);
                        kv.setKeyboard(keyboard, true);
                        kv.invalidateAllKeys();
                        break;
                    case 2:
                        keyboard = new MyKeyboard(this, R.xml.emoji_three);
                        kv.setKeyboard(keyboard, true);
                        kv.invalidateAllKeys();
                        break;
                }
                break;
            case MyKeyboard.KEYCODE_SWITCH:

                setInputView(changeEng());
                language = 0;
                break;
            default:
                String st = MyEmoji.get(primaryCode);
                ic.commitText(st, 1);
                break;
        }
    }

    private void handleEng(int primaryCode) {
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                isDown = false;
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_ENTER));
                break;
            case Keyboard.KEYCODE_SHIFT:
                if (currentKeyboard == 0) {
                    keyboard = new MyKeyboard(this, R.xml.qwerty);
                    kv.setKeyboard(keyboard, info);
                    if (isShift) {
                        caps = false;
                        isShift = false;
                        isTabbed = false;
                        keyboard.setShifted(false);
                        kv.invalidateAllKeys();

                    } else if (isTabbed) {
                        isTabbed = false;
                        caps = true;
                        isShift = true;
                        keyboard.setShifted(true);
                        kv.invalidateAllKeys();
                    } else {
                        isShift = false;
                        isTabbed = true;
                        caps = !caps;
                        keyboard.setShifted(caps);
                        kv.invalidateAllKeys();
                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isTabbed = false;
                            }
                        }, 500);
                    }

                } else if (currentKeyboard == 1) {
                    clear();
                    keyboard = new MyKeyboard(this, R.xml.qwerty_two);
                    kv.setKeyboard(keyboard, info);
                    kv.invalidateAllKeys();
                    currentKeyboard = 2;
                } else if (currentKeyboard == 2) {
                    clear();
                    keyboard = new MyKeyboard(this, R.xml.qwerty_one);
                    kv.setKeyboard(keyboard, info);
                    kv.invalidateAllKeys();
                    currentKeyboard = 1;
                }
                break;
            case MyKeyboard.KEYCODE_NUMPAD:
                if (currentKeyboard == 0) {
                    keyboard = new MyKeyboard(this, R.xml.qwerty_one);
                    kv.setKeyboard(keyboard, info);
                    kv.invalidateAllKeys();
                    currentKeyboard = 1;
                    clear();
                } else {
                    keyboard = new MyKeyboard(this, R.xml.qwerty);
                    kv.setKeyboard(keyboard, info);
                    kv.invalidateAllKeys();
                    currentKeyboard = 0;
                    clear();
                }
                break;
            case MyKeyboard.KEYCODE_SPACE:
                if (!isSpace) {
                    ic.commitText(" ", 1);
                    isSpace = true;
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isSpace = false;
                        }
                    }, 500);
                } else {
                    ic.deleteSurroundingText(1, 0);
                    ic.commitText(". ", 1);
                    caps = true;
                    keyboard.setShifted(true);
                    kv.invalidateAllKeys();
                    isSpace = false;
                }
                if (currentKeyboard > 0) {
                    keyboard = new MyKeyboard(this, R.xml.qwerty);
                    kv.setKeyboard(keyboard, info);
                    kv.invalidateAllKeys();
                    currentKeyboard = 0;
                    clear();
                }
                break;
            case MyKeyboard.KEYCODE_SWITCH:
                isSpace = false;
                int index = languages.indexOf(eng_keyboard);
                if (index + 1 >= languages.size()) {
                    index = -1;
                }
                language = languages.get(index + 1);
                currentKeyboard = 0;
                createLayout(language);
                kv.setKeyboard(keyboard, info);
                kv.invalidateAllKeys();
                isDown = false;
                clear();
                txt = "";
                faketxt = "";
                break;
            case MyKeyboard.KEYCODE_EMOJIKB:
                isSpace = false;
                clear();
                isDown = false;
                setInputView(changeEmoji());
                language = 3;
                break;
            default:
                isSpace = false;
                char code = (char) primaryCode;
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                if (!isShift) {
                    caps = false;
                    keyboard.setShifted(false);
                    kv.invalidateAllKeys();
                }
                ic.commitText(String.valueOf(code), 1);
                break;
        }
    }

    private void handleZg(int primaryCode) {
        if ((ic.getTextBeforeCursor(1, 0) + "").equals("")) {
            txt = "";
        }
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                if (txt.length() > 0) {
                    txt = txt.substring(0, txt.length() - 1);
                    faketxt = SmartZawgyi.smartZawgyi(txt);
                    ic.setComposingText(faketxt, 1);
                } else {
                    ic.deleteSurroundingText(1, 0);
                }
                isDown = false;
                break;
            case Keyboard.KEYCODE_DONE:
                txt = SmartZawgyi.smartZawgyi(txt);
                ic.setComposingText(txt, 1);
                txt = "";
                ic.finishComposingText();
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_ENTER));
                break;
            case Keyboard.KEYCODE_SHIFT:
                if (currentKeyboard == 0) {
                    if (!isShift) {
                        keyboard = new MyKeyboard(this, R.xml.zawgyi_shift);
                        kv.setKeyboard(keyboard, info);
                        kv.invalidateAllKeys();
                        isShift = !isShift;

                    } else {
                        keyboard = new MyKeyboard(this, R.xml.zawgyi);
                        kv.setKeyboard(keyboard, info);
                        kv.invalidateAllKeys();
                        isShift = !isShift;

                    }
                } else if (currentKeyboard == 1) {
                    clear();
                    keyboard = new MyKeyboard(this, R.xml.zawgyi_two);
                    kv.setKeyboard(keyboard, info);
                    kv.invalidateAllKeys();
                    currentKeyboard = 2;
                } else if (currentKeyboard == 2) {
                    clear();
                    keyboard = new MyKeyboard(this, R.xml.zawgyi_one);
                    kv.setKeyboard(keyboard, info);
                    kv.invalidateAllKeys();
                    currentKeyboard = 1;
                }
                break;
            case MyKeyboard.KEYCODE_SPACE:
                ic.finishComposingText();
                txt = "";
                if (!isSpace) {
                    ic.commitText(" ", 1);
                    isSpace = true;
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isSpace = false;
                        }
                    }, 500);
                } else {
                    ic.deleteSurroundingText(1, 0);
                    ic.commitText("။ ", 1);
                    kv.invalidateAllKeys();
                    isSpace = false;
                }
                if (currentKeyboard > 0) {
                    keyboard = new MyKeyboard(this, R.xml.zawgyi);
                    kv.setKeyboard(keyboard, info);
                    kv.invalidateAllKeys();
                    currentKeyboard = 0;
                    clear();
                }
                break;
            case MyKeyboard.KEYCODE_NUMPAD:
                if (currentKeyboard == 0) {
                    keyboard = new MyKeyboard(this, R.xml.zawgyi_one);
                    kv.setKeyboard(keyboard, info);
                    kv.invalidateAllKeys();
                    currentKeyboard = 1;
                    clear();
                } else {
                    keyboard = new MyKeyboard(this, R.xml.zawgyi);
                    kv.setKeyboard(keyboard, info);
                    kv.invalidateAllKeys();
                    currentKeyboard = 0;
                    clear();
                }
                break;
            case -5000:
                txt += "ွ်";
                txt = SmartZawgyi.smartZawgyi(txt);
                ic.setComposingText(txt, 1);
                keyboard = new MyKeyboard(this, R.xml.zawgyi);
                kv.setKeyboard(keyboard, info);
                kv.invalidateAllKeys();
                break;
            case -5001:
                txt += "ၽႊ";
                txt = SmartZawgyi.smartZawgyi(txt);
                ic.setComposingText(txt, 1);
                keyboard = new MyKeyboard(this, R.xml.zawgyi);
                kv.setKeyboard(keyboard, info);
                kv.invalidateAllKeys();
                break;
            case -5002:
                txt += "ၽြ";
                txt = SmartZawgyi.smartZawgyi(txt);
                ic.setComposingText(txt, 1);
                keyboard = new MyKeyboard(this, R.xml.zawgyi);
                kv.setKeyboard(keyboard, info);
                kv.invalidateAllKeys();
                break;
            case MyKeyboard.KEYCODE_SWITCH:
                isSpace = false;
                isDown = false;
                currentKeyboard = 0;
                ic.finishComposingText();
                int index = languages.indexOf(zg_keyboard);
                if (index + 1 >= languages.size()) {
                    index = -1;
                }
                language = languages.get(index + 1);
                currentKeyboard = 0;
                createLayout(language);
                kv.setKeyboard(keyboard, info);
                kv.invalidateAllKeys();
                clear();
                txt = "";
                faketxt = "";
                break;
            case -5005:
                isDown = true;
                break;
            case 4145:
                txt += "ေ";
                ic.setComposingText(txt, 1);
                break;
            case 4155:
                txt += "ျ";
                ic.setComposingText(txt, 1);
                break;
            case 4241:
                txt += (char) 4241;
                ic.setComposingText(txt, 1);
                txt = "";
                ic.finishComposingText();
                break;
            default:
                isSpace = false;
                char code = (char) primaryCode;
                if (!isDown) {
                    txt += String.valueOf(code);
                    txt = SmartZawgyi.smartZawgyi(txt);
                    ic.setComposingText(txt, 1);
                } else {
                    if (!setDown(code).equals("invalid")) {
                        txt += setDown(code);
                        txt = SmartZawgyi.smartZawgyi(txt);
                        ic.setComposingText(txt, 1);
                    }
                    isDown = false;
                }
                if (isShift) {
                    isShift = false;
                    keyboard = new MyKeyboard(this, R.xml.zawgyi);
                    kv.setKeyboard(keyboard, info);
                    kv.invalidateAllKeys();
                }
                break;
        }
    }

    private void handleUni(int primaryCode) {
        if ((ic.getTextBeforeCursor(1, 0) + "").equals("")) {
            txt = "";
            faketxt = "";
        }

        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                if (txt.length() > 0) {
                    txt = txt.substring(0, txt.length() - 1);
                    faketxt = SmartZawgyi.smartUni(txt);
                    ic.setComposingText(faketxt, 1);
                } else {
                    txt = "";
                    faketxt = "";
                    ic.finishComposingText();
                    ic.deleteSurroundingText(1, 0);
                }
                isDown = false;
                break;
            case Keyboard.KEYCODE_DONE:
                txt = SmartZawgyi.smartUni(txt);
                ic.setComposingText(txt, 1);
                ic.finishComposingText();
                txt = "";
                faketxt = "";
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_ENTER));
                break;
            case Keyboard.KEYCODE_SHIFT:
                if (currentKeyboard == 0) {
                    if (!isShift) {
                        keyboard = new MyKeyboard(this, R.xml.mm_unicode_shift);
                        kv.setKeyboard(keyboard, info);
                        kv.invalidateAllKeys();
                        isShift = !isShift;

                    } else {
                        keyboard = new MyKeyboard(this, R.xml.mm_unicode);
                        kv.setKeyboard(keyboard, info);
                        kv.invalidateAllKeys();
                        isShift = !isShift;

                    }
                } else if (currentKeyboard == 1) {
                    clear();
                    keyboard = new MyKeyboard(this, R.xml.mm_unicode_two);
                    kv.setKeyboard(keyboard, info);
                    kv.invalidateAllKeys();
                    currentKeyboard = 2;
                } else if (currentKeyboard == 2) {
                    clear();
                    keyboard = new MyKeyboard(this, R.xml.mm_unicode_one);
                    kv.setKeyboard(keyboard, info);
                    kv.invalidateAllKeys();
                    currentKeyboard = 1;
                }
                break;
            case MyKeyboard.KEYCODE_SPACE:
                txt = SmartZawgyi.smartUni(txt);
                ic.setComposingText(txt, 1);
                ic.finishComposingText();
                txt = "";
                faketxt = "";
                if (!isSpace) {
                    ic.commitText(" ", 1);
                    isSpace = true;
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isSpace = false;
                        }
                    }, 500);
                } else {
                    ic.deleteSurroundingText(1, 0);
                    ic.commitText("။ ", 1);
                    kv.invalidateAllKeys();
                    isSpace = false;
                }
                if (currentKeyboard > 0) {
                    keyboard = new MyKeyboard(this, R.xml.mm_unicode);
                    kv.setKeyboard(keyboard, info);
                    kv.invalidateAllKeys();
                    currentKeyboard = 0;
                    clear();
                }
                break;
            case MyKeyboard.KEYCODE_NUMPAD:
                if (currentKeyboard == 0) {
                    keyboard = new MyKeyboard(this, R.xml.mm_unicode_one);
                    kv.setKeyboard(keyboard, info);
                    kv.invalidateAllKeys();
                    currentKeyboard = 1;
                    clear();
                } else {
                    keyboard = new MyKeyboard(this, R.xml.mm_unicode);
                    kv.setKeyboard(keyboard, info);
                    kv.invalidateAllKeys();
                    currentKeyboard = 0;
                    clear();
                }
                break;
            case MyKeyboard.KEYCODE_SWITCH:
                isSpace = false;
                isDown = false;
                currentKeyboard = 0;
                ic.finishComposingText();
                int index = languages.indexOf(uni_keyboard);
                if (index + 1 >= languages.size()) {
                    index = -1;
                }
                language = languages.get(index + 1);
                currentKeyboard = 0;
                createLayout(language);
                kv.setKeyboard(keyboard, info);
                kv.invalidateAllKeys();
                clear();
                txt = "";
                faketxt = "";
                break;
            case -5005:
                isDown = true;
                break;
            case 4153:
                txt += "္";
                txt = SmartZawgyi.smartUni(txt);
                ic.setComposingText(txt, 1);
                ic.finishComposingText();
                txt = "";
                faketxt = "";
                break;
            case 4152:
                txt += "း";
                txt = SmartZawgyi.smartUni(txt);
                ic.setComposingText(txt, 1);
                ic.finishComposingText();
                txt = "";
                faketxt = "";
                break;
            default:
                isSpace = false;
                char code = (char) primaryCode;
                if (!isDown) {
                    txt += String.valueOf(code);
                    faketxt += String.valueOf(code);
                    faketxt = SmartZawgyi.smartUni(txt);
                    ic.setComposingText(faketxt, 1);
                } else {
                    if (!setDown(code).equals("invalid")) {
                        txt += setDown(code);
                        faketxt = SmartZawgyi.smartUni(txt);
                        ic.setComposingText(faketxt, 1);
                    }
                    isDown = false;
                }
                if (isShift) {
                    isShift = false;
                    keyboard = new MyKeyboard(this, R.xml.mm_unicode);
                    kv.setKeyboard(keyboard, info);
                    kv.invalidateAllKeys();
                }
        }
    }

    private View changeEmoji() {
        int currentTheme = settings.getInt("theme", 0);
        if (currentTheme == 1 || currentTheme == 3) {
            kv = (MyKeyboardView) getLayoutInflater().inflate(R.layout.material_dark, null);
        } else {
            kv = (MyKeyboardView) getLayoutInflater().inflate(R.layout.material_light, null);
        }
        keyboard = new MyKeyboard(this, R.xml.emoji_one);
        kv.setKeyboard(keyboard, true);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    private View changeEng() {
        int currentTheme = settings.getInt("theme", 0);
        switch (currentTheme) {
            case 0:
                kv = (MyKeyboardView) getLayoutInflater().inflate(R.layout.material_light, null);
                break;
            case 1:
                kv = (MyKeyboardView) getLayoutInflater().inflate(R.layout.material_dark, null);
                break;
            case 2:
                kv = (MyKeyboardView) getLayoutInflater().inflate(R.layout.ios_light, null);
                break;
            case 3:
                kv = (MyKeyboardView) getLayoutInflater().inflate(R.layout.ios_dark, null);
                break;
        }
        keyboard = new MyKeyboard(this, R.xml.qwerty);
        kv.setKeyboard(keyboard, info);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    public void clear() {
        isTabbed = false;
        isShift = false;
        caps = false;
    }

    private String setDown(char c) {

        switch (c) {
            case '\u1000':
                return "\u1060";
            case '\u1001':
                return "\u1061";
            case '\u1002':
                return "\u1062";
            case '\u1003':
                return "\u1063";
            case '\u1004':
                return "\u1064";
            case '\u1005':
                return "\u1065";
            case '\u1006':
                return "\u1067";
            case '\u1007':
                return "\u1068";
            case '\u1008':
                return "\u1069";
            case '\u100F':
                return "\u1070";
            case '\u1010':
                return "\u1072";
            case '\u1011':
                return "\u1074";
            case '\u1012':
                return "\u1075";
            case '\u1013':
                return "\u1076";
            case '\u1014':
                return "\u1077";
            case '\u1015':
                return "\u1078";
            case '\u1016':
                return "\u1079";
            case '\u1017':
                return "\u107A";
            case '\u1018':
                return "\u107B";
            case '\u1019':
                return "\u107C";
            case '\u101E':
                return "\u1086";
            default:
                return "invalid";
        }
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        txt = "";
        super.onFinishInputView(finishingInput);
    }

    private void getLanguages() {
        boolean eng = settings.getBoolean("eng", true);
        boolean zawgyi = settings.getBoolean("zawgyi", true);
        boolean unicode = settings.getBoolean("unicode", true);
        languages.clear();
        if (eng) {
            languages.add(eng_keyboard);
        }
        if (zawgyi) {
            languages.add(zg_keyboard);
        }
        if (unicode) {
            languages.add(uni_keyboard);
        }
        if (languages.size() == 0) {
            languages.add(eng_keyboard);
        }
        int i = languages.get(0);
        language = i;
        createLayout(i);

    }

    private void createLayout(int i) {
        int xml = R.xml.qwerty;
        switch (i) {
            case eng_keyboard:
                xml = R.xml.qwerty;
                break;
            case zg_keyboard:
                xml = R.xml.zawgyi;
                break;
            case uni_keyboard:
                xml = R.xml.mm_unicode;
                break;
        }
        keyboard = new MyKeyboard(this, xml);
        keyboard.setImeOptions(getResources(), info.imeOptions);
        kv.setKeyboard(keyboard, info);
        kv.invalidateAllKeys();
    }

    @Override
    public void onUpdateExtractingVisibility(EditorInfo ei) {
        ei.imeOptions |= EditorInfo.IME_FLAG_NO_EXTRACT_UI;
        super.onUpdateExtractingVisibility(ei);

    }

    private void playClick(int keyCode) {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        switch (keyCode) {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    @Override
    public void onDestroy() {
        sendServiceState(false);
        super.onDestroy();
    }

    private void sendServiceState(boolean paramBoolean) {
        Intent intent = new Intent("keyboardRunning");
        intent.putExtra("running", paramBoolean);
        broadcaster.sendBroadcast(intent);
    }

    public void showToast(String text) {
        if (m_currentToast == null) {
            m_currentToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        }
        m_currentToast.setText(text);
        m_currentToast.setDuration(Toast.LENGTH_LONG);
        m_currentToast.show();
    }
}