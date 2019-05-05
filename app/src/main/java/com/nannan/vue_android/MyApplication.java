package com.nannan.vue_android;

import android.app.Application;
import android.util.Log;

import com.nannan.vue_android.utils.BuglyHelper;
import com.tencent.smtt.sdk.QbSdk;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化bugly
        BuglyHelper.getInstance().initBuglyApp(this);
        // 初始化webview
//        QbSdk.setDownloadWithoutWifi(true);
//        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
//
//            @Override
//            public void onViewInitFinished(boolean arg0) {
//                Log.d("app", " onViewInitFinished is " + arg0);
//            }
//
//            @Override
//            public void onCoreInitFinished() {
//            }
//        };
//        //x5内核初始化接口
//        QbSdk.initX5Environment(getApplicationContext(), cb);
    }
}
