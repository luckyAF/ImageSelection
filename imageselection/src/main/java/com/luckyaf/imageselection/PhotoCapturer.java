package com.luckyaf.imageselection;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.luckyaf.imageselection.internal.utils.SmartJump;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/10/25
 */
public class PhotoCapturer {

    private Context mContext;
    private SmartJump smartJump;
    private File outputImagepath;
    private PhotoCallBack mCallBack;

    public PhotoCapturer(@NonNull FragmentActivity activity){
        mContext = activity;
        smartJump =  SmartJump.from(activity);
    }
    public  PhotoCapturer(@NonNull Fragment fragment){
        mContext = fragment.getActivity();
        smartJump =  SmartJump.from(fragment);
    }

    public PhotoCapturer setCallBack(PhotoCallBack callBack){
        this.mCallBack = callBack;
        return this;
    }

    public void start(){
        //获取系統版本
        int currentapiVersion = Build.VERSION.SDK_INT;
        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss",Locale.CHINA);
            String filename = timeStampFormat.format(new Date());
            outputImagepath = new File(Environment.getExternalStorageDirectory(),
                    filename + ".jpg");
            if (currentapiVersion < 24) {
                // 从文件中创建uri
                Uri uri = Uri.fromFile(outputImagepath);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            } else {
                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, outputImagepath.getAbsolutePath());
                Uri uri = mContext.getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
        }

        smartJump.startForResult(intent, new SmartJump.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if(resultCode == RESULT_OK ){
                    if(null != mCallBack) {
                        mCallBack.getPhotoPath(outputImagepath.getAbsolutePath());
                    }

                }
            }
        });

    }

    private  boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public interface PhotoCallBack{
        void getPhotoPath(String path);
    }


}
