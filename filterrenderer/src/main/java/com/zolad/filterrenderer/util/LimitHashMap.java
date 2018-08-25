package com.zolad.filterrenderer.util;

import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class LimitHashMap<K, V> {

    private int DEFAULT_SIZE = 8;
    private int limitSize = DEFAULT_SIZE;
    private LRUCache<K, V> mMap;


    public LimitHashMap() {

        mMap = new LRUCache<K, V>(DEFAULT_SIZE);


    }


    public LimitHashMap(int limitSize) {

        this.limitSize = limitSize;
        mMap = new LRUCache<K, V>(this.limitSize);

    }

    public synchronized V get(K key) {

        return mMap.get(key);
    }

    public synchronized V put(K key, V value) {




        return mMap.put(key, value);
    }



}