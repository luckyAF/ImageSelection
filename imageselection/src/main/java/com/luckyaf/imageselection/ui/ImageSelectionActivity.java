package com.luckyaf.imageselection.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.luckyaf.imageselection.ImageData;
import com.luckyaf.imageselection.internal.entity.IncapableCause;
import com.luckyaf.imageselection.internal.entity.SelectionSpec;
import com.luckyaf.imageselection.internal.model.AlbumCollection;
import com.luckyaf.imageselection.internal.model.NoImageBack;
import com.luckyaf.imageselection.internal.ui.BasePreviewActivity;
import com.luckyaf.imageselection.internal.ui.ImagePreviewActivity;
import com.luckyaf.imageselection.internal.ui.SelectedPreviewActivity;
import com.luckyaf.imageselection.internal.ui.adapter.AlbumsAdapter;
import com.luckyaf.imageselection.internal.ui.widget.AlbumsSpinner;
import com.luckyaf.imageselection.internal.utils.BarUtils;
import com.luckyaf.imageselection.internal.utils.PhotoHelper;
import com.luckyaf.imageselection.R;
import com.luckyaf.imageselection.internal.entity.Album;
import com.luckyaf.imageselection.internal.entity.Item;
import com.luckyaf.imageselection.internal.model.SelectedItemCollection;
import com.luckyaf.imageselection.internal.ui.ImageSelectionFragment;
import com.luckyaf.imageselection.internal.ui.adapter.AlbumImageAdapter;

import java.util.ArrayList;

public class ImageSelectionActivity extends AppCompatActivity implements
        AlbumCollection.AlbumCallbacks, AdapterView.OnItemSelectedListener,
        ImageSelectionFragment.SelectionProvider, View.OnClickListener,
        AlbumImageAdapter.CheckStateListener, AlbumImageAdapter.OnImageClickListener,
        AlbumImageAdapter.OnPhotoCapture {

    private static final int REQUEST_CODE_PREVIEW = 23;
    private static final int REQUEST_CODE_CAPTURE = 24;
    private static final int STORAGE_REQUEST_CODE = 25;
    private static final int CAMERA_REQUEST_CODE = 26;

    public static final String IMAGE_DATA = "image_data";

    private AlbumCollection mAlbumCollection = new AlbumCollection();
    private SelectedItemCollection mSelectedItemCollection = new SelectedItemCollection(this);
    private PhotoHelper mPhotoHelper;

    private SelectionSpec spec;

    private AlbumsSpinner mAlbumsSpinner;
    private AlbumsAdapter mAlbumsAdapter;

    private TextView mButtonConfirm;
    private TextView mTxtPreview;

    private String selectWord;
    private String previewWord;
    private int maxSelectable;

    private View mContainer;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        spec = SelectionSpec.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        if(spec.translucent){
            BarUtils.setColor(this,spec.themeColor);
        }
        setContentView(R.layout.activity_image_selection);

        if(spec.capture){//如果要拍照 初始化拍照
            mPhotoHelper = new PhotoHelper(this);
            mPhotoHelper.setSavePublic(spec.savePublic);
        }

        selectWord = spec.selectWord;
        maxSelectable = spec.maxSelectable;
        previewWord = getString(R.string.button_preview);
        ImageView imgBack = (ImageView)findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        mButtonConfirm = (TextView) findViewById(R.id.txt_confirm);
        mTxtPreview = (TextView)findViewById(R.id.txt_preview);
        mButtonConfirm.setOnClickListener(this);
        mButtonConfirm.setText(selectWord);
        mTxtPreview.setOnClickListener(this);
        mContainer = findViewById(R.id.container);
        mEmptyView = findViewById(R.id.empty_view);
        findViewById(R.id.toolbar).setBackgroundColor(spec.themeColor);
        findViewById(R.id.bottom_toolbar).setBackgroundColor(spec.themeColor);
        // Android 6.0 checkSelfPermission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_REQUEST_CODE);
        }else {
            initAlbum(savedInstanceState);
        }
    }

    public void initAlbum(Bundle savedInstanceState){
        mSelectedItemCollection.onCreate(savedInstanceState, spec);
        updateBottomToolbar();
        mAlbumsAdapter = new AlbumsAdapter(this, null, false);
        mAlbumsSpinner = new AlbumsSpinner(this);
        mAlbumsSpinner.setOnItemSelectedListener(this);
        mAlbumsSpinner.setSelectedTextView((TextView) findViewById(R.id.txt_selected_album));
        mAlbumsSpinner.setPopupAnchorView(findViewById(R.id.txt_selected_album));
        mAlbumsSpinner.setAdapter(mAlbumsAdapter);
        mAlbumCollection.onCreate(this, this);
        mAlbumCollection.onRestoreInstanceState(savedInstanceState);
        mAlbumCollection.loadAlbums();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_PREVIEW) {
            Log.d("onActivityResult","requestCode   REQUEST_CODE_PREVIEW");
            Bundle resultBundle = data.getBundleExtra(BasePreviewActivity.EXTRA_RESULT_BUNDLE);
            ArrayList<Uri> selected = resultBundle.getParcelableArrayList(SelectedItemCollection.STATE_SELECTION);
            if (data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_APPLY, false)) {

                ImageData imageData = new ImageData(selected);

                Intent intent = new Intent();
                intent.putExtra(IMAGE_DATA,imageData);
                setResult(RESULT_OK,intent);
                finish();
            } else {
                mSelectedItemCollection.overwrite(selected);
                Fragment mediaSelectionFragment = getSupportFragmentManager().findFragmentByTag(
                        ImageSelectionFragment.class.getSimpleName());
                if (mediaSelectionFragment instanceof ImageSelectionFragment) {
                    ((ImageSelectionFragment) mediaSelectionFragment).refreshImageGrid();
                }
                updateBottomToolbar();
            }
        } else if (requestCode == REQUEST_CODE_CAPTURE) {
            Log.d("onActivityResult","requestCode   REQUEST_CODE_CAPTURE");
            // Just pass the data back to previous calling Activity.
            Uri contentUri = mPhotoHelper.getCurrentPhotoUri();
            mSelectedItemCollection.add(contentUri);
            Intent intent = new Intent(this, SelectedPreviewActivity.class);
            intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedItemCollection.getDataWithBundle());
            startActivityForResult(intent, REQUEST_CODE_PREVIEW);
        }
    }

    private void updateBottomToolbar() {
        int selectedCount = mSelectedItemCollection.count();
        if (selectedCount == 0) {
            mTxtPreview.setEnabled(false);
            mButtonConfirm.setEnabled(false);
            mTxtPreview.setText(previewWord);
            mButtonConfirm.setText(selectWord);
        } else {
            mTxtPreview.setEnabled(true);
            mButtonConfirm.setEnabled(true);
            mTxtPreview.setText(previewWord+"("+selectedCount+")");
            mButtonConfirm.setText(selectWord+"("+selectedCount+"/" + maxSelectable +")");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAlbumCollection.onDestroy();
    }

    @Override
    public void onClick(View v) {
        //library 里面不能 switch 资源id
        if(v.getId() == R.id.img_back){
            onBackPressed();
        }else if(v.getId() == R.id.txt_preview){
            //预览
            Intent intent = new Intent(this, SelectedPreviewActivity.class);
            intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedItemCollection.getDataWithBundle());
            startActivityForResult(intent, REQUEST_CODE_PREVIEW);

        }else if(v.getId() == R.id.txt_confirm){
            //选择
            ImageData imageData = new ImageData(mSelectedItemCollection.asList());
            Intent intent = new Intent();
            intent.putExtra(IMAGE_DATA,imageData);
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {

        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mAlbumCollection.setStateCurrentSelection(position);
        mAlbumsAdapter.getCursor().moveToPosition(position);
        Album album = Album.valueOf(mAlbumsAdapter.getCursor());
        if (album.isAll() && SelectionSpec.getInstance().capture) {
            album.addCaptureCount();
        }
        onAlbumSelected(album);
    }

    /**
     * 显示某个文件夹
     */
    private void onAlbumSelected(Album album) {
        if (album.isAll() && album.isEmpty()) {
            mContainer.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mContainer.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            Fragment fragment = ImageSelectionFragment.newInstance(album);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment, ImageSelectionFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public SelectedItemCollection provideSelectedItemCollection() {
        return mSelectedItemCollection;

    }

    @Override
    public void onUpdate() {
        // notify bottom toolbar that check state changed.
        updateBottomToolbar();
    }

    @Override
    public void onImageClick(Album album, Item item, int adapterPosition) {
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        intent.putExtra(ImagePreviewActivity.EXTRA_ALBUM, album);
        intent.putExtra(ImagePreviewActivity.EXTRA_ITEM, item);
        intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedItemCollection.getDataWithBundle());
        startActivityForResult(intent, REQUEST_CODE_PREVIEW);
    }

    @Override
    public void capture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
            Toast.makeText(this, getString(R.string.permission_camera_denied), Toast.LENGTH_SHORT).show();
        }else {
            if (mPhotoHelper != null) {
                mPhotoHelper.dispatchCaptureIntent(this, REQUEST_CODE_CAPTURE);
            }
        }
    }

    @Override
    public void onAlbumLoad(final Cursor cursor) {
        mAlbumsAdapter.swapCursor(cursor);
        // select default album.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                cursor.moveToPosition(mAlbumCollection.getCurrentSelection());
                mAlbumsSpinner.setSelection(ImageSelectionActivity.this,
                        mAlbumCollection.getCurrentSelection());
                Album album = Album.valueOf(cursor);
                if (album.isAll() && SelectionSpec.getInstance().capture) {
                    album.addCaptureCount();
                }
                onAlbumSelected(album);
            }
        });
    }

    @Override
    public void onAlbumReset() {
        mAlbumsAdapter.swapCursor(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        spec.onSaveInstanceState(outState);
        mAlbumCollection.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_REQUEST_CODE:
                if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAlbum(getIntent().getExtras());
                } else {
                    IncapableCause cause = new IncapableCause(IncapableCause.TOAST, getString(R.string.permission_storage_denied));
                    IncapableCause.handleCause(this, cause);
                }
                break;
            case CAMERA_REQUEST_CODE:
                if (grantResults.length >= 1 && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                    //如果用户赋予权限，则调用相机
                    if (mPhotoHelper != null) {
                        mPhotoHelper.dispatchCaptureIntent(this, REQUEST_CODE_CAPTURE);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.permission_camera_denied), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
