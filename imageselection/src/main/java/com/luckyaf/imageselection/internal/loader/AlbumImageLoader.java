package com.luckyaf.imageselection.internal.loader;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.luckyaf.imageselection.internal.entity.Album;
import com.luckyaf.imageselection.internal.entity.Item;
import com.luckyaf.imageselection.internal.utils.PhotoHelper;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/7/19
 */

public class AlbumImageLoader extends CursorLoader {
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            "duration"};


    // 查询 所有图片
    private static final String SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }

    // 查询 文件夹内所有图片
    private static final String SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND "
                    + " bucket_id=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static String[] getSelectionAlbumArgsForSingleMediaType(int mediaType, String albumId) {
        return new String[]{String.valueOf(mediaType), albumId};
    }
    // ===============================================================

    private static final String ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC";
    private final boolean mEnableCapture;

    private AlbumImageLoader(Context context, String selection, String[] selectionArgs, boolean capture) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, ORDER_BY);
        mEnableCapture = capture;
    }

    public static CursorLoader newInstance(Context context, Album album, boolean capture) {
        String selection;
        String[] selectionArgs;
        boolean enableCapture;

        if (album.isAll()) {
                selection = SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);

            enableCapture = capture;
        } else {
                selection = SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = getSelectionAlbumArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                        album.getId());

            enableCapture = false;
        }
        return new AlbumImageLoader(context, selection, selectionArgs, enableCapture);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor result = super.loadInBackground();
        if (!mEnableCapture || !PhotoHelper.hasCameraFeature(getContext())) {
            return result;
        }
        MatrixCursor dummy = new MatrixCursor(PROJECTION);
        dummy.addRow(new Object[]{Item.ITEM_ID_CAPTURE, Item.ITEM_DISPLAY_NAME_CAPTURE, "", 0, 0});
        return new MergeCursor(new Cursor[]{dummy, result});
    }

    @Override
    public void onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}
