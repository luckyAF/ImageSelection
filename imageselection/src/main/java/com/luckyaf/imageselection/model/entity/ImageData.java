package com.luckyaf.imageselection.model.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/7/27
 */
@SuppressWarnings("unused")
public class ImageData implements Parcelable {
    private ArrayList<Uri> mImages = new ArrayList<>();

    public ImageData(List<Uri> uris){
        mImages.addAll(uris);
    }

    public ImageData(Uri uri){
        mImages.add(uri);
    }

    protected ImageData(Parcel in) {
        mImages = in.createTypedArrayList(Uri.CREATOR);
    }



    public int size(){
        return mImages.size();
    }

    public boolean isEmpty(){
        return mImages.isEmpty();
    }

    public Uri getImage(){
        return mImages.get(0);
    }

    public List<Uri> getImages(){
        return mImages;
    }

    public Uri getImageAt(int position){
        if(mImages.size() > position && position >= 0){
            return mImages.get(position);
        }else{
            return null;
        }
    }

    @Override
    public String toString(){
        StringBuilder imageData = new StringBuilder();
        for (Uri uri :mImages){
            imageData.append(uri).append("\n");
        }
        return imageData.toString();
    }

    public static final Creator<ImageData> CREATOR = new Creator<ImageData>() {
        @Override
        public ImageData createFromParcel(Parcel in) {
            return new ImageData(in);
        }

        @Override
        public ImageData[] newArray(int size) {
            return new ImageData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mImages);
    }
}
