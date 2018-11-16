package com.luckyaf.imageselection.activity;

import android.database.Cursor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luckyaf.imageselection.R;
import com.luckyaf.imageselection.base.BaseActivity;
import com.luckyaf.imageselection.base.PreviewContract;
import com.luckyaf.imageselection.fragment.PreviewItemFragment;
import com.luckyaf.imageselection.adpater.PreviewImageListAdapter;
import com.luckyaf.imageselection.adpater.PreviewPagerAdapter;
import com.luckyaf.imageselection.widget.CheckView;
import com.luckyaf.imageselection.model.StateContainer;
import com.luckyaf.imageselection.model.entity.Album;
import com.luckyaf.imageselection.model.entity.IncapableCause;
import com.luckyaf.imageselection.model.entity.Item;
import com.luckyaf.imageselection.model.entity.SelectionSpec;
import com.luckyaf.imageselection.presenter.ImagePreviewPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/11/15
 */
public class ImagePreviewActivity extends BaseActivity<ImagePreviewPresenter> implements PreviewContract.View, View.OnClickListener,
        ViewPager.OnPageChangeListener {

    /**
     * 是否是预览
     */
    public static final String EXTRA_IS_SELECTED = "extra_is_selected";
    /**
     *  点击的那个图片
     */
    public static final String EXTRA_ITEM = "extra_item";
    /**
     * 点击图片所在的相册
     */
    public static final String EXTRA_CURRENT_ALBUM = "extra_current_album";
    /**
     * 已选择的图片数据
     */
    public static final String EXTRA_DEFAULT_BUNDLE = "extra_default_bundle";
    /**
     * 返回前一页面的数据
     */
    public static final String EXTRA_RESULT_BUNDLE = "extra_result_bundle";
    /**
     * 是否点击了确定
     */
    public static final String EXTRA_RESULT_APPLY = "extra_result_apply";

    protected StateContainer mStateContainer;
    protected SelectionSpec mSpec;
    protected ViewPager mPager;
    protected PreviewPagerAdapter mAdapter;
    protected LinearLayout mButtonPick;
    protected TextView mButtonBack;
    protected TextView mButtonConfirm;
    protected CheckView mCheckView;
    protected PreviewImageListAdapter mPreviewImageListAdapter;

    protected Map<Uri, Integer> mUriMap;

    private String confirmWord;
    private int maxSelectable;

    protected int mPreviousPos = -1;

    private boolean isSelected;
    private boolean mIsAlreadySetPosition = false;




    @Override
    public void doBeforeSetContentView(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_preview;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mSpec = SelectionSpec.getInstance();
        if(savedInstanceState != null) {
            mStateContainer = StateContainer.create(this, savedInstanceState);
        }else{
            Bundle images = getIntent().getBundleExtra(EXTRA_DEFAULT_BUNDLE);
            mStateContainer = StateContainer.create(this,images);
        }
        confirmWord = mSpec.selectWord;
        maxSelectable = mSpec.maxSelectable;



    }

    @Override
    public ImagePreviewPresenter providePresenter() {
        return new ImagePreviewPresenter(this,mSpec);
    }

    @Override
    public void initView() {
        mUriMap = new HashMap<>();
        mPager = findViewById(R.id.pager);
        mPager.addOnPageChangeListener(this);
        mPager.setAdapter(mAdapter = new PreviewPagerAdapter(getSupportFragmentManager(), null));
        mButtonBack = findViewById(R.id.txt_back);
        mButtonConfirm = findViewById(R.id.txt_confirm);
        mButtonBack.setOnClickListener(this);
        mButtonConfirm.setOnClickListener(this);
        mCheckView = findViewById(R.id.check_view);
        mCheckView.setCountable(false);

        mButtonPick = findViewById(R.id.lay_pick);
        mButtonPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = mAdapter.getMediaUri(mPager.getCurrentItem());
                if (mStateContainer.isSelected(uri)) {
                    mStateContainer.removeImage(uri);
                    mCheckView.setChecked(false);
                    mPreviewImageListAdapter.remove(uri);
                } else {
                    if(mStateContainer.sigleSelection()){
                        mStateContainer.addImage(uri);
                        mCheckView.setChecked(true);
                        mPreviewImageListAdapter.replace(uri);
                    }else {
                        if (assertAddSelection()) {
                            mStateContainer.addImage(uri);
                            mCheckView.setChecked(true);
                            mPreviewImageListAdapter.add(uri);
                        }
                    }
                }
                updateApplyButton();
            }
        });
        RecyclerView mPreviewImageList = (RecyclerView) findViewById(R.id.recyclerview);
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
    public void start() {
        isSelected = getIntent().getBooleanExtra(EXTRA_IS_SELECTED,false);
        // 预览
        List<Uri> selected = mStateContainer.getImageList();
        if(isSelected){

            Log.d("预览","image size ="+selected.size());
            mAdapter.addAll(selected);
            mAdapter.notifyDataSetChanged();
            mCheckView.setChecked(true);
            mPreviousPos = 0;
            mPreviewImageListAdapter.isSelectedPreview(true);
            mPreviewImageListAdapter.addAll(selected);
        }else{
            //
            Album selectedAlbum = getIntent().getParcelableExtra(EXTRA_CURRENT_ALBUM);
            mPresenter.getAlbumImage(selectedAlbum);
            mAdapter.notifyDataSetChanged();
            Item item = getIntent().getParcelableExtra(EXTRA_ITEM);
            mCheckView.setChecked(mStateContainer.isSelected(item.getContentUri()));
            if(mSpec.single){
                mPreviewImageListAdapter.isSelectedPreview(true);
            }else{
                mPreviewImageListAdapter.isSelectedPreview(false);

            }
            mPreviewImageListAdapter.addAll(selected);


        }
        mPreviewImageListAdapter.notifyDataSetChanged();
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mSpec.onSaveInstanceState(outState);
        mStateContainer.onSaveInstanceState(outState);
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

    public void showCurrentItem(Uri uri) {
        int position;
        if (mUriMap.containsKey(uri)) {
            position = mUriMap.get(uri);
        } else {
            position = mAdapter.indexOfUri(uri);
            mUriMap.put(uri, position);
        }
        if (position == -1) {
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
            boolean checked = mStateContainer.isSelected(uri);
            mCheckView.setChecked(checked);
        }
        mPreviousPos = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void updateApplyButton() {
        int selectedCount = mStateContainer.selectedImageCount();
        if (selectedCount == 0) {
            mButtonConfirm.setText(confirmWord);
            mButtonConfirm.setEnabled(false);
        } else {
            mButtonConfirm.setEnabled(true);
            mButtonConfirm.setText(confirmWord + "(" + selectedCount + "/" + maxSelectable + ")");
        }
    }


    protected void sendBackResult(boolean apply) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT_BUNDLE, mStateContainer.getDataWithBundle());
        intent.putExtra(EXTRA_RESULT_APPLY, apply);
        setResult(Activity.RESULT_OK, intent);
    }

    private boolean assertAddSelection() {
        IncapableCause cause = mStateContainer.isAcceptable();
        IncapableCause.handleCause(this, cause);
        return cause == null;
    }


    @Override
    public void loadAlbumImage(Cursor cursor) {
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
}
