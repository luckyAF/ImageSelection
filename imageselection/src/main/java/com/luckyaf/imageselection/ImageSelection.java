package com.luckyaf.imageselection;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.luckyaf.imageselection.usecase.PhotoCapturer;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/7/21
 */

public class ImageSelection {
    private ImageSelection() {
    }

    public static ImageSelection getInstance() {
        return ImageSelectionHolder.INSTANCE;
    }



    public static PhotoCapturer getPhoto(FragmentActivity activity){
        return new PhotoCapturer(activity);
    }


    public static PhotoCapturer getPhoto(Fragment fragment){
        return new PhotoCapturer(fragment);
    }

    public SelectionCreator from(FragmentActivity activity) {
        return new SelectionCreator(activity);
    }

    public SelectionCreator from(Fragment fragment) {
        return new SelectionCreator(fragment);
    }


    private final static class ImageSelectionHolder {
        private static final ImageSelection INSTANCE = new ImageSelection();
    }

}
