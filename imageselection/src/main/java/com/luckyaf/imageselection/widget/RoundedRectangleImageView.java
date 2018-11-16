package com.luckyaf.imageselection.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/7/21
 */

public class RoundedRectangleImageView extends AppCompatImageView {
    private static float radius; // dp
    private RectF rect;
    private Path roundedRectPath;

    public RoundedRectangleImageView(Context context) {
        super(context);
        init(context);
    }

    public RoundedRectangleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundedRectangleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        radius = 2.0f * density;
        roundedRectPath = new Path();
        rect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        rect.set(0.0f, 0.0f, getWidth(), getHeight());
        roundedRectPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(roundedRectPath);
        super.onDraw(canvas);
    }
}
