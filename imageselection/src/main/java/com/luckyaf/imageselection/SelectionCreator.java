package com.luckyaf.imageselection;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.luckyaf.imageselection.imageLoader.ImageLoader;
import com.luckyaf.imageselection.internal.entity.SelectionSpec;
import com.luckyaf.imageselection.internal.utils.SmartJump;
import com.luckyaf.imageselection.ui.ImageSelectionActivity;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.luckyaf.imageselection.ui.ImageSelectionActivity.IMAGE_DATA;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/7/19
 */
@SuppressWarnings("unused")
public class SelectionCreator {

    public interface ImageGetter {
        void getImageSuccess(ImageData imageData);
    }


    private final SelectionSpec mSelectionSpec;

    private FragmentManager supportFragmentManager;
    private ImageGetter imageGetter;


    public SelectionCreator(FragmentActivity activity) {
        supportFragmentManager = activity.getSupportFragmentManager();
        mSelectionSpec = SelectionSpec.getCleanInstance();
    }

    public SelectionCreator(Fragment fragment) {
        supportFragmentManager = fragment.getChildFragmentManager();
        mSelectionSpec = SelectionSpec.getCleanInstance();

    }


    public SelectionCreator needGif(boolean needGif) {
        mSelectionSpec.needGif = needGif;
        return this;
    }

    public SelectionCreator maxSelectable(int maxSelectable) {
        mSelectionSpec.maxSelectable = maxSelectable;
        return this;
    }


    public SelectionCreator capture(boolean capture) {
        mSelectionSpec.capture = capture;
        return this;
    }

    public SelectionCreator themeColor(@ColorInt int themeColor) {
        mSelectionSpec.themeColor = themeColor;
        return this;
    }

    public SelectionCreator translucent(boolean translucent) {
        mSelectionSpec.translucent = translucent;
        return this;
    }

    public SelectionCreator titleWord(String titleWord) {
        mSelectionSpec.titleWord = titleWord;
        return this;
    }

    public SelectionCreator selectWord(String selectWord) {
        mSelectionSpec.selectWord = selectWord;
        return this;
    }

    private SelectionCreator savePublic(boolean savePublic) {
        mSelectionSpec.savePublic = savePublic;
        return this;
    }

    public SelectionCreator editable(boolean editable) {
        mSelectionSpec.editable = editable;
        return this;
    }

    public SelectionCreator setImageLoader(ImageLoader imageLoader) {
        mSelectionSpec.imageLoader = imageLoader;
        return this;
    }

    public SelectionCreator getImage(ImageGetter imageGetter) {
        this.imageGetter = imageGetter;
        return this;
    }

    public void start() {

        SmartJump.with(supportFragmentManager).startForResult(ImageSelectionActivity.class, new SmartJump.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                handleResult(resultCode, data);
            }
        });

    }

    private void handleResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ImageData imageData = (ImageData) data.getParcelableExtra(IMAGE_DATA);
            imageGetter.getImageSuccess(imageData);
        }
    }


}
