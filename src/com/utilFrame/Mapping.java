package com.utilFrame;

import java.lang.reflect.Method;

public class Mapping {
    private String className;
    private String methodName;

    // Constructor with parameters
    public Mapping(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    // Default constructor
    public Mapping() {
    }

    // Getter for className
    public String getClassName() {
        return className;
    }

    // Setter for className
    public void setClassName(String className) {
        this.className = className;
    }

    // Getter for methodName
    public String getMethodName() {
        return methodName;
    }

    // Setter for methodName
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    // Method to return a string representation of the object
    public String str() {
        return "ClassName: " + this.className + ", MethodName: " + this.methodName;
    }

    // Method to invoke the specified method using reflection
    public Object invokeMethod() throws Exception {
        Class<?> clazz = Class.forName(this.className
        );
        Method method = clazz.getDeclaredMethod(this.methodName);
        Object instance = clazz.getDeclaredConstructor().newInstance();
        return method.invoke(instance);
    }
}
