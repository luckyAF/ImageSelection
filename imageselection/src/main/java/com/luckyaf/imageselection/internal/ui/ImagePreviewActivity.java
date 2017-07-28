package com.luckyaf.imageselection.internal.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.luckyaf.imageselection.internal.entity.Album;
import com.luckyaf.imageselection.internal.entity.Item;
import com.luckyaf.imageselection.internal.model.AlbumImageCollection;
import com.luckyaf.imageselection.internal.ui.adapter.PreviewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：某个文件夹下的所有图片预览
 *
 * @author Created by luckyAF on 2017/7/25
 */

public class ImagePreviewActivity extends  BasePreviewActivity implements
        AlbumImageCollection.AlbumImageCallbacks{

    public static final String EXTRA_ALBUM = "extra_album";
    public static final String EXTRA_ITEM = "extra_item";

    private AlbumImageCollection mCollection = new AlbumImageCollection();

    private boolean mIsAlreadySetPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCollection.onCreate(this, this);
        Album album = getIntent().getParcelableExtra(EXTRA_ALBUM);
        mCollection.load(album);
        Item item = getIntent().getParcelableExtra(EXTRA_ITEM);
        mCheckView.setChecked(mSelectedCollection.isSelected(item.getContentUri()));

        mPreviewImageListAdapter.isSelectedPreview(false);
        mPreviewImageListAdapter.addAll(mSelectedCollection.asList());
        mPreviewImageListAdapter.isSelectedPreview(false);
    }
    @Override
    public void onAlbumImageLoad(Cursor cursor) {
        List<Item> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            items.add(Item.valueOf(cursor));
        }
        PreviewPagerAdapter adapter = (PreviewPagerAdapter) mPager.getAdapter();
        adapter.addAllItems(items);
        adapter.notifyDataSetChanged();
        if (!mIsAlreadySetPosition) {
            //onAlbumMediaLoad is called many times..
            mIsAlreadySetPosition = true;
            Item selected = getIntent().getParcelableExtra(EXTRA_ITEM);
            int selectedIndex = items.indexOf(selected);
            mPager.setCurrentItem(selectedIndex, false);
            mPreviousPos = selectedIndex;
        }
    }

    @Override
    public void onAlbumImageReset() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCollection.onDestroy();
    }
}
