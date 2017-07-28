package com.luckyaf.imageselection;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/7/21
 */

public class ImageSelection {
    private ImageSelection(){
    }

    public static ImageSelection getInstance(){
        return ImageSelectionHolder.INSTANCE;
    }

    public SelectionCreator from(Activity activity){
        return new SelectionCreator(activity);
    }

    public SelectionCreator from(Fragment fragment){
        return new SelectionCreator(fragment);
    }

    private final static class ImageSelectionHolder{
        private static final ImageSelection INSTANCE = new ImageSelection();
    }

}
