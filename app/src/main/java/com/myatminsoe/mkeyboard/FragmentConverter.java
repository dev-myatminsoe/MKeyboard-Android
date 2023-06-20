package com.myatminsoe.mkeyboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.myatminsoe.smartzawgyi.SmartZawgyi;

public class FragmentConverter extends Fragment implements View.OnClickListener {

    View root;
    Button btntype, btncopy, btnpaste, btnclear;
    EditText et1, et2;
    SharedPreferences settings;
    int type;
    Typeface uni, zg;
    int sdk = android.os.Build.VERSION.SDK_INT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.frag_converter, null);

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        init();
        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (type == 0) {
                    et2.setText(SmartZawgyi.google(s + ""));
                } else {
                    et2.setText(SmartZawgyi.samsung(s + ""));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard1 = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            et1.setText(clipboard1.getText());
        } else {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            String pasteData = "";
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            pasteData = item.getText() + "";
            et1.setText(pasteData);
        }
        return root;
    }

    private void init() {
        btntype = (Button) root.findViewById(R.id.btnType);
        btnpaste = (Button) root.findViewById(R.id.btnPaste);
        btncopy = (Button) root.findViewById(R.id.btnCopy);
        btnclear = (Button) root.findViewById(R.id.btnClear);
        et1 = (EditText) root.findViewById(R.id.et1);
        et2 = (EditText) root.findViewById(R.id.et2);
        btntype.setOnClickListener(this);
        btnpaste.setOnClickListener(this);
        btncopy.setOnClickListener(this);
        btnclear.setOnClickListener(this);

        uni = Typeface.createFromAsset(getActivity().getAssets(), "fonts/mon3" + ".ttf");
        zg = Typeface.createFromAsset(getActivity().getAssets(), "fonts/zawgyi.ttf");
        type = settings.getInt("converter", 0);
        if (type == 1) {
            btntype.setText("Unicode To Zawgyi");
            et1.setTypeface(uni);
            et2.setTypeface(zg);
        } else {
            btntype.setText("Zawgyi To Unicode");
            et2.setTypeface(uni);
            et1.setTypeface(zg);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnType:
                if (type == 0) {
                    btntype.setText("Unicode To Zawgyi");
                    type = 1;
                    settings.edit().putInt("converter", 1).apply();
                    et1.setText(et1.getText() + "");
                    et1.setTypeface(uni);
                    et2.setTypeface(zg);
                } else {
                    btntype.setText("Zawgyi To Unicode");
                    type = 0;
                    settings.edit().putInt("converter", 0).apply();
                    et1.setText(et1.getText() + "");
                    et2.setTypeface(uni);
                    et1.setTypeface(zg);
                }
                break;
            case R.id.btnPaste:
                if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard1 = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    et1.setText(clipboard1.getText());
                } else {
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    String pasteData = "";
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    pasteData = item.getText() + "";
                    et1.setText(pasteData);
                }

                break;
            case R.id.btnClear:
                et1.setText("");
                break;
            case R.id.btnCopy:
                getActivity().stopService(new Intent(getActivity().getBaseContext(), Background.class));
                if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard1 = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard1.setText(et2.getText());
                } else {
                    android.content.ClipboardManager clipboard2 = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("copied from converter", et2.getText());
                    clipboard2.setPrimaryClip(clip);
                }
                Toast.makeText(getActivity(),
                        "Text copied to clipboard", Toast.LENGTH_SHORT)
                        .show();
                if(settings.getBoolean("copycon", false)) {
                    getActivity().startService(new Intent(getActivity().getBaseContext(), Background.class));
                } else {
                    getActivity().stopService(new Intent(getActivity().getBaseContext(), Background.class));
                }
                break;
            default:
                break;
        }
    }
}