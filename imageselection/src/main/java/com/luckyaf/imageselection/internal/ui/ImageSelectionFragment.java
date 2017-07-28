package com.luckyaf.imageselection.internal.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luckyaf.imageselection.R;
import com.luckyaf.imageselection.internal.entity.Album;
import com.luckyaf.imageselection.internal.entity.Item;
import com.luckyaf.imageselection.internal.entity.SelectionSpec;
import com.luckyaf.imageselection.internal.model.AlbumImageCollection;
import com.luckyaf.imageselection.internal.model.SelectedItemCollection;
import com.luckyaf.imageselection.internal.ui.adapter.AlbumImageAdapter;
import com.luckyaf.imageselection.internal.ui.widget.ImageGridInset;


/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/7/19
 */
@SuppressWarnings("unused")
public class ImageSelectionFragment extends Fragment implements
        AlbumImageCollection.AlbumImageCallbacks, AlbumImageAdapter.CheckStateListener,
        AlbumImageAdapter.OnImageClickListener {

    public static final String EXTRA_ALBUM = "extra_album";

    private final AlbumImageCollection mAlbumImageCollection = new AlbumImageCollection();
    private RecyclerView mRecyclerView;
    private AlbumImageAdapter mAdapter;
    private SelectionProvider mSelectionProvider;
    private AlbumImageAdapter.CheckStateListener mCheckStateListener;
    private AlbumImageAdapter.OnImageClickListener mOnImageClickListener;

    public static ImageSelectionFragment newInstance(Album album) {
        ImageSelectionFragment fragment = new ImageSelectionFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ALBUM, album);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SelectionProvider) {
            mSelectionProvider = (SelectionProvider) context;
        } else {
            throw new IllegalStateException("Context must implement SelectionProvider.");
        }
        if (context instanceof AlbumImageAdapter.CheckStateListener) {
            mCheckStateListener = (AlbumImageAdapter.CheckStateListener) context;
        }
        if (context instanceof AlbumImageAdapter.OnImageClickListener) {
            mOnImageClickListener = (AlbumImageAdapter.OnImageClickListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_selection, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Album album = getArguments().getParcelable(EXTRA_ALBUM);

        mAdapter = new AlbumImageAdapter(getContext(),
                mSelectionProvider.provideSelectedItemCollection(), mRecyclerView);
        mAdapter.registerCheckStateListener(this);
        mAdapter.registerOnImageClickListener(this);
        mRecyclerView.setHasFixedSize(true);

        int spanCount;
        SelectionSpec selectionSpec = SelectionSpec.getInstance();
        spanCount = 3;
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

        int spacing = getResources().getDimensionPixelSize(R.dimen.image_grid_spacing);
        mRecyclerView.addItemDecoration(new ImageGridInset(spanCount, spacing, false));
        mRecyclerView.setAdapter(mAdapter);
        mAlbumImageCollection.onCreate(getActivity(), this);
        mAlbumImageCollection.load(album, selectionSpec.capture);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAlbumImageCollection.onDestroy();
    }

    public void refreshImageGrid() {
        mAdapter.notifyDataSetChanged();
    }

    public void refreshSelection() {
        mAdapter.refreshSelection();
    }

    @Override
    public void onAlbumImageLoad(Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onAlbumImageReset() {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onUpdate() {
        // notify outer Activity that check state changed
        if (mCheckStateListener != null) {
            mCheckStateListener.onUpdate();
        }
    }

    @Override
    public void onImageClick(Album album, Item item, int adapterPosition) {
        if (mOnImageClickListener != null) {
            mOnImageClickListener.onImageClick((Album) getArguments().getParcelable(EXTRA_ALBUM),
                    item, adapterPosition);
        }
    }

    public interface SelectionProvider {
        SelectedItemCollection provideSelectedItemCollection();
    }
}

