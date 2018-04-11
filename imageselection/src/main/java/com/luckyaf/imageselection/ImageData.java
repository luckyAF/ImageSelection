package com.luckyaf.imageselection;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/7/27
 */
@SuppressWarnings("unused")
public class ImageData {
    private ArrayList<Uri> mImages = new ArrayList<>();
    public ImageData(List<Uri> uris){
        mImages.addAll(uris);
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
}
