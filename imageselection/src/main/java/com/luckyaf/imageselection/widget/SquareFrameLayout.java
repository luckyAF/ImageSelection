package com.luckyaf.imageselection.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/7/19
 */

public class SquareFrameLayout extends FrameLayout {

    public SquareFrameLayout(Context context) {
        super(context);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //这个警告去不掉  心烦
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
