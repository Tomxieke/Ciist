package com.ciist.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xieke on 2016/3/1.
 */
public class SharedPreferenceHelper {
    private static final String FILE_NAME = "data.sharedprefrence";  //数据保存的文件名一般用包名加后缀来命名
    private SharedPreferences mSharedPreferences;

    private static SharedPreferenceHelper sHelper;  // 私有 static
    public static SharedPreferenceHelper getInstance(Context context){     // 私有 static
        if(sHelper == null){
            sHelper = new SharedPreferenceHelper(context);
        }
        return sHelper;
    }

    private SharedPreferenceHelper(Context context){
        mSharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }


    /**
     * 保存用户的passport
     * @param key value  保存的key value
     */
    public void addPassport(String key,String value){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }


    /**
     * 返回用户passport
     * @return key
     */
    public String getPassport(String key){
        String passportStr = mSharedPreferences.getString(key,"");
        return passportStr;
    }
}
