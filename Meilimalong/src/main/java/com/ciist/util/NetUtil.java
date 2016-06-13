package com.ciist.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ciist.app.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 判断网络相关
 * Created by xieke on 2016/1/25.
 */
public class NetUtil {

    public static String MAIN_FACE_CATCH_FILE_NAME = "maincatchfile";  //主界面头条资讯缓存文件名
    /**
     * 网络没连接时弹出提示对话框
     * @param context
     */
    public static void hasNetWork(Context context){
        if (!(isNetworkAvailable(context))){
            NotificationDialog dialog = new NotificationDialog(context, R.style.add_dialog);
            dialog.setTitle("提示");
            dialog.setContent("请连接网络");
            dialog.setIcon(true,R.mipmap.ciist_icon_prompt_xinhao); //设置图标
            dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
            dialog.setBgColor(R.drawable.bg_shape);  //设置颜色
            dialog.show();
        }
    }


    /**
     * 检查当前网络是否可用Tom
     *
     * @param context
     * @return has or not has internet
     */
    public static boolean isNetworkAvailable(Context context) {
        // Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * cut string method  切割String 返回传入字符的前toNum个字符
     * @param str  srcString  源字符
     * @param toNum return string Num start frist
     * @return  返回传入字符的前toNum个字符
     */
    public static String IsubString(String str,int toNum){
        if (str.length() < toNum){
            return str;
        }else {
            str = str.substring(0,toNum);
            return str;
        }
    }


    /**
     * 将json文件写入本地储存
     * @param context
     * @param string
     * @param file  文件名
     * @return
     */
    public static boolean saveObject(Context context,String string, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(file, context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(string);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }


    /**
     * 从本地文件读取json
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static String readObject(Context context,String file) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (String) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 缓存文件超过CACHE_TIME毫秒才去访问网络取数据
     * @param context
     * @param cachefile
     * @return
     */
    private static int CACHE_TIME = 600000;  //缓存文件超过10分钟才访问网络取数据，10分钟以内直接读取缓存文件中的数据
    public static boolean isCacheDataFailure(Context context,String cachefile) {
        boolean failure = false;
        File data = context.getFileStreamPath(cachefile);
        if (data.exists()
                && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
            failure = true;
        else if (!data.exists())
            failure = true;
        return failure;
    }
}
