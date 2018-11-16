package com.luckyaf.imageselection.usecase;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v4.os.EnvironmentCompat;
import android.util.Log;
import android.widget.Toast;

import com.luckyaf.imageselection.callback.CropUriGetter;
import com.luckyaf.imageselection.callback.PhotoPathGetter;
import com.luckyaf.imageselection.callback.PhotoUriGetter;
import com.luckyaf.imageselection.utils.FileUtils;
import com.luckyaf.imageselection.utils.PathUtils;
import com.luckyaf.imageselection.utils.ProviderUtil;
import com.luckyaf.imageselection.utils.SmartJump;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * 类描述：拍照获取图片
 *
 * @author Created by luckyAF on 2018/10/25
 */
public class PhotoCapturer {
    private Context mContext;
    private SmartJump smartJump;
    private File outputImagepath;
    private PhotoPathGetter mPathGetter;
    private PhotoUriGetter mUriGetter;
    private boolean savePublic;

    public static PhotoCapturer from(@NonNull FragmentActivity activity) {
        return new PhotoCapturer(activity);
    }

    public static PhotoCapturer from(@NonNull Fragment fragment) {
        return new PhotoCapturer(fragment);
    }

    public PhotoCapturer(@NonNull FragmentActivity activity) {
        mContext = activity;
        smartJump = SmartJump.from(activity);
    }

    public PhotoCapturer(@NonNull Fragment fragment) {
        mContext = fragment.getActivity();
        smartJump = SmartJump.from(fragment);
    }

    public PhotoCapturer setPathCallBack(PhotoPathGetter callBack) {
        this.mPathGetter = callBack;
        return this;
    }

    public PhotoCapturer setUriCallBack(PhotoUriGetter callBack) {
        this.mUriGetter = callBack;
        return this;
    }

    public PhotoCapturer savePublic(boolean savePublic) {
        this.savePublic = savePublic;
        return this;
    }

    public void start(final boolean crop) {
        if (!hasCameraFeature(mContext)) {
            Toast.makeText(mContext, "没有检测到相机", Toast.LENGTH_SHORT).show();
            return;
        }
        //获取系統版本
        int currentapiVersion = Build.VERSION.SDK_INT;
        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("noFaceDetection", true);
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            outputImagepath = createImageFile(false);
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
                Log.d("getPhotoUri", "onActivityResult resultCode = " + resultCode);
                Uri image = FileProvider.getUriForFile(mContext, ProviderUtil.getFileProviderName(mContext), outputImagepath);
                if (crop) {
                    cropImage(outputImagepath, new CropUriGetter() {
                        @Override
                        public void getCropUri(Uri uri) {
                            if (null != mUriGetter) {
                                mUriGetter.getPhotoUri(uri);
                            }
                        }
                    });
                } else {
                    if (resultCode == RESULT_OK) {
                        Log.d("getPhotoUri", "resultCode == RESULT_OK");
                        if (null != mPathGetter) {
                            mPathGetter.getPhotoPath(outputImagepath.getAbsolutePath());
                        }
                        if (null != mUriGetter) {
                            mUriGetter.getPhotoUri(image);
                        }
                    }
                }

            }
        });

    }


    public void cropImage(File originalFile, final CropUriGetter mCallback) {
        String fileId = UUID.randomUUID().toString();
        File mCropFile = getCacheFile(new File(getDiskCacheDir(mContext)), fileId + "crop_image.jpg");
        final Uri mCropUri = Uri.fromFile(mCropFile);
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            //自己使用Content Uri替换File Uri
            intent.setDataAndType(getImageContentUri(mContext, originalFile), "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 180);
            intent.putExtra("outputY", 180);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", false);
            //定义输出的File Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true);
            smartJump.startForResult(intent, new SmartJump.Callback() {
                @Override
                public void onActivityResult(int resultCode, Intent data) {
                    if (resultCode == RESULT_OK) {
                        mCallback.getCropUri(mCropUri);
                    }
                }
            });
        } catch (ActivityNotFoundException e) {
            String errorMessage = "您的设备不支持裁剪操作";
            Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }


    private static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
    private String getDiskCacheDir(Context context) {
        String cachePath = null;
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) && null != context.getExternalCacheDir()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }
    private File getCacheFile(File parent, String child) {
        // 创建File对象，用于存储拍照后的图片
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = mContext.getExternalCacheDir().getPath();
            //.replaceAll("cache","medtap");
        } else {
            cachePath = mContext.getCacheDir().getPath();
            //.replaceAll("cache","medtap");
        }
        File dir = new File(cachePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, child);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private File createImageFile(boolean isCrop) {
        // Create an image file name
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName;
        if (isCrop) {
            imageFileName = String.format("JPEG_%s_crop.jpg", timeStamp);
        } else {
            imageFileName = String.format("JPEG_%s.jpg", timeStamp);
        }
        File storageDir;

        if (savePublic) {
            storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
        } else {
            if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) && null != mContext.getExternalCacheDir()) {
                storageDir = mContext.getExternalCacheDir();
            } else {
                storageDir = mContext.getCacheDir();
            }
        }

        // Avoid joining path components manually
        File tempFile = new File(storageDir, imageFileName);

        // Handle the situation that user's external storage is not ready
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }

        return tempFile;
    }

    /**
     * 检查是否有SD卡
     *
     * @return true if has SD card
     */
    private boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 检查设备是否有相机
     *
     * @param context a context to check for camera feature.
     * @return true if the device has a camera feature. false otherwise.
     */
    private boolean hasCameraFeature(Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


}
