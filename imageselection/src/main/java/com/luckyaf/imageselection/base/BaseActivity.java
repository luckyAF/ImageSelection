package com.luckyaf.imageselection.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/11/14
 */
public abstract class BaseActivity<P extends IPresenter> extends AppCompatActivity implements IView{

    protected P mPresenter;
    protected Context mContext;


    /**
     * 获取布局文件
     * @return layoutId
     */
    public abstract int getLayoutId();

    /**
     * 初始化数据
     */
    public abstract void initData(Bundle savedInstanceState);

    /**
     * 提供presenter
     * @return presenter
     */
    public abstract P providePresenter();

    /**
     * 初始化view
     */
    public  abstract void initView();

    /**
     * 开始加载
     */
    public  abstract void start();




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initData(savedInstanceState);
        super.onCreate(savedInstanceState);
        doBeforeSetContentView();
        mContext=  this;
        setContentView(getLayoutId());
        mPresenter = providePresenter();
        mPresenter.attachView(this);
        initView();
        start();
    }


    public void doBeforeSetContentView(){

    }

    @Override
    public void showMessage(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy(){
        mPresenter.detachView();
        super.onDestroy();

    }


}
