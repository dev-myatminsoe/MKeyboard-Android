package com.myatminsoe.mkeyboard;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;

public class FragmentSetup extends Fragment {

    View root;
    TextView tv;
    BroadcastReceiver receiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.frag_setup, null);
        tv = (TextView) root.findViewById(R.id.tv);

        receiver = new BroadcastReceiver() {

            public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent) {
                boolean bool = paramAnonymousIntent.getBooleanExtra("running", false);
                if (bool) {
                    tv.setVisibility(View.VISIBLE);
                    MainActivity.openDrawer();
                    return;
                } else {
                    tv.setVisibility(View.INVISIBLE);
                    return;
                }
            }
        };
        return root;
    }

    private boolean isTurnedOn() {
        Iterator iterator = ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).getEnabledInputMethodList().iterator();
        while (iterator.hasNext())
            if ("M Keyboard".equals(((InputMethodInfo) iterator.next()).loadLabel(getActivity().getPackageManager()).toString()))
                return true;
        return false;
    }

    public static  boolean isChosen(Context c) {
        Iterator iterator = ((ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(200).iterator();
        while (iterator.hasNext())
            if (((ActivityManager.RunningServiceInfo) iterator.next()).service.getClassName().equals(MyatMinIME.class.getName()))
                return true;
        return false;
    }

    @Override
    public void onResume() {
        if (!isTurnedOn()) {
            tv.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "Please turn on M Keyboard", Toast.LENGTH_SHORT).show();
            startActivity(new Intent("android.settings.INPUT_METHOD_SETTINGS"));
        }

        if (isTurnedOn() && !isChosen(getActivity())) {
            Toast.makeText(getActivity(), "Please choose M Keyboard", Toast.LENGTH_SHORT).show();
            tv.setVisibility(View.INVISIBLE);
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();
        }

        if (isTurnedOn() && isChosen(getActivity())){
            tv.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(this.receiver, new IntentFilter("keyboardRunning"));
    }

    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this.receiver);
        super.onStop();
    }
}