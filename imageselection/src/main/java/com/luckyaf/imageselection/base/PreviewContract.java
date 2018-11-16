package com.luckyaf.imageselection.base;

import android.database.Cursor;

import com.luckyaf.imageselection.model.entity.Album;

/**
 * 类描述：预览
 *
 * @author Created by luckyAF on 2018/11/15
 */
public interface PreviewContract {
    interface View extends IView{
        void loadAlbumImage(Cursor cursor);
    }

    interface Presenter extends IPresenter<View>{
        /**
         * 获取文件夹内图片
         * @param album 文件夹
         */
        void getAlbumImage(Album album);
    }
}
