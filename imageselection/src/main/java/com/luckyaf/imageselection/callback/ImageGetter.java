package com.luckyaf.imageselection.callback;

import com.luckyaf.imageselection.model.entity.ImageData;

/**
 * 类描述：图片选择回调
 *
 * @author Created by luckyAF on 2018/11/14
 */
public interface ImageGetter {
    /**
     *  获取图片成功回调
     * @param imageData 图片数据
     */
    void getImageSuccess(ImageData imageData);
}