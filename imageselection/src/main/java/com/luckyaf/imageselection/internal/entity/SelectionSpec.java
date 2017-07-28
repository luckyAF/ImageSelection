package com.luckyaf.imageselection.internal.entity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import com.luckyaf.imageselection.imageLoader.ImageLoader;
import com.luckyaf.imageselection.imageLoader.impl.GlideLoader;


/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/7/19
 */

public final class SelectionSpec {
    /**
     * 是否要gif(默认false 不要)
     */
    public boolean needGif;
    /**
     * 最大选择图片数(默认为9)
     */
    public int maxSelectable;
    /**
     * 是否能拍照(默认可以)
     */
    public boolean capture;

    /**
     * 主题色
     */
    @ColorInt
    public int themeColor;
    /**
     * 是否使用沉浸式状态栏(默认开启)
     */
    public  boolean translucent;
    /**
     * 选择图片title   如   图片／选择图片
     */
    public String titleWord;
    /**
     * 选中图片返回的按钮 字（如 发送，选择）
     */
    public String selectWord;
    /**
     * 是否存放在公共空间(默认 为否)
     */
    public boolean savePublic;
    /**
     * 图片是否可编辑(默认不可以)
     */
    public boolean editable;
    /**
     * 图片加载框架
     */
    public ImageLoader imageLoader;

    public static SelectionSpec getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static SelectionSpec getCleanInstance() {
        SelectionSpec selectionSpec = getInstance();
        selectionSpec.reset();
        return selectionSpec;
    }

    public static SelectionSpec onCreate(Bundle bundle){
        SelectionSpec selectionSpec = getInstance();
        if (bundle != null) {
            selectionSpec.needGif = bundle.getBoolean("needGif");
            selectionSpec.capture = bundle.getBoolean("capture");
            selectionSpec.translucent = bundle.getBoolean("translucent");
            selectionSpec.savePublic = bundle.getBoolean("savePublic");
            selectionSpec.maxSelectable = bundle.getInt("maxSelectable");
            selectionSpec.themeColor = bundle.getInt("themeColor");
            selectionSpec.titleWord = bundle.getString("titleWord");
            selectionSpec.selectWord = bundle.getString("selectWord");
        }
        return selectionSpec;
    }
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("needGif",needGif);
        outState.putBoolean("capture",capture);
        outState.putBoolean("translucent",translucent);
        outState.putBoolean("savePublic",savePublic);
        outState.putInt("maxSelectable",maxSelectable);
        outState.putInt("themeColor",themeColor);
        outState.putString("titleWord",titleWord);
        outState.putString("selectWord",selectWord);
    }

    private void reset() {
        needGif = false;
        maxSelectable = 9;
        capture = true;
        themeColor = Color.parseColor("#1E8AE8");
        translucent = true;
        titleWord ="选择图片";
        selectWord = "确认";
        savePublic = true;
        imageLoader = new GlideLoader();
    }


    private final static class InstanceHolder{
        private static final SelectionSpec INSTANCE = new SelectionSpec();
    }
}
