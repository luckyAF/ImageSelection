package com.luckyaf.imageselection.internal.utils;


import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * 类描述：  事件总线
 *
 * @author Created by luckyAF on 2017/7/19
 */

public class RxBus {
    private static volatile RxBus instance;

    private final Subject<Object,Object> bus;
    // PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
    private RxBus(){
        bus = new SerializedSubject<>(PublishSubject.create());
    }

    public static RxBus getInstance(){
        if(instance == null){
            synchronized (RxBus.class){
                if(instance == null){
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }
    // 发送一个新的事件
    public void post(Object o){
        bus.onNext(o);
    }

    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
    public <T> Observable<T> toObservable (Class<T> eventType) {
        return bus.ofType(eventType);
    }

}
