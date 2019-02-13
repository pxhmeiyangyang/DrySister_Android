package com.example.pxh.drysister.DB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.pxh.drysister.DrySisterApp;
import com.example.pxh.drysister.bean.entity.Sister;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：数据库操作类
 */
public class SisterDBHelper {
    private static final String TAG = "SisterDBHelper";

    private static SisterDBHelper dbHelper;
    private SisterOpenHelper sqlHelper;
    private SQLiteDatabase db;

    /**
     * 创建数据库操作类对象
     */
    private SisterDBHelper(){
        sqlHelper = new SisterOpenHelper(DrySisterApp.getContext());
    }

    /**
     * 单例对象
     * @return
     */
    public static SisterDBHelper getInstance(){
        if (dbHelper == null){
            synchronized (SisterDBHelper.class){
                if (dbHelper == null){
                    dbHelper = new SisterDBHelper();
                }
            }
        }
        return dbHelper;
    }

    /**插入一个妹子对象
     * @param sister 对象
     */
    public void insertSister(Sister sister){
        db = getWritableDB();
        ContentValues contentValues = getSisterContentValues(sister);
        db.insert(TableDefine.TABLE_FULI,null,contentValues);
        closeIO(null);
    }


    /**
     * 插入一堆妹子（使用事务）
     */
    public void insertSisters(ArrayList<Sister> sisters){
        db = getWritableDB();
        db.beginTransaction();
        try {
            for (Sister sister: sisters){
                ContentValues contentValues = getSisterContentValues(sister);
                db.insert(TableDefine.TABLE_FULI,null,contentValues);
            }
            db.setTransactionSuccessful();
        }finally {
            if (db != null && db.isOpen()){
                db.endTransaction();
                closeIO(null);
            }
        }
    }


    /**
     * 插入单个妹子的具体实现
     */
    private ContentValues getSisterContentValues(Sister sister){
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableDefine.COLUMN_FULI_ID,sister.get_id());
        contentValues.put(TableDefine.COLUMN_FULI_CREATEAT,sister.getCreatedAt());
        contentValues.put(TableDefine.COLUMN_FULI_DESC,sister.getDesc());
        contentValues.put(TableDefine.COLUMN_FULI_PUBLISHEDAT,sister.getPublishedAt());
        contentValues.put(TableDefine.COLUMN_FULI_SOURCE,sister.getSource());
        contentValues.put(TableDefine.COLUMN_FULI_TYPE,sister.getType());
        contentValues.put(TableDefine.COLUMN_FULI_URL,sister.getUrl());
        contentValues.put(TableDefine.COLUMN_FULI_USED,sister.getUsed());
        contentValues.put(TableDefine.COLUMN_FULI_WHO,sister.getWho());
        return contentValues;
    }


    /**
     * 删除妹子（根据_id）
     */
    public void deleteSister(String _id){
        db = getWritableDB();
        db.delete(TableDefine.TABLE_FULI,"_id =?",new String[]{_id});
        closeIO(null);
    }


    /**
     * 删除所有妹子
     */
    public void deleteAllSisters(){
        db = getWritableDB();
        db.delete(TableDefine.TABLE_FULI,null,null);
        closeIO(null);
    }


    /**
     * 更新妹子信息根据_id
     */
    public void updateSister(String _id,Sister sister){
        db = getWritableDB();
        ContentValues contentValues = getSisterContentValues(sister);
        db.update(TableDefine.TABLE_FULI,contentValues,"_id =?",new String[]{_id});
        closeIO(null);
    }

    /**
     * 查询当前表中有多少个妹子
     */
    public int getSisterCount(){
        db = getReadableDB();
        Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TableDefine.TABLE_FULI,null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        Log.v(TAG,"count:" + count);
        closeIO(cursor);
        return count;
    }


    /**
     * 分页查询妹子，参数为当前页和每一个的数量，页数从0开始算
     */
    public List<Sister> getSistersLimit(int curPage,int limit){
        db = getReadableDB();
        List<Sister> sisters = new ArrayList<>();
        String startPos = String.valueOf(curPage * limit);//数据开始位置
        if (db != null){
            Cursor cursor = db.query(TableDefine.TABLE_FULI,new String[]{
                    TableDefine.COLUMN_FULI_ID,
                    TableDefine.COLUMN_FULI_CREATEAT,
                    TableDefine.COLUMN_FULI_DESC,
                    TableDefine.COLUMN_FULI_PUBLISHEDAT,
                    TableDefine.COLUMN_FULI_SOURCE,
                    TableDefine.COLUMN_FULI_TYPE,
                    TableDefine.COLUMN_FULI_URL,
                    TableDefine.COLUMN_FULI_USED,
                    TableDefine.COLUMN_FULI_WHO
                    },null,null,null,null,TableDefine.COLUMN_ID,
                    startPos + "," + limit);
            while (cursor.moveToNext()){
                sisters.add(getSisterByDB(cursor));

            }
            closeIO(cursor);
        }
        return sisters;
    }


    /**
     * 查询所有的妹子
     */
    public List<Sister> getAllSisters(){
        db = getReadableDB();
        List<Sister> sisters = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TableDefine.TABLE_FULI,null);
        cursor.moveToFirst();
        while (cursor.moveToNext()){
            sisters.add(getSisterByDB(cursor));
        }
        closeIO(cursor);
        return sisters;
    }

    /**
     * 通过数据库获取sister对象
     */
    private Sister getSisterByDB(Cursor cursor){
        Sister sister = new Sister();
        sister.set_id(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_ID)));
        sister.setCreatedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_CREATEAT)));
        sister.setDesc(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_DESC)));
        sister.setPublishedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_PUBLISHEDAT)));
        sister.setSource(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_SOURCE)));
        sister.setType(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_TYPE)));
        sister.setUrl(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_URL)));
        sister.setUsed(cursor.getInt(cursor.getColumnIndex(TableDefine.COLUMN_FULI_USED)));
        return sister;
    }

    /**
     * 获取可写数据库的方法
     * @return
     */
    private SQLiteDatabase getWritableDB(){
        return sqlHelper.getWritableDatabase();
    }

    /**
     * 获取可读数据库的方法
     */
    private SQLiteDatabase getReadableDB(){
        return sqlHelper.getReadableDatabase();
    }

    /**
     * 关闭cursor和数据库的方法
     */
    private void closeIO(Cursor cursor){
        if (cursor != null){
            cursor.close();
        }
        if (db != null){
            db.close();
        }

    }

}
