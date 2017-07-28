package com.luckyaf.imageselection.internal.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.luckyaf.imageselection.internal.model.SelectedItemCollection;

import java.util.List;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/7/25
 */

public class SelectedPreviewActivity extends BasePreviewActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getBundleExtra(EXTRA_DEFAULT_BUNDLE);
        List<Uri> selected = bundle.getParcelableArrayList(SelectedItemCollection.STATE_SELECTION);
        mAdapter.addAll(selected);
        mAdapter.notifyDataSetChanged();
        mCheckView.setChecked(true);
        mPreviousPos = 0;
        mPreviewImageListAdapter.isSelectedPreview(true);
        mPreviewImageListAdapter.addAll(selected);

    }
}
