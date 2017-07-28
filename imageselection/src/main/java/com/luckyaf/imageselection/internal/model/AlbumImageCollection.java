package com.luckyaf.imageselection.internal.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.luckyaf.imageselection.internal.entity.Album;
import com.luckyaf.imageselection.internal.loader.AlbumImageLoader;

import java.lang.ref.WeakReference;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/7/19
 */

public class AlbumImageCollection implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 2;
    private static final String ARGS_ALBUM = "args_album";
    private static final String ARGS_ENABLE_CAPTURE = "args_enable_capture";
    private WeakReference<Context> mContext;
    private LoaderManager mLoaderManager;
    private AlbumImageCallbacks mCallbacks;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mContext.get();
        if (context == null) {
            return null;
        }

        Album album = args.getParcelable(ARGS_ALBUM);
        if (album == null) {
            return null;
        }

        return AlbumImageLoader.newInstance(context, album,
                album.isAll() && args.getBoolean(ARGS_ENABLE_CAPTURE, false));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCallbacks.onAlbumImageLoad(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCallbacks.onAlbumImageReset();
    }

    public void onCreate(@NonNull FragmentActivity context, @NonNull AlbumImageCallbacks callbacks) {
        mContext = new WeakReference<Context>(context);
        mLoaderManager = context.getSupportLoaderManager();
        mCallbacks = callbacks;
    }

    public void onDestroy() {
        mLoaderManager.destroyLoader(LOADER_ID);
        mCallbacks = null;
    }

    public void load(@Nullable Album target) {
        load(target, false);
    }

    public void load(@Nullable Album target, boolean enableCapture) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_ALBUM, target);
        args.putBoolean(ARGS_ENABLE_CAPTURE, enableCapture);
        mLoaderManager.initLoader(LOADER_ID, args, this);
    }

    public interface AlbumImageCallbacks {

        void onAlbumImageLoad(Cursor cursor);

        void onAlbumImageReset();
    }
}
