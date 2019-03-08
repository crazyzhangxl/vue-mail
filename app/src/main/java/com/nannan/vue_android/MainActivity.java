package com.nannan.vue_android;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.nannan.vue_android.h5.H5Activity;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;


/**
 * 参考文档:
 * https://blog.csdn.net/solocoder/article/details/81948286
 */
public class MainActivity extends AppCompatActivity {

    private X5WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = (X5WebView)findViewById(R.id.webview);
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.i("consoleMessage", consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
        });
        mWebView.getSettings();
        mWebView.loadUrl("file:///android_asset/vue/index.html");
        //mWebView.loadUrl("http://www.baidu.com");

        // 设置方法拦截... (参数1 不清楚, 参数2 交互时相互定义的名字)


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
        public void event(final String userInfo){
            Toast.makeText(MainActivity.this, userInfo, Toast.LENGTH_LONG).show();
            if (mWebView != null){
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.evaluateJavascript("javascript:share("+userInfo+");", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }
}
