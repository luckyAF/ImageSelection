package com.luckyaf.imageselection.internal.utils;

import android.content.Context;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/10/19
 */
public class ProviderUtil {
    public static String getFileProviderName(Context context){
        return context.getPackageName()+".ImageProvider";
    }
}
