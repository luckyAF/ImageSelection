package com.luckyaf.imageselection.callback;

import android.net.Uri;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/11/16
 */
public interface CropUriGetter {
    /**
     * 获取裁剪后照片地址
     * @param uri 地址
     */
    void getCropUri(Uri uri);
}
