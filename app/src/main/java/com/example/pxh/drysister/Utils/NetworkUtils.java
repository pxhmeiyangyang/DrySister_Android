package com.example.pxh.drysister.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
    /**
     * 获取网络信息
     * @param context 获取网络信息的主题
     * @return 返回网路状态
     */
    private static NetworkInfo getActiveNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
        //获取网络状态需要在Manifest中添加获取网络状态的权限
    }

    /**
     * 判断网络是否可用
     * @param context
     * @return 网络可用状态
     */
    public static boolean isAvailable(Context context){
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isAvailable();
    }

}
