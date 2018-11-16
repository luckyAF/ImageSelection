package com.luckyaf.imageselection.callback;

import android.net.Uri;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/11/16
 */
public interface PhotoUriGetter {
    /**
     * 获取照片地址
     * @param uri 地址
     */
    void getPhotoUri(Uri uri);
}
