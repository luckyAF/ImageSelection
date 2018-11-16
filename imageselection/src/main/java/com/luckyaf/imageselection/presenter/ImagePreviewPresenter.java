package com.luckyaf.imageselection.presenter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.luckyaf.imageselection.base.DisplayContract;
import com.luckyaf.imageselection.base.PreviewContract;
import com.luckyaf.imageselection.model.entity.Album;
import com.luckyaf.imageselection.model.entity.SelectionSpec;
import com.luckyaf.imageselection.model.loader.AlbumImageLoader;
import com.luckyaf.imageselection.model.loader.AlbumLoader;

import java.lang.ref.WeakReference;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/11/15
 */
public class ImagePreviewPresenter implements PreviewContract.Presenter {
    private static final int LOADER_IMAGE = 21548;

    private PreviewContract.View mView;
    private WeakReference<Context> mContext;

    private LoaderManager mLoaderManager;
    private SelectionSpec mSelectionSpec;

    public ImagePreviewPresenter(AppCompatActivity activity, SelectionSpec selectionSpec) {
        mContext = new WeakReference<Context>(activity);
        mLoaderManager = LoaderManager.getInstance(activity);
        mSelectionSpec = selectionSpec;

    }

    @Override
    public void getAlbumImage(final Album album) {
        mLoaderManager.destroyLoader(LOADER_IMAGE);
        mLoaderManager.initLoader(LOADER_IMAGE, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @NonNull
            @Override
            public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
                Context context = mContext.get();
                if (context == null) {
                    return null;
                }
                if(album == null){
                    return null;
                }
                return AlbumImageLoader.newInstance(context, album, false);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
                Context context = mContext.get();
                if (context == null) {
                    return;
                }

                mView.loadAlbumImage(cursor);
            }
            @Override
            public void onLoaderReset(@NonNull Loader<Cursor> loader) {
                Context context = mContext.get();
                if (context == null) {
                    return;
                }
                mView.loadAlbumImage(null);
            }
        });
    }


    @Override
    public void attachView(PreviewContract.View view) {
        mView = view;

    }

    @Override
    public void detachView() {
        mView = null;
        mContext = null;
        mLoaderManager.destroyLoader(LOADER_IMAGE);

    }
}
