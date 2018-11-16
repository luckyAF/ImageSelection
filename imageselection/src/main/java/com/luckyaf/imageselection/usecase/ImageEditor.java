package com.luckyaf.imageselection.usecase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v4.os.EnvironmentCompat;

import com.luckyaf.imageselection.callback.CropUriGetter;
import com.luckyaf.imageselection.utils.ProviderUtil;
import com.luckyaf.imageselection.utils.SmartJump;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * 类描述：图片处理
 *
 * @author Created by luckyAF on 2018/11/14
 */
public class ImageEditor {
    private Context mContext;
    private SmartJump smartJump;


    public static ImageEditor from(@NonNull FragmentActivity activity){
        return new ImageEditor(activity);
    }
    public static ImageEditor from(@NonNull Fragment fragment){
        return new ImageEditor(fragment);
    }




    public ImageEditor(@NonNull FragmentActivity activity) {
        mContext = activity;
        smartJump = SmartJump.from(activity);
    }

    public ImageEditor(@NonNull Fragment fragment) {
        mContext = fragment.getActivity();
        smartJump = SmartJump.from(fragment);
    }


    public void crop(Uri imageUri,final CropUriGetter mCallback) {
        File newFile =createCropFile(imageUri);
        final Uri newUri = FileProvider.getUriForFile(mContext, ProviderUtil.getFileProviderName(mContext), newFile);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        // 设置为true直接返回bitmap
        intent.putExtra("return-data", false);
        // 上面设为false的时候将MediaStore.EXTRA_OUTPUT关联一个Uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, newUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        smartJump.startForResult(intent, new SmartJump.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if( resultCode == RESULT_OK){
                    mCallback.getCropUri(newUri);
                }
            }
        });

    }

    private File createCropFile(Uri imageUri){
        // Create an image file name
        String timeStamp =imageUri.getEncodedPath();
        String imageFileName = String.format("%s_crop.jpg", timeStamp);
        // Avoid joining path components manually
        File tempFile = new File( imageFileName);
        // Handle the situation that user's external storage is not ready
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }


}
