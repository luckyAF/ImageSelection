package com.luckyaf.imageselection.adpater;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.luckyaf.imageselection.model.entity.Item;
import com.luckyaf.imageselection.fragment.PreviewItemFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/7/20
 */

public class PreviewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Uri> mUris = new ArrayList<>();
    private OnPrimaryItemSetListener mListener;

    public PreviewPagerAdapter(FragmentManager manager, OnPrimaryItemSetListener listener) {
        super(manager);
        mListener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        return PreviewItemFragment.newInstance(mUris.get(position));
    }

    @Override
    public int getCount() {
        return mUris.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (mListener != null) {
            mListener.onPrimaryItemSet(position);
        }
    }

    public Uri getMediaUri(int position) {
        return mUris.get(position);
    }

    public void addAll(List<Uri> uris) {
        mUris.addAll(uris);
    }

    public void addAllItems(List<Item> items){
        for (Item item : items) {
            mUris.add(item.getContentUri());
        }
    }

    interface OnPrimaryItemSetListener {

        void onPrimaryItemSet(int position);
    }

    public int indexOfUri(Uri uri){
        return mUris.indexOf(uri);
    }

}
