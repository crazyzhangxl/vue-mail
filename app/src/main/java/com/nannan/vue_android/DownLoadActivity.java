package com.nannan.vue_android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.nannan.vue_android.store.Login;
import com.nannan.vue_android.store.SharePreferenceUtils;
import com.nannan.vue_android.utils.BuglyHelper;
import com.nannan.vue_android.utils.LoadingProgressDialog;

import java.io.File;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class DownLoadActivity extends AppCompatActivity {
    private Context context;
    private MaterialDialog mWaitMaterialDialog;
    private MaterialDialog mPSMaterialDialog;

    private android.webkit.WebView mWebView;
    private File file;
    private String savePath;
    private long exitTime;
    private Dialog mNiceDialog;
    private  boolean isFirstLoading = true;
    private boolean isSuccess = false;
    private boolean isError = false;
    private RelativeLayout mRlError;
    private TextView mTvErrorText;

    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load);
        context = this;
        initWebView();
//        new android.webkit.WebViewClient(){
//            @Override
//            public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                showNiceDialog(context);
//            }
//
//            @Override
//            public void onPageFinished(android.webkit.WebView view, String url) {
//                super.onPageFinished(view, url);
//                view.getSettings().setBlockNetworkImage(false);
//                cancelNiceDialog();
//                if (isFirstLoading) {
//                    BuglyHelper.getInstance().doUpdateNow(context);
//                    isFirstLoading = false;
//                }
//            }
//        }

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                isError = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view,url);
                cancelNiceDialog();
                if (!isError) {
                    isSuccess = true;
                    if (mWebView.getVisibility() == View.INVISIBLE){
                        mWebView.setVisibility(View.VISIBLE);
                    }
                    isError = false;
                    return;
                }
                // 失败的处理
                mWebView.setVisibility(View.INVISIBLE);
                mRlError.setVisibility(View.VISIBLE);
                isError = false;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                isError = true;
                isSuccess = false;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100){
                    cancelNiceDialog();
                    view.getSettings().setBlockNetworkImage(false);
                    if (isFirstLoading) {
                        BuglyHelper.getInstance().doUpdateNow(context);
                        isFirstLoading = false;
                    }
                }else {
                    showNiceDialog(context);
                }
            }
        });
        mWebView.addJavascriptInterface(new JSInterface(),"latte");
        // 进行网络权限的请求
        DownLoadActivityPermissionsDispatcher.needPermissionWithCheck(DownLoadActivity.this);
    }

    private void initWebView() {
        mWebView = findViewById(R.id.webview);
        mTvErrorText = findViewById(R.id.tvText);
        mRlError = findViewById(R.id.loadError);
        mTvErrorText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlError.setVisibility(View.GONE);
                loadWebView();
            }
        });

        WebSettings setting = mWebView.getSettings();
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
        /*关闭webview中缓存*/
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        /*设置可以访问文件*/
        setting.setAllowFileAccess(true);
        setting.setAllowFileAccessFromFileURLs(true);
        setting.setAllowUniversalAccessFromFileURLs(true);
        setting.setAllowContentAccess(true);
        setting.setBlockNetworkImage(true);
    }

    private void loadWebView(){
        //mWebView.loadUrl("file:///android_asset/vue/mail/index.html");
        // 测试环境包......
        // mWebView.loadUrl("file:///android_asset/vue/test/index.html");
        //mWebView.loadUrl("http://172.22.31.1:8012/#/newLogin");
        //mWebView.loadUrl("http://192.168.1.108:8012/#/newLogin");
        SharePreferenceUtils instance = SharePreferenceUtils.getInstance(context);
        String info="";
        if (instance.getString(Login.id,null) != null){
            info = "?id="+instance.getString(Login.id,null)+
                    "&token="+instance.getString(Login.token,null)+
                    "&username="+instance.getString(Login.username,null)+
                    "&name="+instance.getString(Login.name,null)+
                    "&mobile="+instance.getString(Login.mobile,null)+
                    "&emailDomain="+instance.getString(Login.emailDomain,null)+
                    "&departmentName="+instance.getString(Login.departmentName,null)+
                    "&companyId="+instance.getString(Login.companyId,null)+
                    "&companyName="+instance.getString(Login.companyName,null)+
                    "&account="+instance.getString(Login.account,null)+
                    "&password="+instance.getString(Login.password,null)+
                    "&employeeId="+instance.getString(Login.employeeId,null)+
                    "&positionId="+instance.getString(Login.positionId,null)+
                    "&departmentId="+instance.getString(Login.departmentId,null) +
                    "&identity="+instance.getString(Login.identity,null)+
                    "&positionName="+instance.getString(Login.positionName,null)+
                    "&companyType="+instance.getString(Login.companyType,null);
        }
        Log.e("数据", "http://172.22.31.1:8012/#/newLogin"+info);
        // 线上 == 应用市场
        // mWebView.loadUrl("http://app.extsci.com/#/newLogin"+info);
        mWebView.loadUrl("https://app.wjsci.com/#/newLogin"+info);
        //mWebView.loadUrl("http://192.168.1.123:8080/#/newLogin"+info);
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
            Log.e("地址", mWebView.getUrl());
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_BACK && !mWebView.canGoBack()){
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
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


    public void showNiceDialog(Context context){
        try {
            if (mNiceDialog != null && mNiceDialog.isShowing()){
                return;
            }
            mNiceDialog = LoadingProgressDialog.createLoadingDialog(context);
            mNiceDialog.setCanceledOnTouchOutside(false);
            mNiceDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelNiceDialog() {
        try {
            if (mNiceDialog != null && mNiceDialog.isShowing()) {
                mNiceDialog.dismiss();
            }
        } catch (Exception e ) {
            e.printStackTrace();
        }
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
            // 获得传递的值
            Log.e("数据", info);
            Gson gson = new Gson();
            NotifyBean notifyBean = gson.fromJson(info, NotifyBean.class);
            String command =  notifyBean.command;
            if ("download".equals(command)){
                doDownLoad(notifyBean.msg);
            }else if ("look".equals(command)){
                // 进行页面条状
                Intent intent = new Intent(DownLoadActivity.this,PdfShowActivity.class);
                intent.putExtra("path",notifyBean.msg);
                startActivity(intent);
            }else if ("save_login".equals(command)){
                Log.e("数据", "event: 进来了");
                SharePreferenceUtils instance = SharePreferenceUtils.getInstance(context);
                instance.putString(Login.id,notifyBean.id);
                instance.putString(Login.token,notifyBean.token);
                instance.putString(Login.username,notifyBean.username);
                instance.putString(Login.name,notifyBean.name);
                instance.putString(Login.mobile,notifyBean.mobile);
                instance.putString(Login.emailDomain,notifyBean.emailDomain);
                instance.putString(Login.departmentName,notifyBean.departmentName);
                instance.putString(Login.departmentId,notifyBean.departmentId);
                instance.putString(Login.companyId,notifyBean.companyId);
                instance.putString(Login.companyName,notifyBean.companyName);
                instance.putString(Login.account,notifyBean.account);
                instance.putString(Login.password,notifyBean.password);
                instance.putString(Login.employeeId,notifyBean.employeeId);
                instance.putString(Login.positionId,notifyBean.positionId);
                instance.putString(Login.identity,notifyBean.identity);
                instance.putString(Login.positionName,notifyBean.positionName);
                instance.putString(Login.companyType,notifyBean.companyType);
            }else if ("login_in".equals(command)){
                if (mWebView != null){
                    mWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            notifyLoginDara();
                        }
                    });
                }
            }else if ("login_clean".equals(command)){
                SharePreferenceUtils instance = SharePreferenceUtils.getInstance(context);
                instance.remove(Login.id);
                instance.remove(Login.token);
                instance.remove(Login.username);
                instance.remove(Login.name);
                instance.remove(Login.mobile);
                instance.remove(Login.emailDomain);
                instance.remove(Login.departmentId);
                instance.remove(Login.departmentName);
                instance.remove(Login.companyId);
                instance.remove(Login.companyName);
                instance.remove(Login.positionId);
                instance.remove(Login.employeeId);
                // 账号是否需要清除呢
                instance.remove(Login.account);
                instance.remove(Login.password);
                instance.remove(Login.positionName);
                instance.remove(Login.identity);
                instance.remove(Login.companyType);
            }
        }
    }


    public void notifyLoginDara(){
        //todo json传值
//        SharePreferenceUtils instance = SharePreferenceUtils.getInstance(context);
//        String info = new Gson().toJson(notifyBean);
//        Log.e("检查数据", info );
//        //"javascript:nativeCall(\'"+imagePath+"\');"
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            mWebView.evaluateJavascript("javascript:loginAndroid("+info+");",
//                    new ValueCallback<String>() {
//                        @Override
//                        public void onReceiveValue(String s) {
//
//                        }
//                    });
//        }
    }


    private void doDownLoad(String url) {
        Log.e("路径", url);
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (!sdCardExist) {
            Toast.makeText(context, "下载,不存在SD卡!", Toast.LENGTH_SHORT).show();
            return;
        }

        sdDir = Environment.getExternalStorageDirectory();
        file = new File(sdDir.toString()
                + File.separator + "yc_mail" + File.separator + "download");
        makeDir(file);
        String[] split = url.split("/");
        // 文件名
        String fileName = split[split.length-1];
        savePath = file.toString() + File.separator + fileName;
        if (hasThisFile(fileName)){
            Toast.makeText(context, "文件已下载", Toast.LENGTH_SHORT).show();
            openFileByPath(context,savePath);
            // 进行查看吧
            return;
        }
        Log.e("路径", savePath );
        // /storage/emulated/0/yc_mail/download/测试下载的文件.pdf
        FileDownloader.setup(context);
        FileDownloader.getImpl().create(url)
                .setPath(savePath)
                .setForceReDownload(true)
                .setListener(new FileDownloadLargeFileListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                        showWaitingDialog("下载中请稍等");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        hideWaitingDialog();
                        Toast.makeText(DownLoadActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                        openFileByPath(context,savePath);

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        hideWaitingDialog();
                        Toast.makeText(DownLoadActivity.this, "下载出错", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        hideWaitingDialog();
                    }
                }).start();
    }

    public void openFileByPath(Context context, String path) {
        if(context==null||path==null) {
            return;
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        Log.e("路径", path+"");
        try {
            //设置intent的data和Type属性
            // "text/html|image/*|text/plain|image/jpeg|application/pdf|application/msword"
            intent.setDataAndType(Uri.fromFile(new File(path)), "image/*");
            //跳转
            context.startActivity(intent);
        } catch (Exception e) { //当系统没有携带文件打开软件，提示
            Toast.makeText(context, "无法打开该格式文件!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // 等待的dialog
    public void showWaitingDialog(String tip) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .content(tip)
                .progress(true, 0)
                .progressIndeterminateStyle(false);
        mWaitMaterialDialog = builder.build();
        mWaitMaterialDialog.setCancelable(false);
        mWaitMaterialDialog.show();
    }

    // 隐藏dislog
    public void hideWaitingDialog() {
        if (mWaitMaterialDialog != null) {
            mWaitMaterialDialog.dismiss();
            mWaitMaterialDialog = null;
        }
    }

    public  void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }

    /**
     *  判断手机SD卡是否存在该文件
     * */
    private boolean hasThisFile(String fileName) {
        boolean has = false;
        File[] listFiles = file.listFiles();
        for (int i=0;i<listFiles.length;i++){
            File file =  listFiles[i];
            if (fileName.equals(file.getName())){
                has = true;
                break;
            }
        }
        return has;
    }


    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void needPermission() {
        // 执行任务.........
        //initWebView();
        loadWebView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DownLoadActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onShow(final PermissionRequest request) {
        showPSMaterialDialog(null, getPermissionTips() + "权限需要您的授权", "确定", "取消",
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        request.proceed();
                    }
                }, new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        request.cancel();
                    }
                });
    }

    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onPermission() {
        showPSMaterialDialog("权限说明", "未取得"+getPermissionTips()+"的使用权限，邮箱无法开启。", "赋予权限", "", new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
                DownLoadActivityPermissionsDispatcher.needPermissionWithCheck(DownLoadActivity.this);
            }
        },null);
    }

    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onNerverAsk() {
        showPSMaterialDialog(null, "未取得"+getPermissionTips()+"的使用权限，邮箱无法开启。请前往应用权限设置打开权限", "去打开", "", new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
                DownLoadActivity.this.getAppDetailSettingIntent();
            }
        },null);
    }


    private String getPermissionTips(){
        StringBuilder mStringBuilder = new StringBuilder();
//        if (lackPermission(Manifest.permission.READ_PHONE_STATE)){
//            mStringBuilder.append("手机信息");
//        }
        if (lackPermission(Manifest.permission.READ_EXTERNAL_STORAGE) || lackPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            if (TextUtils.isEmpty(mStringBuilder.toString())) {
                mStringBuilder.append("读写存储");
            }else {
                mStringBuilder.append(" 读写存储");
            }
        }
        return mStringBuilder.toString();
    }

    public boolean lackPermission(String permission){
        return ContextCompat.checkSelfPermission(context,permission) == PackageManager.PERMISSION_DENIED;
    }

    /**
     * 显示权限MaterialDialog
     */
    public MaterialDialog showPSMaterialDialog(String title, String message, String positiveText, String negativeText, MaterialDialog.SingleButtonCallback positiveCallBack,MaterialDialog.SingleButtonCallback negativeCallBack) {
        hidePSMaterialDialog();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
        }
        if (!TextUtils.isEmpty(message)) {
            builder.content(message);
        }
        if (!TextUtils.isEmpty(positiveText)) {
            builder.positiveText(positiveText).onPositive(positiveCallBack);
        }
        if (!TextUtils.isEmpty(negativeText)) {
            builder.negativeText(negativeText).onNegative(negativeCallBack);
        }
        mPSMaterialDialog = builder.build();
        mPSMaterialDialog.setCancelable(false);
        mPSMaterialDialog.show();
        return mPSMaterialDialog;
    }

    /**
     * 隐藏权限MaterialDialog
     */
    public void hidePSMaterialDialog() {
        if (mPSMaterialDialog != null) {
            mPSMaterialDialog.dismiss();
            mPSMaterialDialog = null;
        }
    }

    /**
     * 跳转到设置权限的界面
     * */
    public void getAppDetailSettingIntent() {
        // 可以看下这篇文章
        // https://blog.csdn.net/cbbbc/article/details/60148864
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(intent);
    }
}
