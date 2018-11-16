package com.luckyaf.imageselection.fragment;

import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luckyaf.imageselection.R;
import com.luckyaf.imageselection.model.entity.SelectionSpec;
import com.luckyaf.imageselection.utils.PhotoMetadataUtils;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * 类描述：图片预览fragment
 *
 * @author Created by luckyAF on 2017/7/20
 */

public class PreviewItemFragment  extends Fragment {

    private static final String ARGS_URI = "args_uri";

    public static PreviewItemFragment newInstance(Uri uri) {
        PreviewItemFragment fragment = new PreviewItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_URI, uri);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview_item, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Uri uri = getArguments().getParcelable(ARGS_URI);
        if (uri == null) {
            return;
        }

        ImageViewTouch image = (ImageViewTouch)view.findViewById(R.id.image_view);
        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        Point size = PhotoMetadataUtils.getBitmapSize(uri, getActivity());
        SelectionSpec.getInstance().imageLoader.loadImage(getContext(), size.x, size.y, image, uri);

    }

    public void resetView() {
        if (getView() != null) {
            ((ImageViewTouch) getView().findViewById(R.id.image_view)).resetMatrix();
        }
    }
}
