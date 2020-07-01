package com.example.auth.message;

import com.google.gson.Gson;

public abstract class AbstractMsg {
    public String toJson() {
        return new Gson().toJson(this);
    }
}
