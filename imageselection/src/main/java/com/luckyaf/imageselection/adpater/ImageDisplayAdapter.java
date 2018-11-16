package com.luckyaf.imageselection.adpater;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luckyaf.imageselection.R;
import com.luckyaf.imageselection.widget.CheckView;
import com.luckyaf.imageselection.widget.ImageGrid;
import com.luckyaf.imageselection.model.StateContainer;
import com.luckyaf.imageselection.model.entity.Album;
import com.luckyaf.imageselection.model.entity.IncapableCause;
import com.luckyaf.imageselection.model.entity.Item;

/**
 * 类描述：图片列表
 *
 * @author Created by luckyAF on 2018/11/15
 */

public class ImageDisplayAdapter extends
        RecyclerViewCursorAdapter<RecyclerView.ViewHolder> implements
        ImageGrid.OnImageGridClickListener {

    private static final int VIEW_TYPE_CAPTURE = 0x01;
    private static final int VIEW_TYPE_MEDIA = 0x02;
    private StateContainer mStateContainer;
    private final Drawable mPlaceholder;
    private CheckStateListener mCheckStateListener;
    private OnImageClickListener mOnImageClickListener;
    private OnCameraClickListener mOnCameraClickListener;
    private RecyclerView mRecyclerView;
    private int mImageResize;

    public ImageDisplayAdapter(Context context, StateContainer stateContainer, RecyclerView recyclerView) {
        super(null);
        mStateContainer = stateContainer;
        TypedArray ta = context.getTheme().obtainStyledAttributes(new int[]{R.attr.item_placeholder});
        mPlaceholder = ta.getDrawable(0);
        ta.recycle();
        mRecyclerView = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CAPTURE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_capture_item, parent, false);
            CaptureViewHolder holder = new CaptureViewHolder(v);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnCameraClickListener != null) {
                        mOnCameraClickListener.capture();
                    }
                }
            });
            return holder;
        } else if (viewType == VIEW_TYPE_MEDIA) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_grid_item, parent, false);
            return new ImageViewHolder(v);
        }
        return null;
    }

    @Override
    protected void onBindViewHolder(final RecyclerView.ViewHolder holder, Cursor cursor) {
        if (holder instanceof CaptureViewHolder) {
            CaptureViewHolder captureViewHolder = (CaptureViewHolder) holder;
            Drawable[] drawables = captureViewHolder.mHint.getCompoundDrawables();
        } else if (holder instanceof ImageViewHolder) {
            ImageViewHolder mediaViewHolder = (ImageViewHolder) holder;
            final Item item = Item.valueOf(cursor);
            mediaViewHolder.mImageGrid.preBindImage(new ImageGrid.PreBindInfo(
                    getImageResize(mediaViewHolder.mImageGrid.getContext()),
                    mPlaceholder,
                    false,
                    holder
            ));
            mediaViewHolder.mImageGrid.bindImage(item);
            mediaViewHolder.mImageGrid.setOnImageGridClickListener(this);
            setCheckStatus(item, mediaViewHolder.mImageGrid);
        }
    }

    /**
     * 显示图片上的选择状态
     *
     * @param item      item
     * @param mediaGrid iamge
     */
    private void setCheckStatus(Item item, ImageGrid mediaGrid) {
        boolean selected = mStateContainer.isSelected(item.getContentUri());
        mediaGrid.setChecked(selected);
    }

    public void updateState(StateContainer stateContainer){
        mStateContainer = stateContainer;
        notifyCheckStateChanged();
    }

    @Override
    public void onThumbnailClicked(ImageView thumbnail, Item item, RecyclerView.ViewHolder holder) {
        if (mOnImageClickListener != null) {
            mOnImageClickListener.onImageClick(item, holder.getAdapterPosition());
        }
    }

    @Override
    public void onCheckViewClicked(CheckView checkView, Item item, RecyclerView.ViewHolder holder) {
        if (mStateContainer.isSelected(item.getContentUri())) {
            mStateContainer.removeImage(item.getContentUri());

        } else {
            if (mStateContainer.sigleSelection()){
                mStateContainer.addImage(item.getContentUri());

            }else if(assertAddSelection(holder.itemView.getContext())) {
                mStateContainer.addImage(item.getContentUri());
            }
        }
        notifyCheckStateChanged();
    }

    /**
     * 更新选中状态
     */
    private void notifyCheckStateChanged() {
        notifyDataSetChanged();
        if (mCheckStateListener != null) {
            mCheckStateListener.onUpdate();
        }
    }

    @Override
    public int getItemViewType(int position, Cursor cursor) {
        return Item.valueOf(cursor).isCapture() ? VIEW_TYPE_CAPTURE : VIEW_TYPE_MEDIA;
    }

    private boolean assertAddSelection(Context context) {
        IncapableCause cause = mStateContainer.isAcceptable();
        IncapableCause.handleCause(context, cause);
        return cause == null;
    }

    public void registerCheckStateListener(CheckStateListener listener) {
        mCheckStateListener = listener;
    }

    public void unregisterCheckStateListener() {
        mCheckStateListener = null;
    }

    public void registerOnImageClickListener(OnImageClickListener listener) {
        mOnImageClickListener = listener;
    }

    public void unregisterOnImageClickListener() {
        mOnImageClickListener = null;
    }


    public void registerOnCameraClickListener(OnCameraClickListener listener) {
        mOnCameraClickListener = listener;
    }


    public void refreshSelection() {
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        if (first == -1 || last == -1) {
            return;
        }
        Cursor cursor = getCursor();
        for (int i = first; i <= last; i++) {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(first);
            if (holder instanceof ImageViewHolder) {
                if (cursor.moveToPosition(i)) {
                    setCheckStatus(Item.valueOf(cursor), ((ImageViewHolder) holder).mImageGrid);
                }
            }
        }
    }

    private int getImageResize(Context context) {
        if (mImageResize == 0) {
            RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
            int spanCount = ((GridLayoutManager) lm).getSpanCount();
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int availableWidth = screenWidth - context.getResources().getDimensionPixelSize(
                    R.dimen.image_grid_spacing) * (spanCount - 1);
            mImageResize = availableWidth / spanCount;
            mImageResize = (int) (mImageResize * 0.5f);
        }
        return mImageResize;
    }


    public interface CheckStateListener {
        void onUpdate();
    }

    /**
     * 图片点击
     */
    public interface OnImageClickListener {
        /**
         * 点击图片
         *
         * @param item            图片item
         * @param adapterPosition 位置
         */
        void onImageClick( Item item, int adapterPosition);
    }

    public interface OnCameraClickListener {
        /**
         * 拍照
         */
        void capture();
    }


    /**
     * 普通图片holder
     */
    private static class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageGrid mImageGrid;

        ImageViewHolder(View itemView) {
            super(itemView);
            mImageGrid = (ImageGrid) itemView;
        }
    }

    /**
     * 相机holder
     */
    private static class CaptureViewHolder extends RecyclerView.ViewHolder {
        private TextView mHint;

        CaptureViewHolder(View itemView) {
            super(itemView);
            mHint = itemView.findViewById(R.id.hint);
        }
    }

}
