package com.luckyaf.imageselection;

import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.luckyaf.imageselection.callback.ImageGetter;
import com.luckyaf.imageselection.imageLoader.ImageLoader;
import com.luckyaf.imageselection.model.entity.ImageData;
import com.luckyaf.imageselection.model.entity.SelectionSpec;
import com.luckyaf.imageselection.utils.SmartJump;
import com.luckyaf.imageselection.activity.ImageDisplayActivity;

import static android.app.Activity.RESULT_OK;
import static com.luckyaf.imageselection.activity.ImageDisplayActivity.IMAGE_DATA;

/**
 * 类描述：图片选择器创建者
 *
 * @author Created by luckyAF on 2017/7/19
 */
@SuppressWarnings("unused")
public class SelectionCreator {




    private final SelectionSpec mSelectionSpec;

    private FragmentManager supportFragmentManager;
    private ImageGetter imageGetter;


    SelectionCreator(FragmentActivity activity) {
        supportFragmentManager = activity.getSupportFragmentManager();
        mSelectionSpec = SelectionSpec.getCleanInstance();
    }

    SelectionCreator(Fragment fragment) {
        supportFragmentManager = fragment.getChildFragmentManager();
        mSelectionSpec = SelectionSpec.getCleanInstance();

    }


    public SelectionCreator needGif(boolean needGif) {
        mSelectionSpec.needGif = needGif;
        return this;
    }

    public SelectionCreator maxSelectable(int maxSelectable) {
        mSelectionSpec.maxSelectable = maxSelectable;
        mSelectionSpec.single = maxSelectable == 1;
        return this;
    }


    public SelectionCreator capture(boolean capture) {
        mSelectionSpec.capture = capture;
        return this;
    }



    public SelectionCreator crop(boolean crop) {
        mSelectionSpec.crop = crop;
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

    public SelectionCreator savePublic(boolean savePublic) {
        mSelectionSpec.savePublic = savePublic;
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
        mSelectionSpec.check();
        SmartJump.with(supportFragmentManager).startForResult(ImageDisplayActivity.class, new SmartJump.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                handleResult(resultCode, data);
            }
        });

    }

    private void handleResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ImageData imageData = data.getParcelableExtra(IMAGE_DATA);
            imageGetter.getImageSuccess(imageData);
        }
    }


}
