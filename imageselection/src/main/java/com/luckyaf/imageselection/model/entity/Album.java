package com.luckyaf.imageselection.model.entity;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import com.luckyaf.imageselection.R;
import com.luckyaf.imageselection.model.loader.AlbumLoader;

/**
 * 类描述：图片文件夹
 *
 * @author Created by luckyAF on 2017/7/19
 */
public class Album implements Parcelable {

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Nullable
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };


    public static final String ALBUM_ID_ALL = String.valueOf(-1);
    public static final String ALBUM_NAME_ALL = "All";

    /**
     * 文件夹id
     */
    private final String mId;
    /**
     * 封面图片路径
     */
    private final String mCoverPath;
    /**
     * 文件夹名
     */
    private final String mDisplayName;
    /**
     * 图片数量
     */
    private long mCount;

    private Album(String id, String coverPath, String albumName, long count) {
        mId = id;
        mCoverPath = coverPath;
        mDisplayName = albumName;
        mCount = count;
    }

    private Album(Parcel source) {
        mId = source.readString();
        mCoverPath = source.readString();
        mDisplayName = source.readString();
        mCount = source.readLong();
    }

    /**
     * Constructs a new {@link Album} entity from the {@link Cursor}.
     * This method is not responsible for managing cursor resource, such as close, iterate, and so on.
     */
    public static Album valueOf(Cursor cursor) {
        return new Album(
                cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID)),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)),
                cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)),
                cursor.getLong(cursor.getColumnIndex(AlbumLoader.COLUMN_COUNT)));
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mCoverPath);
        dest.writeString(mDisplayName);
        dest.writeLong(mCount);
    }

    public String getId() {
        return mId;
    }

    public String getCoverPath() {
        return mCoverPath;
    }

    public String getDisplayName(Context context) {
        if (isAll()) {
            return context.getString(R.string.album_name_all);
        }
        return mDisplayName;
    }


    public void addCaptureCount() {
        mCount++;
    }

    public long getCount() {
        return mCount;
    }

    public boolean isAll() {
        return ALBUM_ID_ALL.equals(mId);
    }

    public boolean isEmpty() {
        return mCount == 0;
    }

}
