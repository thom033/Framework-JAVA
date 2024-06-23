package util;

import java.util.HashMap;

public class ModelView {
    String url;
    HashMap<String, Object> data;
    public ModelView() {
        setData(new HashMap<>());
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public HashMap<String, Object> getData() {
        return data;
    }
    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }
    public void addObject(String key, Object o){
        this.getData().put(key, o);
    }


}
