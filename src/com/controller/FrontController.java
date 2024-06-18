package com.controller;

import com.annotation.AnnotationController;
import com.utilFrame.Mapping;
import com.annotation.GET;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FrontController extends HttpServlet {

    private Map<String, Mapping> liens = new HashMap<>();
    private List<Class<?>> controllers = new ArrayList<>();

    public void init() throws ServletException {
        super.init();
        findControllerClasses();
    }

    public void findControllerClasses() {
        String controllerPackage = getServletConfig().getInitParameter("controller");
        if (controllerPackage == null || controllerPackage.isEmpty()) {
            System.err.println("Controller package not specified");
            return;
        }

        String path = controllerPackage.replace('.', '/');
        File directory = new File(getServletContext().getRealPath("/WEB-INF/classes/" + path));

        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Package directory not found: " + directory.getAbsolutePath());
            return;
        }

        findClassesInDirectory(controllerPackage, directory);
    }

    private void findClassesInDirectory(String packageName, File directory) {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                findClassesInDirectory(packageName + "." + file.getName(), file);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                addClassIfController(className);
            }
        }
    }

    private void addClassIfController(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(AnnotationController.class)) {
                controllers.add(clazz);
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(GET.class)) {
                        GET getAnnotation = method.getAnnotation(GET.class);
                        String url = getAnnotation.url();
                        liens.put(url, new Mapping(clazz.getName(), method.getName()));
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + className);
        }
    }

     protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = null;
        String contextPath = req.getContextPath();
        String uri = req.getRequestURI().replace(contextPath, "");

        try {
            out = resp.getWriter();
            Mapping mapping = liens.get(uri);
            if (mapping != null) {
                out.println("Method found: " + mapping);
        
                Class<?> clazz = Class.forName(mapping.getClassName());
                Method method = clazz.getMethod(mapping.getMethodName());
        
                Class<?> returnType = method.getReturnType();
                out.println("Return type of the method: " + returnType.getName());
        
                Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
                Object result = method.invoke(controllerInstance);
        
                if (returnType.equals(String.class)) {
                    out.println((String) result);
                } else if (returnType.equals(ModelView.class)) {
                    ModelView mv = (ModelView) result;
                    out.println("ModelView URL: " + mv.getUrl());
        
                    mv.getData().forEach((key, value) -> req.setAttribute(key, value));
        
                    req.getRequestDispatcher(mv.getUrl()).forward(req, resp);
                } else {
                    out.println("Unsupported return type: " + returnType.getName());
                }
            } else {
                out.println("No method associated with URL: " + uri);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            out.println("Error: " + e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    public Map<String, Mapping> getLiens() {
        return liens;
    }

    public void setLiens(Map<String, Mapping> liens) {
        this.liens = liens;
    }
}
