package com.luckyaf.imageselection.base;

import android.database.Cursor;

import com.luckyaf.imageselection.model.entity.Album;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/11/14
 */
public interface DisplayContract {

    interface View extends IView{
        void loadAlbumList(Cursor cursor);
        void loadAlbumImage(Cursor cursor);
    }


    interface Presenter extends IPresenter<View>{


        //void clipImage()

        /**
         * 获取图片文件夹
         */
        void getAlbumList();

        /**
         * 获取文件夹内图片
         * @param album 文件夹
         */
        void getAlbumImage(Album album);


    }
}
