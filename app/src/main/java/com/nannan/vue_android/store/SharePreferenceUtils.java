package com.nannan.vue_android.store;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zxl on 2017/8/26.
 * sharePreference 使用键值对来进行存储的
 * 位于 /data/data/应用包名/shared_prefs/*.xml  这里面就是<map></map> 键值对了
 *
 * /data/data/ 存放当前应用的私有信息  应用卸载也就删除了
 */

public class SharePreferenceUtils {
    // 私有的 外部当然没法访问咯
    private Context mContext;
    private static SharePreferenceUtils mSharedPreferencesUtils;
    private SharedPreferences mSp;
    private SharedPreferences.Editor mEditor;
    @SuppressLint("CommitPrefEdits")
    private SharePreferenceUtils(Context context){
        mContext = context;
        mSp = mContext.getSharedPreferences("wj_mail",Context.MODE_PRIVATE);
        mEditor=mSp.edit();
    }
    
    public static SharePreferenceUtils getInstance(Context context){
        if (mSharedPreferencesUtils == null){
            synchronized (SharePreferenceUtils.class){
                if (mSharedPreferencesUtils == null){
                    mSharedPreferencesUtils = new SharePreferenceUtils(context);
                }
            }
        }
        return mSharedPreferencesUtils;
    }
    

    public  void putBoolean(String key, boolean value){
        mEditor.putBoolean(key,value).apply();
    }

    public  boolean getBoolean( String key, boolean defValue){
        return mSp.getBoolean(key,defValue);
    }

    public  void putString( String key, String value){
        if (value == null) {
            return;
        }
        mEditor.putString(key,value).apply();
    }

    public  String getString( String key, String defValue){
        return mSp.getString(key,defValue);
    }


    public  void putInt( String key , int value){
        mEditor.putInt(key,value).apply();
    }

    public int getInt( String key, int defValue){
        return mSp.getInt(key,defValue);
    }

    public void remove(String key){
        mEditor.remove(key).apply();
    }

    public  void clear(){
        mEditor.clear().apply();
    }
}
