package com.luckyaf.imageselection;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;

import com.luckyaf.imageselection.imageLoader.ImageLoader;
import com.luckyaf.imageselection.internal.entity.SelectionSpec;
import com.luckyaf.imageselection.internal.model.NoImageBack;
import com.luckyaf.imageselection.internal.utils.RxBus;
import com.luckyaf.imageselection.ui.ImageSelectionActivity;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/7/19
 */
@SuppressWarnings("unused")
public class SelectionCreator {

    public  interface ImageGetter {
        void getImageSuccess(ImageData imageData);
    }


    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;
    private final SelectionSpec mSelectionSpec;
    private Subscription mSubscription;
    private Subscription mNoBackSubscription;

    public SelectionCreator(Activity activity) {
        this(activity, null);
    }

    public SelectionCreator(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private SelectionCreator(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
        mSelectionSpec = SelectionSpec.getCleanInstance();
    }

    public SelectionCreator needGif(boolean needGif){
        mSelectionSpec.needGif = needGif;
        return this;
    }

    public SelectionCreator maxSelectable(int maxSelectable){
        mSelectionSpec.maxSelectable = maxSelectable;
        return this;
    }


    public SelectionCreator capture(boolean capture){
        mSelectionSpec.capture = capture;
        return this;
    }

    public SelectionCreator themeColor(@ColorInt int themeColor){
        mSelectionSpec.themeColor = themeColor;
        return this;
    }

    public SelectionCreator translucent(boolean translucent){
        mSelectionSpec.translucent = translucent;
        return this;
    }

    public SelectionCreator titleWord(String titleWord){
        mSelectionSpec.titleWord = titleWord;
        return this;
    }

    public SelectionCreator selectWord(String selectWord){
        mSelectionSpec.selectWord = selectWord;
        return this;
    }

    public SelectionCreator savePublic(boolean savePublic){
        mSelectionSpec.savePublic = savePublic;
        return this;
    }

    public SelectionCreator editable(boolean editable){
        mSelectionSpec.editable = editable;
        return this;
    }

    public SelectionCreator setImageLoader(ImageLoader imageLoader){
        mSelectionSpec.imageLoader = imageLoader;
        return this;
    }

    public SelectionCreator getImage(final ImageGetter imageGetter){
        this.mSubscription = RxBus.getInstance()
                .toObservable(ImageData.class)
                .subscribe(new Action1<ImageData>() {
                    @Override
                    public void call(ImageData imageData) {
                        imageGetter.getImageSuccess(imageData);
                        clear();
                    }
                });
        this.mNoBackSubscription = RxBus.getInstance()
                .toObservable(NoImageBack.class)
                .subscribe(new Action1<NoImageBack>() {
                    @Override
                    public void call(NoImageBack back) {
                       clear();
                    }
                });
        return this;
    }

    public void start(){
        Activity activity = mContext.get();
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, ImageSelectionActivity.class);
        Fragment fragment = mFragment != null ? mFragment.get() : null;
        if(fragment != null){
            fragment.startActivity(intent);
        }else{
            activity.startActivity(intent);
        }

    }

    private void clear() {
        if (this.mSubscription != null) {
            if (!this.mSubscription.isUnsubscribed()) {
                this.mSubscription.unsubscribe();
            }
            this.mSubscription = null;
        }
        if (this.mNoBackSubscription != null) {
            if (!this.mNoBackSubscription.isUnsubscribed()) {
                this.mNoBackSubscription.unsubscribe();
            }
            this.mNoBackSubscription = null;
        }
    }


}
