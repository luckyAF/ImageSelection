package com.luckyaf.imageselection.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.SparseArray;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/9/19
 */
@SuppressWarnings("unused")
public class SmartJump {


    private static final String TAG = SmartJump.class.getSimpleName();

    private SupportResultBridgeFragment supportResultBridgeFragment;


    public static SmartJump from(@NonNull FragmentActivity activity){
        return new SmartJump(activity.getSupportFragmentManager());
    }

    public static SmartJump from(@NonNull Fragment fragment){
        return new SmartJump(fragment.getChildFragmentManager());
    }

    public static SmartJump with(@NonNull FragmentManager fragmentManager){
        return new SmartJump(fragmentManager);
    }


    private SmartJump(FragmentManager fragmentManager){
        supportResultBridgeFragment = getSupportResultBridgeFragment(fragmentManager);
        Log.d(TAG,"supportResultBridgeFragment" + supportResultBridgeFragment.getTag());

    }


    public void startForResult(Intent intent, Callback callback) {
        supportResultBridgeFragment.startForResult(intent, callback);

    }
    public void startForResult(Class<?> clazz, Callback callback) {
        Intent intent =new Intent(supportResultBridgeFragment.getActivity(), clazz);
        startForResult(intent, callback);
    }

    public interface Callback {
        /**
         * 回调
         * @param resultCode  code
         * @param data   data
         */
        void onActivityResult(int resultCode, Intent data);
    }




    private SupportResultBridgeFragment getSupportResultBridgeFragment(@NonNull final android.support.v4.app.FragmentManager fragmentManager) {
        SupportResultBridgeFragment bridgeFragment = findFragment(fragmentManager);
        boolean isNewInstance = bridgeFragment == null;
        if (isNewInstance) {
            bridgeFragment = new SupportResultBridgeFragment();
            fragmentManager
                    .beginTransaction()
                    .add(bridgeFragment, TAG)
                    .commitNow();
        }
        return bridgeFragment;
    }


    private SupportResultBridgeFragment findFragment(@NonNull final android.support.v4.app.FragmentManager fragmentManager) {
        return (SupportResultBridgeFragment) fragmentManager.findFragmentByTag(TAG);
    }




    public static class SupportResultBridgeFragment extends android.support.v4.app.Fragment {
        private SparseArray<Callback> mCallbacks = new SparseArray<>();
        /**
         * 每次启动都会有个不同的requestCode
         */
        private int uniqueCode = 1;

        public SupportResultBridgeFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }


        /**
         * 防止 同时多个activity启动 造成request相同
         * @param intent intent
         * @param callback 回调
         */
        public synchronized void startForResult(Intent intent, SmartJump.Callback callback) {
            uniqueCode ++;
            mCallbacks.put(uniqueCode, callback);
            startActivityForResult(intent, uniqueCode);
            // 保证requestCode 每个都不同
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            SmartJump.Callback callback = mCallbacks.get(requestCode);
            if (callback != null) {
                callback.onActivityResult(resultCode, data);
            }
            mCallbacks.remove(requestCode);
        }
    }

}
