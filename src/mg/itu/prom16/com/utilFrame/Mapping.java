package com.utilFrame;
public class Mapping {
    String ClassName;
    String MethodName;

    public String str() {
        return "ClassName: " + this.ClassName+ ", MethodName: " + this.MethodName;
    }
    public Mapping(String ClassName, String MethodName) {
        setClassName(ClassName);
        setMethodName(MethodName);
    }
    public Mapping() {
    }
    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String ClassName) {
        this.ClassName= ClassName;
    }

    public String getMethodName() {
        return MethodName;
    }
    public void setMethodName(String MethodName) {
        this.MethodName = MethodName;
    }
}
