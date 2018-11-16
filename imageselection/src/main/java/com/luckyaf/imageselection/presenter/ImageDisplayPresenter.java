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
import com.luckyaf.imageselection.model.entity.Album;
import com.luckyaf.imageselection.model.entity.SelectionSpec;
import com.luckyaf.imageselection.model.loader.AlbumImageLoader;
import com.luckyaf.imageselection.model.loader.AlbumLoader;

import java.lang.ref.WeakReference;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/11/14
 */
public class ImageDisplayPresenter implements DisplayContract.Presenter {

    private static final int LOADER_ALBUM = 188;
    private static final int LOADER_IMAGE = 408;

    private DisplayContract.View mView;
    private WeakReference<Context> mContext;

    private LoaderManager mLoaderManager;
    private SelectionSpec mSelectionSpec;

    public ImageDisplayPresenter(AppCompatActivity activity, SelectionSpec selectionSpec) {
        mContext = new WeakReference<Context>(activity);
        mLoaderManager = LoaderManager.getInstance(activity);
        mSelectionSpec = selectionSpec;

    }


    @Override
    public void getAlbumList() {
        mLoaderManager.initLoader(LOADER_ALBUM, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @NonNull
            @Override
            public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
                Context context = mContext.get();
                if (context == null) {
                    return null;
                }
                return new AlbumLoader(context);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
                Context context = mContext.get();
                if (context == null) {
                    return;
                }
                mView.loadAlbumList(cursor);
            }

            @Override
            public void onLoaderReset(@NonNull Loader<Cursor> loader) {
                Context context = mContext.get();
                if (context == null) {
                    return;
                }
                mView.loadAlbumList(null);
            }
        });
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
                return AlbumImageLoader.newInstance(context, album,
                        album.isAll() && mSelectionSpec.capture);
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
    public void attachView(DisplayContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mContext = null;
        mLoaderManager.destroyLoader(LOADER_IMAGE);
        mLoaderManager.destroyLoader(LOADER_ALBUM);

    }
}
