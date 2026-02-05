package com.studioapk.apkbackup;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by jfp on 10/06/2016.
 */
public class FontOverride extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("irsans.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }
}