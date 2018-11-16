package com.luckyaf.imageselection.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.luckyaf.imageselection.model.entity.ImageData;
import com.luckyaf.imageselection.R;
import com.luckyaf.imageselection.adpater.AlbumListAdapter;
import com.luckyaf.imageselection.adpater.ImageDisplayAdapter;
import com.luckyaf.imageselection.base.BaseActivity;
import com.luckyaf.imageselection.base.DisplayContract;
import com.luckyaf.imageselection.callback.CropUriGetter;
import com.luckyaf.imageselection.callback.PhotoUriGetter;
import com.luckyaf.imageselection.model.entity.IncapableCause;
import com.luckyaf.imageselection.usecase.PhotoCapturer;
import com.luckyaf.imageselection.utils.PathUtils;
import com.luckyaf.imageselection.widget.AlbumsSpinner;
import com.luckyaf.imageselection.widget.ImageGridInset;
import com.luckyaf.imageselection.utils.BarUtils;
import com.luckyaf.imageselection.utils.SmartJump;
import com.luckyaf.imageselection.model.StateContainer;
import com.luckyaf.imageselection.model.entity.Album;
import com.luckyaf.imageselection.model.entity.Item;
import com.luckyaf.imageselection.model.entity.SelectionSpec;
import com.luckyaf.imageselection.presenter.ImageDisplayPresenter;

import java.io.File;
import java.util.ArrayList;

import static com.luckyaf.imageselection.model.StateContainer.STATE_SELECTED_IMAGE;

/**
 * @author xiangzhongfei
 */
public class ImageDisplayActivity extends BaseActivity<ImageDisplayPresenter> implements DisplayContract.View, View.OnClickListener {

    public static final String IMAGE_DATA = "image_data";

    private static final int PERMISSION_CAMERA_REQUEST_CODE = 26;
    private static final int PERMISSION_STORAGE_REQUEST_CODE = 25;

    private TextView mButtonConfirm;
    private TextView mTxtPreview;
    private String selectWord;
    private String previewWord;
    private int maxSelectable;
    private View mEmptyView;
    private RecyclerView mRecyclerView;
    private SelectionSpec spec;
    private StateContainer mStateContainer;
    private AlbumsSpinner mAlbumsSpinner;
    private AlbumListAdapter mAlbumListAdapter;
    private ImageDisplayAdapter mImageDisplayAdapter;
    private Album mCurrentAlbum;


    @Override
    public void initData(Bundle savedInstanceState) {
        spec = SelectionSpec.onCreate(savedInstanceState);
        selectWord = spec.selectWord;
        maxSelectable = spec.maxSelectable;
        previewWord = getString(R.string.button_preview);
        mStateContainer = StateContainer.create(this, savedInstanceState);
    }

    @Override
    public void doBeforeSetContentView() {
        if (spec.translucent) {
            BarUtils.setColor(this, spec.themeColor);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_display;
    }


    @Override
    public ImageDisplayPresenter providePresenter() {
        return new ImageDisplayPresenter(this, spec);
    }

    @Override
    public void initView() {
        ImageView imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        mButtonConfirm = findViewById(R.id.txt_confirm);
        mTxtPreview = findViewById(R.id.txt_preview);
        mButtonConfirm.setOnClickListener(this);
        mButtonConfirm.setText(selectWord);
        mTxtPreview.setOnClickListener(this);
        findViewById(R.id.toolbar).setBackgroundColor(spec.themeColor);
        findViewById(R.id.bottom_toolbar).setBackgroundColor(spec.themeColor);
        initAlbum();
        initImageDisplay();
        updateBottomToolbar();
    }

    /**
     * 初始化左上方相册选择
     */
    private void initAlbum() {

        mAlbumListAdapter = new AlbumListAdapter(this, null, false);
        mAlbumsSpinner = new AlbumsSpinner(this);
        mAlbumsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mStateContainer.setStateCurrentAlbum(position);
                mAlbumListAdapter.getCursor().moveToPosition(position);
                Album album = Album.valueOf(mAlbumListAdapter.getCursor());
                if (album.isAll() && SelectionSpec.getInstance().capture) {
                    album.addCaptureCount();
                }
                onAlbumSelected(album);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mAlbumsSpinner.setSelectedTextView((TextView) findViewById(R.id.txt_selected_album));
        mAlbumsSpinner.setPopupAnchorView(findViewById(R.id.txt_selected_album));
        mAlbumsSpinner.setAdapter(mAlbumListAdapter);
    }

    /**
     * 初始化图片展示
     */
    private void initImageDisplay() {
        mEmptyView = findViewById(R.id.empty_view);
        mRecyclerView = findViewById(R.id.recyclerview);
        mImageDisplayAdapter = new ImageDisplayAdapter(this, mStateContainer, mRecyclerView);
        mImageDisplayAdapter.registerCheckStateListener(new ImageDisplayAdapter.CheckStateListener() {
            @Override
            public void onUpdate() {
                updateBottomToolbar();
            }
        });
        mImageDisplayAdapter.registerOnImageClickListener(new ImageDisplayAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(Item item, int adapterPosition) {
                clickImageItem(item, adapterPosition);
            }
        });
        mImageDisplayAdapter.registerOnCameraClickListener(new ImageDisplayAdapter.OnCameraClickListener() {
            @Override
            public void capture() {
                clickCamera();
            }
        });
        mRecyclerView.setHasFixedSize(true);
        int spanCount = 3;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        int spacing = getResources().getDimensionPixelSize(R.dimen.image_grid_spacing);
        mRecyclerView.addItemDecoration(new ImageGridInset(spanCount, spacing, false));
        mRecyclerView.setAdapter(mImageDisplayAdapter);

    }


    /**
     * 点击拍照
     */
    private void clickCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CAMERA_REQUEST_CODE);
            Toast.makeText(this, getString(R.string.permission_camera_denied), Toast.LENGTH_SHORT).show();
        } else {
            takePhoto();
        }
    }

    private void takePhoto() {
        PhotoCapturer.from(this)
                .setUriCallBack(new PhotoUriGetter() {
                    @Override
                    public void getPhotoUri(Uri uri) {
                        if (spec.single) {
                            ImageData imageData = new ImageData(uri);
                            Intent intent = new Intent();
                            intent.putExtra(IMAGE_DATA, imageData);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            //预览
                            mStateContainer.addImage(uri);
                            updateBottomToolbar();
                            Intent intent = new Intent(ImageDisplayActivity.this, ImagePreviewActivity.class);
                            intent.putExtra(ImagePreviewActivity.EXTRA_IS_SELECTED, true);
                            intent.putExtra(ImagePreviewActivity.EXTRA_DEFAULT_BUNDLE, mStateContainer.getDataWithBundle());
                            SmartJump.from(ImageDisplayActivity.this).startForResult(intent, new SmartJump.Callback() {
                                @Override
                                public void onActivityResult(int resultCode, Intent data) {
                                    handlePreviewBack(resultCode, data);
                                }
                            });

                        }
                    }
                }).start(spec.crop);
    }


    private void handlePreviewBack(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        Bundle resultBundle = data.getBundleExtra(ImagePreviewActivity.EXTRA_RESULT_BUNDLE);
        ArrayList<Uri> selected = resultBundle.getParcelableArrayList(STATE_SELECTED_IMAGE);
        if (data.getBooleanExtra(ImagePreviewActivity.EXTRA_RESULT_APPLY, false)) {
            ImageData imageData = new ImageData(selected);
            if(spec.crop){
                File file = new File(PathUtils.getPath(mContext,imageData.getImage()));
                PhotoCapturer.from(ImageDisplayActivity.this).cropImage(file, new CropUriGetter() {
                    @Override
                    public void getCropUri(Uri uri) {
                        Intent intent = new Intent();
                        ImageData cropData = new ImageData(uri);
                        intent.putExtra(IMAGE_DATA, cropData);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }else {
                Intent intent = new Intent();
                intent.putExtra(IMAGE_DATA, imageData);
                setResult(RESULT_OK, intent);
                finish();
            }
        } else {
            mStateContainer.updateImages(selected);
            mImageDisplayAdapter.updateState(mStateContainer);
            updateBottomToolbar();
        }
    }

    /**
     * 点击图片
     *
     * @param item            item
     * @param adapterPosition 位置
     */
    private void clickImageItem(Item item, int adapterPosition) {
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        intent.putExtra(ImagePreviewActivity.EXTRA_CURRENT_ALBUM, mCurrentAlbum);
        intent.putExtra(ImagePreviewActivity.EXTRA_ITEM, item);
        intent.putExtra(ImagePreviewActivity.EXTRA_DEFAULT_BUNDLE, mStateContainer.getDataWithBundle());

        SmartJump.from(this).startForResult(intent, new SmartJump.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                handlePreviewBack(resultCode, data);
            }
        });


    }


    private void updateBottomToolbar() {
        int selectedCount = mStateContainer.selectedImageCount();
        if (selectedCount == 0) {
            mTxtPreview.setEnabled(false);
            mButtonConfirm.setEnabled(false);
            mTxtPreview.setText(previewWord);
            mButtonConfirm.setText(selectWord);
        } else {
            mTxtPreview.setEnabled(true);
            mButtonConfirm.setEnabled(true);
            mTxtPreview.setText(previewWord + "(" + selectedCount + ")");
            mButtonConfirm.setText(selectWord + "(" + selectedCount + "/" + maxSelectable + ")");
        }
    }

    /**
     * 选择个文件夹
     *
     * @param album 文件夹
     */
    private void onAlbumSelected(Album album) {
        mCurrentAlbum = album;
        if (!album.isAll() && album.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
        mPresenter.getAlbumImage(album);

    }


    @Override
    public void start() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_STORAGE_REQUEST_CODE);
        } else {
            mPresenter.getAlbumList();
        }
    }

    @Override
    public void loadAlbumList(final Cursor cursor) {
        mAlbumListAdapter.swapCursor(cursor);
        // select default album.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                cursor.moveToPosition(mStateContainer.getCurrentAlbum());
                mAlbumsSpinner.setSelection(ImageDisplayActivity.this,
                        mStateContainer.getCurrentAlbum());
                Album album = Album.valueOf(cursor);
                mCurrentAlbum = album;
                if (album.isAll() && SelectionSpec.getInstance().capture) {
                    album.addCaptureCount();
                }
                onAlbumSelected(album);
            }
        });
    }

    @Override
    public void loadAlbumImage(Cursor cursor) {
        mImageDisplayAdapter.swapCursor(cursor);
    }

    @Override
    public void onClick(View v) {
//        //library 里面不能 switch 资源id
        if (v.getId() == R.id.img_back) {
            onBackPressed();
        } else if (v.getId() == R.id.txt_preview) {
            //预览
            Intent intent = new Intent(this, ImagePreviewActivity.class);
            intent.putExtra(ImagePreviewActivity.EXTRA_IS_SELECTED, true);
            intent.putExtra(ImagePreviewActivity.EXTRA_DEFAULT_BUNDLE, mStateContainer.getDataWithBundle());
            SmartJump.from(this).startForResult(intent, new SmartJump.Callback() {
                @Override
                public void onActivityResult(int resultCode, Intent data) {
                    handlePreviewBack(resultCode, data);
                }
            });
        } else if (v.getId() == R.id.txt_confirm) {
            //选择
            ImageData imageData = new ImageData(mStateContainer.getImageList());
            if(spec.crop){
                File file = new File(PathUtils.getPath(mContext,imageData.getImage()));
                PhotoCapturer.from(ImageDisplayActivity.this).cropImage(file, new CropUriGetter() {
                    @Override
                    public void getCropUri(Uri uri) {
                        Intent intent = new Intent();
                        ImageData cropData = new ImageData(uri);
                        intent.putExtra(IMAGE_DATA, cropData);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }else {
                Intent intent = new Intent();
                intent.putExtra(IMAGE_DATA, imageData);
                setResult(RESULT_OK, intent);
                finish();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_STORAGE_REQUEST_CODE:
                if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPresenter.getAlbumList();
                } else {
                    IncapableCause cause = new IncapableCause(IncapableCause.TOAST, getString(R.string.permission_storage_denied));
                    IncapableCause.handleCause(this, cause);
                }
                break;
            case PERMISSION_CAMERA_REQUEST_CODE:
                if (grantResults.length >= 1 && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                    //如果用户赋予权限，则调用相机
                    takePhoto();
                } else {
                    Toast.makeText(this, getString(R.string.permission_camera_denied), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

}
