package com.myatminsoe.mkeyboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class FragmentSettings extends Fragment {

    View root;
    LinearLayout testLayout, llchoosetheme, llchooselanguage, llkeyheight;
    RelativeLayout keySound, keyVibrate, keyPopUp, keyConvert;
    CheckBox sound, vibrate, popup;
    SeekBar sk;
    Vibrator vi;
    SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.frag_settings, null);
        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        keySound = (RelativeLayout) root.findViewById(R.id.keySound);
        keyVibrate = (RelativeLayout) root.findViewById(R.id.keyVibrate);
        keyPopUp = (RelativeLayout) root.findViewById(R.id.keypopup);
        llchoosetheme = (LinearLayout) root.findViewById(R.id.llchoosetheme);
        llchooselanguage = (LinearLayout) root.findViewById(R.id.llchooselanguage);
        keyConvert = (RelativeLayout) root.findViewById(R.id.rlConverter);
        llkeyheight = (LinearLayout) root.findViewById(R.id.llkeyheight);
        popup = (CheckBox) root.findViewById(R.id.cbPopup);
        testLayout = (LinearLayout) root.findViewById(R.id.settings7);

        sk = (SeekBar) root.findViewById(R.id.seekbar);
        vi = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        sk.setEnabled(settings.getBoolean("vibrate", true));
        sk.setMax(200);
        sk.setProgress(settings.getInt("vibDuration", 5));
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int p = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                p = (Math.round(progress / 10)) * 10;
                if (p == 0) {
                    p = 5;
                }
                seekBar.setProgress(p);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                vi.vibrate(p);
                settings.edit().putInt("vibDuration", p).apply();
            }
        });

        sound = (CheckBox) root.findViewById(R.id.cbSound);
        vibrate = (CheckBox) root.findViewById(R.id.cbVibrate);

        sound.setChecked(settings.getBoolean("sound", true));
        vibrate.setChecked(settings.getBoolean("vibrate", true));
        popup.setChecked(settings.getBoolean("popup", true));

        keySound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sound.setChecked(!sound.isChecked());
            }
        });

        keyVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate.setChecked(!vibrate.isChecked());
            }
        });

        llchoosetheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTheme();
            }
        });

        llkeyheight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyHeight();
            }
        });

        keyPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.setChecked(!popup.isChecked());
            }
        });


        testLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });

        sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                settings.edit().putBoolean("sound", isChecked).apply();
            }
        });

        vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sk.setEnabled(isChecked);
                settings.edit().putBoolean("vibrate", isChecked).apply();
            }
        });

        popup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.edit().putBoolean("popup", isChecked).apply();
            }
        });

        llchooselanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseLanguage();
            }
        });

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            keyConvert.setVisibility(View.GONE);
            root.findViewById(R.id.tohide).setVisibility(View.GONE);
        } else {
            final CheckBox cbConvert = (CheckBox) root.findViewById(R.id.cbConvert);
            keyConvert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cbConvert.setChecked(!cbConvert.isChecked());
                }
            });
            cbConvert.setChecked(settings.getBoolean("copycon", false));
            cbConvert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    settings.edit().putBoolean("copycon", isChecked).apply();
                    if(isChecked) {
                        getActivity().startService(new Intent(getActivity().getBaseContext(), Background.class));
                    } else {
                        getActivity().stopService(new Intent(getActivity().getBaseContext(), Background.class));
                    }
                }
            });
        }
        return root;
    }

    private void chooseLanguage() {
        View v = View.inflate(getActivity(), R.layout.language, null);
        final CheckBox cbEng = (CheckBox) v.findViewById(R.id.cbEng);
        final CheckBox cbZg = (CheckBox) v.findViewById(R.id.cbZg);
        final CheckBox cbUni = (CheckBox) v.findViewById(R.id.cbUni);

        cbEng.setChecked(settings.getBoolean("eng", true));
        cbZg.setChecked(settings.getBoolean("zawgyi", true));
        cbUni.setChecked(settings.getBoolean("unicode", true));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Languages");
        builder.setView(v).setCancelable(true)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        settings.edit().putBoolean("eng", cbEng.isChecked()).apply();
                        settings.edit().putBoolean("zawgyi", cbZg.isChecked()).apply();
                        settings.edit().putBoolean("unicode", cbUni.isChecked()).apply();
                        dialog.cancel();
                    }
                }).show();
    }

    private void keyHeight() {
        View v = View.inflate(getActivity(), R.layout.height, null);

        final RadioButton phone = (RadioButton) v
                .findViewById(R.id.rbPhone);
        final RadioButton tablet = (RadioButton) v
                .findViewById(R.id.rbTablet);

        phone.setChecked(!settings.getBoolean("height", false));
        tablet.setChecked(settings.getBoolean("height", false));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set Height");
        builder.setView(v).setCancelable(true)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        settings.edit().putBoolean("height", tablet.isChecked()).apply();
                        dialog.cancel();
                    }
                }).show();
    }

    private void chooseTheme() {

        View radioView = View.inflate(getActivity(), R.layout.theme, null);

        final RadioButton fbtn1 = (RadioButton) radioView.findViewById(R.id.aLight);
        final RadioButton fbtn2 = (RadioButton) radioView.findViewById(R.id.aDark);
        final RadioButton fbtn3 = (RadioButton) radioView.findViewById(R.id.iLight);
        final RadioButton fbtn4 = (RadioButton) radioView.findViewById(R.id.iDark);
        final int before = settings.getInt("theme", 0);

        switch (before) {
            case 0:
                fbtn1.setChecked(true);
                break;
            case 1:
                fbtn2.setChecked(true);
                break;
            case 2:
                fbtn3.setChecked(true);
                break;
            case 3:
                fbtn4.setChecked(true);
            default:
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Theme").setView(radioView).setCancelable(true).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                SharedPreferences.Editor editor = settings.edit();
                if (fbtn1.isChecked()) {
                    editor.putInt("theme", 0);
                } else if (fbtn2.isChecked()) {
                    editor.putInt("theme", 1);
                } else if (fbtn3.isChecked()) {
                    editor.putInt("theme", 2);
                } else if (fbtn4.isChecked()) {
                    editor.putInt("theme", 3);
                }
                editor.apply();
            }
        }).show();
    }

    private void test() {
        View v = View.inflate(getActivity(), R.layout.test, null);
        final EditText et = (EditText) v.findViewById(R.id.edit);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setView(v);
        final AlertDialog al = dialog.show();
        al.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    al.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
    }
}