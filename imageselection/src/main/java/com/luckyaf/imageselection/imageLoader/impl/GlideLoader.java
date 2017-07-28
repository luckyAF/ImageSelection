package com.luckyaf.imageselection.imageLoader.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.luckyaf.imageselection.imageLoader.ImageLoader;

/**
 * 类描述：glide图片加载  默认图片加载
 *
 * @author Created by luckyAF on 2017/7/18
 */

public class GlideLoader implements ImageLoader{

    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        Glide.with(context)
                .load(uri)
                .asBitmap()  // some .jpeg files are actually gif
                .placeholder(placeholder)
                .override(resize, resize)
                .centerCrop()
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        Glide.with(context)
                .load(uri)
                .asBitmap()
                .placeholder(placeholder)
                .override(resize, resize)
                .centerCrop()
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        Glide.with(context)
                .load(uri)
                .override(resizeX, resizeY)
                .priority(Priority.HIGH)
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        Glide.with(context)
                .load(uri)
                .asGif()
                .override(resizeX, resizeY)
                .priority(Priority.HIGH)
                .into(imageView);
    }

    @Override
    public boolean supportAnimatedGif() {
        return true;
    }
}
