package com.example.pxh.drysister.ImgLoader.Helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.util.Log;

import com.example.pxh.drysister.ImgLoader.DiskLruCache.DiskLruCache;
import com.example.pxh.drysister.ImgLoader.SisterCompress;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DiskCacheHelper {

    private static final String TAG = "DiskCacheHelper";
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;
    private static final int DISK_CACHE_INDEX = 0;

    private Context mContext;
    private DiskLruCache mDiskLruCache;
    private SisterCompress mCompress;
    private boolean mIsDiskLruCacheCreated = false;//磁盘缓存是否创建

    public DiskCacheHelper(Context mContext){
        this.mContext = mContext;
        mCompress = new SisterCompress();
        File diskCacheDir = getDiskCacheDir(mContext,"diskCache");
        if (!diskCacheDir.exists()){
            diskCacheDir.mkdir();
        }

        if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE){
            try {
                mDiskLruCache = DiskLruCache.open(diskCacheDir,1,1,DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated = true;
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    /** 获取磁盘缓存的目录
     * @param context
     * @param dirName
     * @return
     */
    public File getDiskCacheDir(Context context,String dirName){
        //判断机身存储是否可用
        boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable){
            cachePath = context.getExternalCacheDir().getPath();
        }else{
            cachePath = context.getCacheDir().getPath();
        }
        Log.v(TAG,"diskCachePath =" + cachePath);
        return new File(cachePath + File.separator + dirName);
    }

    /** 查询可用空间大小（兼容2.3一下版本）
     * @param path 文件路径
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private long getUsableSpace(File path){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }


    /**
     * 根据key加载磁盘缓存中的图片
     * @param key 缓存图片的key
     * @param reqWidth 图片的宽
     * @param reqHeight 图片的高
     * @return
     */
    public Bitmap loadBitmapFromDiskCache(String key,int reqWidth,int reqHeight) throws IOException {
        Log.v(TAG,"加载磁盘缓存中的图片");
        //判断是否在主线程里操作
        if (Looper.myLooper() == Looper.getMainLooper()){
            throw new RuntimeException("不能在UI线程中加载图片");
        }
        if (mDiskLruCache == null){
            return null;
        }

        Bitmap bitmap = null;
        //获取磁盘缓存中的图片，添加到内存缓存中
        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
        if (snapshot != null){
            FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            bitmap = mCompress.decodeBitmapFromFileDescriptor(fileDescriptor,reqWidth,reqHeight);
        }
        return bitmap;
    }

    /**
     * 将图片字节流缓存到磁盘，并返回一个bitmap用于显示
     * @param key 图片存储的key
     * @param reqWidth 图片的宽度
     * @param reqHeight  图片的高度
     * @param bytes 图片的字节流
     * @return
     */
    public Bitmap saveImgByte(String key,int reqWidth,int reqHeight,byte[] bytes){
        //判断是否在主线程中
        if (Looper.myLooper() == Looper.getMainLooper()){
            throw new RuntimeException("不能再UI线程中做网络操作！");
        }
        if (mDiskLruCache == null){
            return null;
        }
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            if (editor != null){
                OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
                outputStream.write(bytes);
                outputStream.flush();
                editor.commit();
                outputStream.close();
                return loadBitmapFromDiskCache(key,reqWidth,reqHeight);
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public DiskLruCache getmDiskLruCache(){ return  mDiskLruCache; }
    public boolean getIsDiskCacheCreate(){ return  mIsDiskLruCacheCreated; }
    public void setIsDiskLruCacheCreated(boolean status){ this.mIsDiskLruCacheCreated = status; }


}
