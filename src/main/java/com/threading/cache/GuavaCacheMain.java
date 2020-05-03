package com.threading.cache;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class GuavaCacheMain {

    public static void main(String[] args)
    {CacheLoader<String, String> loader;

        LoadingCache<String, String> graphs = CacheBuilder.newBuilder()
                .maximumSize(3)
                .build(
                        new CacheLoader<String, String>() {

                            public String load(String key) {
                                return key.toUpperCase();
                            }
                        });

        System.out.println(graphs.getUnchecked("cd"));
        System.out.println(graphs.size());
        System.out.println(graphs.getUnchecked("cde"));
        System.out.println(graphs.size());
    }
}
