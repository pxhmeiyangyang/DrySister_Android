package com.example.pxh.drysister.Network;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.pxh.drysister.bean.entity.Sister;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：网络请求处理相关
 */
public class SisterApi {
    private static final String TAG = "Network";
    private static final String BASE_URL = "http://gank.io/api/data/";


    /**
     * @param count 显示的数值
     * @param page  请求的页数
     * @return 返回获取到的数据对象
     */
    public ArrayList<Sister> fetchSister(int count,int page){
        String weal = "福利";
        try {
            weal = URLEncoder.encode(weal,"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        String fetchUrl = BASE_URL + weal + "/" + count + "/" + page;
        ArrayList<Sister> sisters = new ArrayList<Sister>();
        try{
            URL url = new URL(fetchUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            Log.v(TAG,"Server response" + code);
            if (code == 200){
                InputStream in = conn.getInputStream();
                byte[] data = readFromStream(in);
                String result = new String(data);
                sisters = parseSister(result);
            }else{
                Log.e(TAG,"request fail" + code);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return sisters;
    }


    /**
     * @param content
     * @return
     * @throws Exception
     */
    public ArrayList<Sister> parseSister(String content) throws Exception{
        ArrayList<Sister> sisters = new ArrayList<Sister>();
        JSONObject object = JSONObject.parseObject(content);
        JSONArray array = object.getJSONArray("results");
        sisters = (ArrayList<Sister>) JSON.parseArray(array.toJSONString(),Sister.class);
        return sisters;
    }


    /**
     * @param inputStream 输入对象
     * @return
     * @throws Exception 抛出异常
     */
    public byte[] readFromStream(InputStream inputStream) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1){
            outputStream.write(buffer,0,len);
        }
        inputStream.close();
        return  outputStream.toByteArray();
    }
}
