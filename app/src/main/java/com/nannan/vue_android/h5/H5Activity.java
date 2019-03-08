package com.nannan.vue_android.h5;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.nannan.vue_android.MainActivity;
import com.nannan.vue_android.R;
import com.nannan.vue_android.X5WebView;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;

import java.io.File;

/**
 * @author crazyZhangxl on 2019-3-6 13:41:38.
 * Describe:
 */

public class H5Activity extends AppCompatActivity {
    private X5WebView mX5WebView;

    private static final int CHOOSE_PGOTO = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h5);

        mX5WebView = findViewById(R.id.webviewH5);
        mX5WebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.i("consoleMessage", consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
        });
        mX5WebView.getSettings();
        // 交互
        mX5WebView.loadUrl("file:///android_asset/vue/h5/html_1.html");
        // 设置方法拦截... (参数1 不清楚, 参数2 交互时相互定义的名字)
        mX5WebView.addJavascriptInterface(new JSInterface(),"latte");

       // mX5WebView.loadUrl("file:///android_asset/vue/h5/camera.html");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mX5WebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mX5WebView.onPause();
        mX5WebView.getSettings().setLightTouchEnabled(false);
    }

    @Override
    protected void onDestroy() {
        if (mX5WebView != null){
            mX5WebView.destroy();
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
        public void event(String info){
            // 弹出信息...
            Toast.makeText(H5Activity.this, info, Toast.LENGTH_LONG).show();
            if (mX5WebView != null){
                mX5WebView.post(new Runnable() {
                    @Override
                    public void run() {
                        // 传递参数... 4.4以下
                        // mX5WebView.loadUrl("javascript:nativeCall();");
                        //  4.4    11111_1
                        final String filePath = Environment.getExternalStorageDirectory()+ File.separator+"DCIM"+File.separator+"Camera"+
                                File.separator+"IMG_20190228_091600.jpg";
                        Log.e("222", "run: "+filePath );
                        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
                        // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
                        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpeg");
                        startActivityForResult(intentToPickPic,CHOOSE_PGOTO);
                        /**
                         * 后面考虑以json格式进行转换传递
                         */
                    }
                });
            }
        }



        @JavascriptInterface
        public void nativeCall2(String result) {
            /**
             * 这里进行判断也行
             */
            Toast.makeText(H5Activity.this, result, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == CHOOSE_PGOTO){
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null,null);
                if (cursor != null && cursor.moveToFirst()) {
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    showPic(path);
                }
            }

        }
    }

    public void showPic(String imagePath){
        mX5WebView.evaluateJavascript("javascript:nativeCall(\'"+imagePath+"\');",
                new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
               //todo
               // 接收回传值...
            }
        });
    }
}
