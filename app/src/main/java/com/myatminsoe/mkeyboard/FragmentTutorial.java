package com.myatminsoe.mkeyboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class FragmentTutorial extends Fragment {

    View root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.frag_tutorial, null);
        WebView wv = (WebView) root.findViewById(R.id.wv);
        wv.loadUrl("file:///android_asset/tutorial.html");
        return root;
    }
}