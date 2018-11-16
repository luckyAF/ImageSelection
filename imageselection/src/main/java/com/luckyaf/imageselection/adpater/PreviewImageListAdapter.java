package com.luckyaf.imageselection.adpater;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.luckyaf.imageselection.R;
import com.luckyaf.imageselection.model.entity.SelectionSpec;
import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：预览页面下方选择的图片列表adapter
 *
 * @author Created by luckyAF on 2017/7/25
 */

public class PreviewImageListAdapter extends
        RecyclerView.Adapter<PreviewImageListAdapter.PreviewItemViewHolder>  {

    private ArrayList<Uri> mSelectedUris;
    private ArrayList<Uri> mUnSelectedUris;
    private final Drawable mPlaceholder;
    private OnImageClickListener mClickListener;
    /**
     *  选中后取消的图片也要显示
     */
    private boolean isSelectedPreview;

    private Context mContext;

    public PreviewImageListAdapter(Context context){
        mContext = context;
        mPlaceholder = ContextCompat.getDrawable(context, R.drawable.ic_default_image);
        isSelectedPreview = false;
        mSelectedUris = new ArrayList<>();
        mUnSelectedUris = new ArrayList<>();
    }


    public void setOnImageClickListener(OnImageClickListener listener){
        mClickListener = listener;
    }

    public void isSelectedPreview(boolean is){
        isSelectedPreview = is;
    }

    public void addAll(List<Uri> uris) {
        mSelectedUris.addAll(uris);
        notifyDataSetChanged();
    }
    public void add(Uri uri){
        if(isSelectedPreview && mUnSelectedUris.contains(uri)){
            mUnSelectedUris.remove(uri);
        }else{
            mSelectedUris.add(uri);
        }
        notifyDataSetChanged();
    }

    public void replace(Uri uri){
        mUnSelectedUris.clear();
        mUnSelectedUris.addAll(mSelectedUris);
        if(mUnSelectedUris.contains(uri)){
            mUnSelectedUris.remove(uri);
        }
        if(!mSelectedUris.contains(uri)){
            mSelectedUris.add(uri);
        }
        notifyDataSetChanged();
    }

    public void remove(Uri uri){
        if(isSelectedPreview && mSelectedUris.contains(uri)){
            mUnSelectedUris.add(uri);
        }else{
            mSelectedUris.remove(uri);
        }
        notifyDataSetChanged();
    }


    @Override
    public PreviewItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_preview_grid_item, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int)v.getTag();
                mClickListener.onImageClick(mSelectedUris.get(position),position);
            }
        });
        return new PreviewItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PreviewItemViewHolder holder, int position) {
        final Uri uri = mSelectedUris.get(position);

        if(isSelectedPreview && mUnSelectedUris.contains(uri)){
            holder.mUnSelectView.setVisibility(View.VISIBLE);
        }else{
            holder.mUnSelectView.setVisibility(View.GONE);
        }
        holder.layoutItem.setTag(position);
        SelectionSpec.getInstance().imageLoader.loadThumbnail(mContext, mContext.getResources().getDimensionPixelSize(R
                        .dimen.image_grid_size), mPlaceholder,
                holder.mImageView, uri);

    }



    @Override
    public int getItemCount() {
        return mSelectedUris.size();
    }




    public interface OnImageClickListener {
        void onImageClick(Uri uri, int adapterPosition);
    }

    class PreviewItemViewHolder extends RecyclerView.ViewHolder{

        private ImageView mImageView;
        private View mUnSelectView;
        private RelativeLayout layoutItem;

         PreviewItemViewHolder(View itemView) {
            super(itemView);
            layoutItem = (RelativeLayout)itemView;
            mImageView = (ImageView) itemView.findViewById(R.id.img_selected);
            mUnSelectView = itemView.findViewById(R.id.view_unSelect);
        }
    }
}
