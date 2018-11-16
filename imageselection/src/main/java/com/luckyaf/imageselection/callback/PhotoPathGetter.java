package com.luckyaf.imageselection.callback;

/**
 * 类描述：拍照照片回调
 *
 * @author Created by luckyAF on 2018/11/14
 */
public interface PhotoPathGetter {
    /**
     * 获取照片地址
     * @param path 地址
     */
    void getPhotoPath(String path);
}
