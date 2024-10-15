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
import com.annotation.Get;
import com.annotation.Param;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.utilFrame.Mapping;
import com.utilFrame.VerbAction;

public class Reflect {

    // Execute method in Mapping
    public static Object executeMethod(Mapping m, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Class<?> clazz = Class.forName(m.getClassName());
        
        // Find the correct VerbAction based on the HTTP request method (GET, POST, etc.)
        VerbAction verbAction = findVerbActionForRequest(req.getMethod(), m.getVerbActions());
        if (verbAction == null) {
            throw new Exception("No matching method found for the HTTP method: " + req.getMethod());
        }

        // Get the method name from the matching VerbAction
        Method method = getMethod(clazz, verbAction.getMethodName());
        Object obj = clazz.getDeclaredConstructor().newInstance();  // Instantiate the class
        List<String> paramValues = new ArrayList<>();
        
        // Process method parameters
        for (Parameter methodParam : method.getParameters()) {
            if (methodParam.isAnnotationPresent(Param.class) &&
                isAmongRequestParameter(req.getParameterNames(), methodParam.getAnnotation(Param.class).name())) {
                Param p = methodParam.getAnnotation(Param.class);
                paramValues.add(req.getParameter(p.name()));
            } else if (isAmongRequestParameter(req.getParameterNames(), methodParam.getName())) {
                    paramValues.add(req.getParameter(methodParam.getName()));                      
            } else {
                    paramValues.add(null);
                }
            }

        // Execute the method with the parameters
        return method.invoke(obj, paramValues.toArray(new Object[0]));
    }

    // Find the VerbAction based on the HTTP request method (GET, POST, etc.)
    public static VerbAction findVerbActionForRequest(String httpMethod, List<VerbAction> verbActions) {
        for (VerbAction verbAction : verbActions) {
            if (verbAction.getVerb().equalsIgnoreCase(httpMethod)) {
                return verbAction;
            }
        }
        return null;
    }

    // Get the method by name from the class
    public static Method getMethod(Class<?> clazz, String methodName) throws Exception {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new NoSuchMethodException("No method found with name: " + methodName);
    }

    // Scan and find classes in a directory
    public static void scanFindClasses(String packageName, File directory, Map<String, Mapping> url_mapping, ArrayList<String> exceptions) throws Exception {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                scanFindClasses(packageName + "." + file.getName(), file, url_mapping, exceptions);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Controller.class)) {  // Get annotated class
                    Method[] methods = clazz.getDeclaredMethods();
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(Get.class)) {  // Get annotated method of the annotated class
                            Get getmapping = method.getAnnotation(Get.class);
                            checkDuplicateUrl(url_mapping, getmapping, exceptions, clazz, method);
                        }
                    }
                }
            }
        }
    }

    // Check for duplicate URL mappings
    public static void checkDuplicateUrl(Map<String, Mapping> url_mapping, Get getmapping, ArrayList<String> exceptions, Class<?> clazz, Method method) {
        if (!getmapping.url().isEmpty()) {
            if (url_mapping.containsKey(getmapping.url())) {
                exceptions.add("Duplicated URL \"" + getmapping.url() + "\" in " + clazz.getName() + " and " + url_mapping.get(getmapping.url()).getClassName());
            } else {
                Mapping mapping = new Mapping(clazz.getName());
                mapping.addVerbAction(new VerbAction(method.getName(), "GET"));
                url_mapping.put(getmapping.url(), mapping);
            }               
        }
    }

    // Check if a name parameter is in Request.getParametersName()
    public static boolean isAmongRequestParameter(Enumeration<String> parameters, String name) {
        while (parameters.hasMoreElements()) {
            if (parameters.nextElement().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
