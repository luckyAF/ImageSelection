package com.luckyaf.imageselction_master;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.squareup.leakcanary.LeakCanary;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/7/21
 */

public class App extends Application {
    private static final String TAG = "LogTest";
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(mCallbacks);


    }

    private ActivityLifecycleCallbacks mCallbacks = new ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            LogUtils.d(TAG, "onActivityCreated() called with: activity = [" + activity + "], savedInstanceState = [" + savedInstanceState + "]");
        }

        @Override
        public void onActivityStarted(Activity activity) {
            LogUtils.d(TAG, "onActivityStarted() called with: activity = [" + activity + "]");
        }

        @Override
        public void onActivityResumed(Activity activity) {
            LogUtils.d(TAG, "onActivityResumed() called with: activity = [" + activity + "]");
        }

        @Override
        public void onActivityPaused(Activity activity) {
            LogUtils.d(TAG, "onActivityPaused() called with: activity = [" + activity + "]");
        }

        @Override
        public void onActivityStopped(Activity activity) {
            LogUtils.d(TAG, "onActivityStopped() called with: activity = [" + activity + "]");
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            LogUtils.d(TAG, "onActivitySaveInstanceState() called with: activity = [" + activity + "], outState = [" + outState + "]");
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            LogUtils.d(TAG, "onActivityDestroyed() called with: activity = [" + activity + "]");
        }
    };

}