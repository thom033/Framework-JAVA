package com.controller;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.annotation.Controller;
import com.annotation.GET;
import com.annotation.Param;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.Mapping;

public class Reflect {
    // excecute methode in Mapping
    public static Object excetudeMethod(Mapping m, HttpServletRequest req, HttpServletResponse resp) throws Exception{
        Class<?> clazz = Class.forName(m.getController());
        Method method = Reflect.getMethode(clazz, m.getMethod());
        Object obj = clazz.newInstance();
        List<String> paramValues = new ArrayList<>();
        for (Parameter methodParam : method.getParameters()) {
            // par annotation  
            if  (methodParam.isAnnotationPresent(Param.class) && Reflect.isAmoungRequestParameter(req.getParameterNames(), methodParam.getAnnotation(Param.class).name()) ) {
                Param p = methodParam.getAnnotation(Param.class);
                paramValues.add(req.getParameter(p.name()));
            }
            else {   
                // par convention methodParamName = Name of req.getPramater
                if (Reflect.isAmoungRequestParameter(req.getParameterNames(), methodParam.getName())) {    
                    paramValues.add(req.getParameter(methodParam.getName()));                      
                }
                else{
                    paramValues.add(null);
                }
            }
        }
        for (String string : paramValues) {
            System.out.println(string);
        }
        // excecute Methode
        return method.invoke(obj, paramValues.toArray( new Object[0]));
    }
    // getMethode by name
    public static Method getMethode(Class<?> clazz, String methodeName) throws Exception{
        Method m = null;
        Method[] methods =  clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodeName)) {
                m =  method;
                break;
            }
        }
        return m;
    }
    public static void scanFindClasses (String packageName, File directory, Map<String, Mapping> url_mapping, ArrayList<String> exceptions) throws Exception {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                scanFindClasses (packageName + "." + file.getName(), file, url_mapping, exceptions);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Controller.class)) {  //get annoted class
                    Method[] methods = clazz.getDeclaredMethods();
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(Get.class)) {   // get annoted method of the class annoted
                            Get getmapping = method.getAnnotation(Get.class);
                            checkDucplicateUrl(url_mapping, getmapping, exceptions, clazz, method);
                        }
                    }
                }
            }
        }
    }
    public static void checkDucplicateUrl(Map<String, Mapping> url_mapping, Get getmapping, ArrayList<String> exceptions, Class<?> clazz, Method method ){
        if (!getmapping.url().isEmpty()) {
            if (url_mapping.containsKey(getmapping.url())) {
                exceptions.add("Duplicated url \"" +getmapping.url()+"\" in "+ clazz.getName() + " and "+ url_mapping.get(getmapping.url()).getController());
            }else{
                url_mapping.put( method.getAnnotation(Get.class).url() , new Mapping(method.getName(), clazz.getName()));
            }               
        }
    }
    // check if a nameParamter is in Request.getParamtersName()
    public static boolean isAmoungRequestParameter(Enumeration<String> parameters, String name){
        while (parameters.hasMoreElements()) {
            if (parameters.nextElement().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}

