package com.luckyaf.imageselection.model;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.luckyaf.imageselection.R;
import com.luckyaf.imageselection.model.entity.IncapableCause;
import com.luckyaf.imageselection.model.entity.SelectionSpec;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 类描述：状态保存容器
 *
 * @author Created by luckyAF on 2018/11/15
 */
public class StateContainer {

    /**
     * 当前选中的相册
     */
    private static final String STATE_SELECTED_ALBUM = "state_selected_album";
    /**
     * 当前选中的图片
     */
    public static final String STATE_SELECTED_IMAGE = "state_selected_image";


    /**
     * 当前选中相册
     */
    private int mCurrentAlbum;

    /**
     * 已选中的图片uri
     */
    private Set<Uri> mUris;

    private Context mContext;

    private SelectionSpec mSpec;


    public static StateContainer create(Context context,Bundle savedInstanceState) {
        return new StateContainer(context,savedInstanceState);
    }


    private StateContainer(Context context,Bundle parmas) {
        mSpec = SelectionSpec.onCreate(parmas);
        mContext = context;
        mUris = new LinkedHashSet<>();
        if (parmas == null) {
            return;
        }
        mCurrentAlbum = parmas.getInt(STATE_SELECTED_ALBUM,0);
        List<Uri> saved = parmas.getParcelableArrayList(STATE_SELECTED_IMAGE);
        if (saved != null) {
            mUris.addAll(saved);
        }

    }




    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_ALBUM, mCurrentAlbum);
        outState.putParcelableArrayList(STATE_SELECTED_IMAGE, new ArrayList<>(mUris));

    }


    public void setStateCurrentAlbum(int currentAlbum) {
        mCurrentAlbum = currentAlbum;
    }

    public int getCurrentAlbum() {
        return mCurrentAlbum;
    }

    public Bundle getDataWithBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(STATE_SELECTED_ALBUM,mCurrentAlbum);
        bundle.putParcelableArrayList(STATE_SELECTED_IMAGE, new ArrayList<>(mUris));
        return bundle;
    }

    public List<Uri> getImageList(){
        return new ArrayList<>(mUris);
    }

    public boolean addImage(Uri uri) {
        if(mSpec.single){
            mUris.clear();
        }
        return mUris.add(uri);
    }

    public void addImages(List<Uri> images){
        mUris.addAll(images);
    }

    public boolean removeImage(Uri uri) {
        return mUris.remove(uri);
    }

    public void updateImages(List<Uri> uris) {
        mUris.clear();
        mUris.addAll(uris);
    }
    public int selectedImageCount() {
        return mUris.size();
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




    public boolean sigleSelection(){
        return mSpec.single || mSpec.maxSelectable == 1;
    }






}
