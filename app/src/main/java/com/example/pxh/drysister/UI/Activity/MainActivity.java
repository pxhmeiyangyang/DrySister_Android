package com.example.pxh.drysister.UI.Activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.pxh.drysister.ImgLoader.PictureLoader;
import com.example.pxh.drysister.ImgLoader.SisterLoader;
import com.example.pxh.drysister.Network.SisterApi;
import com.example.pxh.drysister.R;
import com.example.pxh.drysister.bean.entity.Sister;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView showImg;
    private Button showBtn;
    private Button refreshBtn;

    private ArrayList<Sister> data;
    private int curPos = 0; //当前显示的是哪一张
    private int page = 1;//当前显示的页数
    private PictureLoader loader;
    private SisterApi sisterApi;
    private SisterTask sisterTask;
    private SisterLoader mLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loader = new PictureLoader();
        sisterApi = new SisterApi();
        mLoader = SisterLoader.getInstance(MainActivity.this);
        initData();
        initUI();
    }

    private void initData(){
        data = new ArrayList<Sister>();
    }

    private void initUI(){
        showImg = findViewById(R.id.img_show);
        showBtn = findViewById(R.id.btn_show);
        refreshBtn = findViewById(R.id.btn_refresh);
        showBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.btn_show:
                if (data != null && !data.isEmpty()){
                    if (curPos > data.size() - 1){
                        curPos = 0;
                    }
//                    loader.load(showImg,data.get(curPos).getUrl());
                    mLoader.bindBitmap(data.get(curPos).getUrl(),showImg,400,400);
                    curPos ++;
                }
                break;
            case R.id.btn_refresh:
                page ++;
                sisterTask = new SisterTask();
                sisterTask.execute();
                curPos = 0;
                break;
        }
    }


    private class SisterTask extends AsyncTask<Void,Void,ArrayList<Sister>>{

        public SisterTask(){

        }

        @Override
        protected ArrayList<Sister> doInBackground(Void... voids) {
            return sisterApi.fetchSister(10,page);
        }

        @Override
        protected void onPostExecute(ArrayList<Sister> sisters) {
            super.onPostExecute(sisters);
            data.clear();
            data.addAll(sisters);
            page ++;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            sisterTask = null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        sisterTask.cancel(true);
    }
}
