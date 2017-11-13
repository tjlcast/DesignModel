package com.tjlcast.MonitorMode;

import apple.laf.JRSUIConstants;

/**
 * Created by tangjialiang on 2017/11/13.
 *
 * 从线程封闭原则及其推论可以得出Java监视器模式。
 * 遵循Java监视器模式的对象会把对象的所有可变状态
 * 都封装起来，并由对象自己的内置锁来保护。
 */

public final class Counter {
    private long value = 0 ;

    public synchronized long getValue() {
        return value ;
    }

    public synchronized long increment() {
        if (value == Long.MAX_VALUE) {
            throw  new IllegalArgumentException("count overflower") ;
        }

        return ++value ;
    }
}


class PrivateLock {
    private final Object myLock = new Object() ;
    JRSUIConstants.Widget widget ;

    void someMethod() {
        synchronized (myLock) {
            // 访问或修改Widge的状态
        }
    }

    // 使用私有的锁对象而不是对象的内置锁（或任何其他可通过公有方式访问的锁），
    // 有许多优点。
}