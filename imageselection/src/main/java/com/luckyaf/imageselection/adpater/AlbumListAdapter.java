package com.luckyaf.imageselection.adpater;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.luckyaf.imageselection.R;
import com.luckyaf.imageselection.model.entity.Album;
import com.luckyaf.imageselection.model.entity.SelectionSpec;

import java.io.File;

/**
 * 类描述：相册adpater
 *
 * @author Created by luckyAF on 2017/7/20
 */
@SuppressWarnings("unused")
public class AlbumListAdapter extends CursorAdapter {

    private final Drawable mPlaceholder;
    public AlbumListAdapter(Context context, Cursor c, boolean autoReQuery) {
        super(context, c, autoReQuery);
        mPlaceholder = ContextCompat.getDrawable(context, R.drawable.ic_default_image);
    }

    public AlbumListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mPlaceholder = ContextCompat.getDrawable(context, R.drawable.ic_default_image);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.album_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Album album = Album.valueOf(cursor);
        ((TextView) view.findViewById(R.id.album_name)).setText(album.getDisplayName(context));
        ((TextView) view.findViewById(R.id.album_media_count)).setText(String.valueOf(album.getCount()));

        // do not need to load animated Gif
        SelectionSpec.getInstance().imageLoader.loadThumbnail(context, context.getResources().getDimensionPixelSize(R
                        .dimen.image_grid_size), mPlaceholder,
                (ImageView) view.findViewById(R.id.album_cover), Uri.fromFile(new File(album.getCoverPath())));
    }
}
