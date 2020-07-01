package com.example.auth.singleton;

import com.google.gson.Gson;

import java.io.Serializable;


public class GsonSingleton implements Serializable {
    private final Gson gson;

    private GsonSingleton() {
        gson = new Gson();
    }

    public static class Holder {
        private final static GsonSingleton INSTANCE = new GsonSingleton();
    }

    public static GsonSingleton getInstance() {
        return Holder.INSTANCE;
    }

    public static Gson getGson() {
        return getInstance().gson;
    }

    protected Object readResolve() {
        return getInstance();
    }

}
