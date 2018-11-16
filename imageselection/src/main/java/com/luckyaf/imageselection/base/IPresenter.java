package com.luckyaf.imageselection.base;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/11/14
 */
public interface IPresenter<V extends IView> {

    void attachView(V view);
    void detachView();
}
