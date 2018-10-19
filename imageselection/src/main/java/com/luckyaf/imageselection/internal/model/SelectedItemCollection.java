package com.luckyaf.imageselection.internal.model;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.luckyaf.imageselection.R;
import com.luckyaf.imageselection.internal.entity.IncapableCause;
import com.luckyaf.imageselection.internal.entity.SelectionSpec;
import com.luckyaf.imageselection.internal.utils.PathUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 类描述：已选择的图片(url)
 *
 * @author Created by luckyAF on 2017/7/19
 */
@SuppressWarnings("unused")
public class SelectedItemCollection {

    public static final String STATE_SELECTION = "state_selection";
    public static final String STATE_COLLECTION_TYPE = "state_collection_type";

    private final Context mContext;
    private Set<Uri> mUris;
    private SelectionSpec mSpec;

    public SelectedItemCollection(Context context) {
        mContext = context;
    }

    public void onCreate(Bundle bundle, SelectionSpec spec) {
        if (bundle != null) {
            List<Uri> saved = bundle.getParcelableArrayList(STATE_SELECTION);
            if(saved != null) {
                mUris = new LinkedHashSet<>(saved);
            }else{
                mUris = new LinkedHashSet<>();
            }
        } else {
            mUris = new LinkedHashSet<>();
        }
        mSpec = spec;
    }

    public void setDefaultSelection(List<Uri> uris) {
        mUris.addAll(uris);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mUris));
    }

    public Bundle getDataWithBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mUris));
        return bundle;
    }

    public boolean add(Uri uri) {
        return mUris.add(uri);
    }



    public boolean remove(Uri uri) {
        return mUris.remove(uri);
    }

    public void overwrite(ArrayList<Uri> uris) {
        mUris.clear();
        mUris.addAll(uris);
    }


    public List<Uri> asList() {
        return new ArrayList<>(mUris);
    }



    public List<String> asListOfString() {
        List<String> paths = new ArrayList<>();
        for (Uri uri : mUris) {
            paths.add(PathUtils.getPath(mContext, uri));
        }
        return paths;
    }

    public boolean isEmpty() {
        return mUris == null || mUris.isEmpty();
    }

    public boolean isSelected(Uri uri) {
        return mUris.contains(uri);
    }

    public IncapableCause isAcceptable() {
        if (maxSelectableReached()) {
            int maxSelectable = mSpec.maxSelectable;
            String cause;
            cause = mContext.getString(
                    R.string.error_over_count,
                    maxSelectable);
            return new IncapableCause(cause);
        }
        return null;
    }

    public boolean maxSelectableReached() {
        return mUris.size() == mSpec.maxSelectable;
    }



    public int count() {
        return mUris.size();
    }

}

