package com.utilFrame;

public class VerbAction {
    private String methodName;
    private String verb;
    private Class<?>[] parameterTypes;  


    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }
      // Constructor
      public VerbAction(String methodName, String verb, Class<?>[] parameterTypes) {
        this.methodName = methodName;
        this.verb = verb;
        this.parameterTypes = parameterTypes;
    }
    public VerbAction(String methodName, String verb) {
        this.methodName = methodName;
        this.verb = verb;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }
}
