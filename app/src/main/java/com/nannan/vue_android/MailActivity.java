package com.nannan.vue_android;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;

public class MailActivity extends AppCompatActivity {
    private X5WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);
        mWebView = (X5WebView)findViewById(R.id.webview);
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.i("consoleMessage", consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
        });
        mWebView.getSettings();
        // mWebView.loadUrl("file:///android_asset/vue/mail/html_1.html");
         mWebView.loadUrl("file:///android_asset/vue/mail/index.html");
        //mWebView.loadUrl("file:///android_asset/vue/index.html");
        mWebView.addJavascriptInterface(new JSInterface(),"latte");

    }

    /**
     * https://blog.csdn.net/wuqingsen1/article/details/80485093
     * 返回键 返回上一个界面...
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean
    onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();//返回上个页面
            return true;
        }
        //退出H5界面
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
        mWebView.getSettings().setLightTouchEnabled(false);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null){
            mWebView.destroy();
        }
        super.onDestroy();
    }

    /**
     * 设置处理 -- 方法处理
     *
     * JS调用原生...
     */
    private final class JSInterface{
        @SuppressLint("JavascriptInterface")
        @JavascriptInterface
        public void event(final String info){
            Toast.makeText(MailActivity.this, info, Toast.LENGTH_LONG).show();
            // 获得传递的值
        }
    }
}
