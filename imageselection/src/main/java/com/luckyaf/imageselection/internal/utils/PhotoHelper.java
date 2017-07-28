package com.luckyaf.imageselection.internal.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.os.EnvironmentCompat;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/7/19
 */
@SuppressWarnings("unused")
public class PhotoHelper {
    private static final String PROVIDER = ".provider";
    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;
    private Uri mCurrentPhotoUri;
    private       String                  mCurrentPhotoPath;
    private       boolean                 savePublic;//图片存放到公共目录

    public PhotoHelper(Activity activity) {
        mContext = new WeakReference<>(activity);
        mFragment = null;
        savePublic = false;
    }

    public PhotoHelper(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
        savePublic = false;
    }

    public void setSavePublic(boolean savePublic) {
        this.savePublic = savePublic;
    }

    /**
     * 检查设备是否有相机
     *
     * @param context a context to check for camera feature.
     * @return true if the device has a camera feature. false otherwise.
     */
    public static boolean hasCameraFeature(Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void dispatchCaptureIntent(Context context, int requestCode) {
        if(hasCameraFeature(context)) {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (captureIntent.resolveActivity(context.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    mCurrentPhotoPath = photoFile.getAbsolutePath();
                    mCurrentPhotoUri = FileProvider.getUriForFile(mContext.get(),
                            mContext.get().getPackageName() + PROVIDER, photoFile);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);
                    captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        List<ResolveInfo> resInfoList = context.getPackageManager()
                                .queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            context.grantUriPermission(packageName, mCurrentPhotoUri,
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                    }
                    if (mFragment != null) {
                        mFragment.get().startActivityForResult(captureIntent, requestCode);
                    } else {
                        mContext.get().startActivityForResult(captureIntent, requestCode);
                    }
                }
            }
        }else{
            Toast.makeText(mContext.get(), "没有检测到相机", Toast.LENGTH_SHORT).show();
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = String.format("JPEG_%s.jpg", timeStamp);
        File storageDir;

        if (savePublic) {
            storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
        } else {
            //storageDir = mContext.get().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) && null != mContext.get().getExternalCacheDir()) {
                storageDir = mContext.get().getExternalCacheDir();
            } else {
                storageDir = mContext.get().getCacheDir();
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
    public Uri getCurrentPhotoUri() {
        return mCurrentPhotoUri;
    }

    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }
}

