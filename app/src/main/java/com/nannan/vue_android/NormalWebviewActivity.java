package com.nannan.vue_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.nannan.vue_android.R;


public class NormalWebviewActivity extends AppCompatActivity {
    private WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_webview);
        initWebView();
        webview.loadUrl("file:///android_asset/vue/mail/index.html");
    }

    private void initWebView() {
        webview = findViewById(R.id.webview);
        WebSettings setting = webview.getSettings();
         /**支持Js**/
        setting.setJavaScriptEnabled(true);

        /**设置自适应屏幕，两者合用**/
        //将图片调整到适合webview的大小
        setting.setUseWideViewPort(true);
     // 缩放至屏幕的大小
        setting.setLoadWithOverviewMode(true);

        /**缩放操作**/
        // 是否支持画面缩放，默认不支持
        setting.setBuiltInZoomControls(true);
        setting.setSupportZoom(true);
        // 是否显示缩放图标，默认显示
        setting.setDisplayZoomControls(false);
        // 设置网页内容自适应屏幕大小
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        /**设置允许JS弹窗**/
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setAppCacheEnabled(true);
        setting.setDomStorageEnabled(true);
        setting.setGeolocationEnabled(true);
        setting.setAppCacheMaxSize(Long.MAX_VALUE);
        setting.setPluginState(WebSettings.PluginState.ON_DEMAND);

        /**关闭webview中缓存**/
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        /**设置可以访问文件 **/
        setting.setAllowFileAccess(true);
        setting.setAllowFileAccessFromFileURLs(true);
        setting.setAllowUniversalAccessFromFileURLs(true);
        setting.setAllowContentAccess(true);

    }
}
