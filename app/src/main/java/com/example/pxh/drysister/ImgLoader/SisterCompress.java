package com.example.pxh.drysister.ImgLoader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileDescriptor;

/**
 * 描述：图片压缩类
 *
 */
public class SisterCompress {
   private static final String TAG = "ImageCompress";

   public SisterCompress(){}

    /**
     * 压缩资源图片
     * @param res 图片资源
     * @param resId 资源id
     * @param reqWidth 图片宽度
     * @param reqHeight 图片高度
     * @return BitMap
     */
   public Bitmap decodeBitmapFromResource(Resources res,int resId,int reqWidth,int reqHeight){
       BitmapFactory.Options options = new BitmapFactory.Options();
       options.inJustDecodeBounds = true;
       BitmapFactory.decodeResource(res,resId,options);
       //计算缩放比例
       options.inSampleSize = computeSimpleSize(options,reqWidth,reqWidth);
       options.inJustDecodeBounds = false;
       return BitmapFactory.decodeResource(res,resId,options);
   }

    /**
     * @param descriptor
     * @param reqWith
     * @param reqHeight
     * @return
     */
   public Bitmap decodeBitmapFromFileDescriptor(FileDescriptor descriptor,int reqWith,int reqHeight){
       BitmapFactory.Options options = new BitmapFactory.Options();
       options.inJustDecodeBounds = true;
       BitmapFactory.decodeFileDescriptor(descriptor,null,options);
       options.inSampleSize = computeSimpleSize(options,reqWith,reqHeight);
       options.inJustDecodeBounds = false;
       return BitmapFactory.decodeFileDescriptor(descriptor,null,options);
   }

    /** 计算图片缩放比例的算法
     * @param options bitmap factory options
     * @param resWidth 资源的宽度
     * @param resHeight 资源的高度
     * @return 缩放的比例
     */
   public int computeSimpleSize(BitmapFactory.Options options,int resWidth,int resHeight){
       if (resWidth == 0 || resHeight == 0){
           return 1;
       }
       int inSampleSize = 1;
       final int height = options.outHeight;
       final int width = options.outWidth;
       Log.v(TAG,"原图大小为：" + width + "x" + height);
       if (height > resHeight || width > resWidth){
           final int halfHeight = height / 2;
           final int halfWidth = width / 2;
           while ((halfHeight / inSampleSize) > resHeight && (halfWidth / inSampleSize) > width){
               inSampleSize *= 2;
           }
       }
       Log.v(TAG,"inSampleSize = " + inSampleSize);
       return inSampleSize;
   }
}
