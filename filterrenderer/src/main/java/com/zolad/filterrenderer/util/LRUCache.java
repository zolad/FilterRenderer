package com.zolad.filterrenderer.util;

import android.graphics.SurfaceTexture;

import com.zolad.filterrenderer.FilterRenderer;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V>  extends LinkedHashMap<K, V>
{

    private RemoveObjectListener<V> mRemoveListener;

    public LRUCache(int maxSize)
    {
        super(maxSize, 0.75F, true);
        maxElements = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest) {

        boolean isRemove = false;

        if(size() > maxElements){

            isRemove = true;
            if(mRemoveListener!=null)
            {
                mRemoveListener.onRemove(eldest.getValue());
            }

        }


        return isRemove;
    }


    private static final long serialVersionUID = 1L;
    protected int maxElements;


    public interface RemoveObjectListener<V> {

        public void onRemove(V eldest);

    }
}
