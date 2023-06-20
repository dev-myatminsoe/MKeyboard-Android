package com.myatminsoe.mkeyboard;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by myatminsoe on 8/18/15.
 */
public class Hahaha extends Application {

    @Override
    public void onCreate() {
        Parse.initialize(this, "cMm2mJvkRlIButQ0wM4lfj5veFxrQYUKw8P4mdM4", "bOplHPHvWb9IRzv2EbQnylWFgyveTsDYEspV2qs0");
    }
}
