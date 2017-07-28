package com.luckyaf.imageselection.internal.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.luckyaf.imageselection.R;
import com.luckyaf.imageselection.internal.entity.IncapableCause;
import com.luckyaf.imageselection.internal.entity.SelectionSpec;
import com.luckyaf.imageselection.internal.model.SelectedItemCollection;
import com.luckyaf.imageselection.internal.ui.adapter.PreviewImageListAdapter;
import com.luckyaf.imageselection.internal.ui.adapter.PreviewPagerAdapter;
import com.luckyaf.imageselection.internal.ui.widget.CheckView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/7/20
 */

public class BasePreviewActivity extends AppCompatActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener {

    public static final String EXTRA_DEFAULT_BUNDLE = "extra_default_bundle";
    public static final String EXTRA_RESULT_BUNDLE = "extra_result_bundle";
    public static final String EXTRA_RESULT_APPLY = "extra_result_apply";

    protected final SelectedItemCollection mSelectedCollection = new SelectedItemCollection(this);
    protected SelectionSpec mSpec;
    protected ViewPager mPager;
    protected PreviewPagerAdapter mAdapter;
    protected LinearLayout mButtonPick;
    protected TextView mButtonBack;
    protected TextView mButtonConfirm;
    protected CheckView mCheckView;
    protected PreviewImageListAdapter mPreviewImageListAdapter;

    protected Map<Uri,Integer> mUriMap;

    private String confirmWord;
    private int maxSelectable;

    protected int mPreviousPos = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_image_preview);
        mSpec = SelectionSpec.getInstance();
        if (savedInstanceState == null) {
            mSelectedCollection.onCreate(getIntent().getBundleExtra(EXTRA_DEFAULT_BUNDLE), mSpec);
        } else {
            mSelectedCollection.onCreate(savedInstanceState, mSpec);
        }

        confirmWord = mSpec.selectWord;
        maxSelectable = mSpec.maxSelectable;
        mUriMap = new HashMap<>() ;
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.addOnPageChangeListener(this);
        mPager.setAdapter(mAdapter = new PreviewPagerAdapter(getSupportFragmentManager(), null));

        mButtonBack = (TextView)findViewById(R.id.txt_back) ;
        mButtonConfirm = (TextView)findViewById(R.id.txt_confirm);
        mButtonBack.setOnClickListener(this);
        mButtonConfirm.setOnClickListener(this);
        mCheckView = (CheckView) findViewById(R.id.check_view);
        mCheckView.setCountable(false);
        mButtonPick = (LinearLayout) findViewById(R.id.lay_pick);
        mButtonPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = mAdapter.getMediaUri(mPager.getCurrentItem());
                if (mSelectedCollection.isSelected(uri)) {
                    mSelectedCollection.remove(uri);
                    mCheckView.setChecked(false);
                    mPreviewImageListAdapter.remove(uri);
                } else {
                    if (assertAddSelection()) {
                        mSelectedCollection.add(uri);
                        mCheckView.setChecked(true);
                        mPreviewImageListAdapter.add(uri);
                    }
                }
                updateApplyButton();
            }
        });
        RecyclerView mPreviewImageList = (RecyclerView)findViewById(R.id.recyclerview) ;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPreviewImageList.setLayoutManager(linearLayoutManager);
        mPreviewImageListAdapter = new PreviewImageListAdapter(this);
        mPreviewImageListAdapter.setOnImageClickListener(new PreviewImageListAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(Uri uri, int adapterPosition) {
                showCurrentItem(uri);
            }
        });
        mPreviewImageList.setAdapter(mPreviewImageListAdapter);
        updateApplyButton();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mSpec.onSaveInstanceState(outState);
        mSelectedCollection.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        sendBackResult(false);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txt_back) {
            onBackPressed();
        } else if (v.getId() == R.id.txt_confirm) {
            sendBackResult(true);
            finish();
        }
    }

    public void showCurrentItem(Uri uri){
        int position;
        if(mUriMap.containsKey(uri)){
            position =  mUriMap.get(uri);
        }else{
            position = mAdapter.indexOfUri(uri);
            mUriMap.put(uri,position);
        }
        if(position == -1){
            return;
        }
        mPager.setCurrentItem(position);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        PreviewPagerAdapter adapter = (PreviewPagerAdapter) mPager.getAdapter();
        if (mPreviousPos != -1 && mPreviousPos != position) {
            ((PreviewItemFragment) adapter.instantiateItem(mPager, mPreviousPos)).resetView();

            Uri uri = adapter.getMediaUri(position);

            boolean checked = mSelectedCollection.isSelected(uri);
            mCheckView.setChecked(checked);
        }
        mPreviousPos = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void updateApplyButton() {
        int selectedCount = mSelectedCollection.count();
        if (selectedCount == 0) {
            mButtonConfirm.setText(confirmWord);
            mButtonConfirm.setEnabled(false);
        } else {
            mButtonConfirm.setEnabled(true);
            mButtonConfirm.setText(confirmWord+"("+selectedCount+"/" + maxSelectable +")");
        }
    }


    protected void sendBackResult(boolean apply) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT_BUNDLE, mSelectedCollection.getDataWithBundle());
        intent.putExtra(EXTRA_RESULT_APPLY, apply);
        setResult(Activity.RESULT_OK, intent);
    }

    private boolean assertAddSelection() {
        IncapableCause cause = mSelectedCollection.isAcceptable();
        IncapableCause.handleCause(this, cause);
        return cause == null;
    }


}
