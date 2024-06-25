package com.utilFrame;

import java.util.HashMap;

public class ModelView {
    private String url;
    private HashMap <String, Object> data = new HashMap<>();;

    public ModelView(String url, HashMap<String, Object> data) {
        setUrl(url);
        setData(data);
    }
    public void addObject(String key, Object value) {
        this.data.put(key, value);
    }

    public ModelView(String url) {
        setUrl(url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap <String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public void addData (String nom, Object value){
        this.data.put(nom,value);
    }
}

