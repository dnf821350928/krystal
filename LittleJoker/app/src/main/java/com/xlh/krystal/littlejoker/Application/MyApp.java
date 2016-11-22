package com.xlh.krystal.littlejoker.Application;

import android.app.Application;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by krystal on 2016/11/11.
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ShareSDK.initSDK(getApplicationContext(),"18ef50854b590");
    }
}
