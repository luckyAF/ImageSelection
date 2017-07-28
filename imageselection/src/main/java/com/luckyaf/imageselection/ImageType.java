package com.luckyaf.imageselection;

import android.content.ContentResolver;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.luckyaf.imageselection.internal.utils.PhotoMetadataUtils;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * 类描述：图片类型
 *
 * @author Created by luckyAF on 2017/7/19
 */

public enum ImageType {
    // ============== images ==============
    JPEG("image/jpeg", new HashSet<String>() {
        {
            add("jpg");
            add("jpeg");
        }
    }),
    PNG("image/png", new HashSet<String>() {
        {
            add("png");
        }
    }),
    GIF("image/gif", new HashSet<String>() {
        {
            add("gif");
        }
    }),
    BMP("image/x-ms-bmp", new HashSet<String>() {
        {
            add("bmp");
        }
    }),
    WEBP("image/webp", new HashSet<String>() {
        {
            add("webp");
        }
    });

    private final String mMimeTypeName;
    private final Set<String> mExtensions;


    ImageType(String mimeTypeName, Set<String> extensions) {
        mMimeTypeName = mimeTypeName;
        mExtensions = extensions;
    }

    public static Set<ImageType> of(ImageType type, ImageType... rest) {
        return EnumSet.of(type, rest);
    }

    public static Set<ImageType> ofAll() {
        return EnumSet.of(JPEG, PNG, GIF, BMP, WEBP);
    }

    public static Set<ImageType> ofWithoutGif(){return EnumSet.of(JPEG, PNG, BMP, WEBP);}

    @Override
    public String toString() {
        return mMimeTypeName;
    }

    public boolean checkType(ContentResolver resolver, Uri uri) {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        if (uri == null) {
            return false;
        }
        String type = map.getExtensionFromMimeType(resolver.getType(uri));
        for (String extension : mExtensions) {
            if (extension.equals(type)) {
                return true;
            }
            String path = PhotoMetadataUtils.getPath(resolver, uri);
            if (path != null && path.toLowerCase(Locale.US).endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
