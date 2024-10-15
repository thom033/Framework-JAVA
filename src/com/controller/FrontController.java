package com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;

import com.annotation.AnnotationController;
import com.annotation.Get;
import com.annotation.POST;
import com.annotation.ParamAnnotation;
import com.annotation.ParamObjectAnnotation;
import com.annotation.RestApi;
import com.google.gson.Gson;

import com.utilFrame.Function;
import com.utilFrame.Mapping;
import com.utilFrame.ModelView;
import com.utilFrame.MySession;
import com.utilFrame.VerbAction;

public class FrontController extends HttpServlet {
    private List<String> controllers;
    private HashMap<String, Mapping> map;
    private static final String POST_METHOD = "POST";
    private static final String GET_METHOD = "GET";

    private List<Class<?>> getControllersAsClasses() throws ClassNotFoundException {
        List<Class<?>> classList = new ArrayList<>();
        for (String controller : this.controllers) {
            classList.add(Class.forName(controller));
        }
        return classList;
    }

    @Override
    public void init() throws ServletException {
        String packageToScan = this.getInitParameter("controller");
        try {
            this.controllers = new Function().getAllclazzsStringAnnotation(packageToScan, AnnotationController.class);
            List<Class<?>> controllerClasses = getControllersAsClasses();
            this.map = new Function().scanControllersMethods(controllerClasses);
        } catch (Exception e) {
            String errorMessage = "Error during initialization: " + e.getMessage();
            getServletContext().setAttribute("errorMessage", errorMessage);
            throw new ServletException(errorMessage);
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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check for an error message in the servlet context
        String errorMessage = (String) getServletContext().getAttribute("errorMessage");
        if (errorMessage != null) {
            displayError(response, errorMessage);
            getServletContext().removeAttribute("errorMessage");
            return;
        }

        String path = new Function().getURIWithoutContextPath(request);
        path = stripQueryParameters(path);

        if (!map.containsKey(path)) {
            handleNotFound(response);
            return;
        }

        try {
            Mapping mapping = map.get(path);
            Class<?> clazz = Class.forName(mapping.getClassName());
            Method targetMethod = findTargetMethod(mapping, request, clazz);

            validateHttpMethod(request, targetMethod);
            Object[] params = Function.getParameterValue(request, targetMethod, ParamAnnotation.class, ParamObjectAnnotation.class);
            Object controllerInstance = createControllerInstance(clazz);
            injectSessionIfRequired(controllerInstance, clazz, request);
            handleRequest(response, request, targetMethod, params, controllerInstance);
        } catch (ClassNotFoundException e) {
            handleInternalServerError(response, "Class not found: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            handleInternalServerError(response, "Method not found: " + e.getMessage());
        } catch (InstantiationException | IllegalAccessException e) {
            handleInternalServerError(response, "Instantiation error: " + e.getMessage());
        } catch (InvocationTargetException e) {
            handleInternalServerError(response, "Invocation error: " + e.getTargetException().getMessage());
        }
    }

    private String stripQueryParameters(String path) {
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }
        return path;
    }

    private void handleNotFound(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        PrintWriter out = response.getWriter();
        out.println("404 NOT FOUND");
    }

    private Method findTargetMethod(Mapping mapping, HttpServletRequest request, Class<?> clazz) throws NoSuchMethodException {
                Method targetMethod = null;
        for (VerbAction verbAction : mapping.getVerbActions()) {
            if (verbAction.getVerb().equalsIgnoreCase(request.getMethod())) {
                targetMethod = clazz.getDeclaredMethod(verbAction.getMethodName(), verbAction.getParameterTypes());
                        break;
                    }
                }

        if (targetMethod == null) {
            throw new NoSuchMethodException("No suitable method found for path. or the verb associated, check your form and anotation controller");
        }
        return targetMethod;
    }

    private void validateHttpMethod(HttpServletRequest request, Method targetMethod) throws ServletException {
        if (targetMethod.isAnnotationPresent(POST.class) && !request.getMethod().equals(POST_METHOD)) {
                    throw new ServletException("This method must be called via POST.");
                }
        if (targetMethod.isAnnotationPresent(Get.class) && !request.getMethod().equals(GET_METHOD)) {
                    throw new ServletException("This method must be called via GET.");
                }
    }

    private Object createControllerInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        try {
            return clazz.getDeclaredConstructor().newInstance();

        } catch (Exception e) {
            throw new IllegalArgumentException("error instance");
        }
    }

    private void injectSessionIfRequired(Object controllerInstance, Class<?> clazz, HttpServletRequest request) throws IllegalAccessException {
                for (Field field : clazz.getDeclaredFields()) {
                        if (field.getType().equals(MySession.class)) {
                            field.setAccessible(true);
                            field.set(controllerInstance, new MySession(request.getSession()));
            }
                        }
                    }

    private void handleRequest(HttpServletResponse response, HttpServletRequest request, Method targetMethod, Object[] params, Object controllerInstance) throws IOException, InvocationTargetException, ServletException {
        Gson gson = new Gson();

                    if (targetMethod.isAnnotationPresent(RestApi.class)) {
            try {
                handleRestApiResponse(response, targetMethod, params, controllerInstance, gson);
            } catch (InvocationTargetException e) {
                handleInternalServerError(response, "Error during REST API handling: " + e.getTargetException().getMessage());
            }
        } else {
            try {
                handleStandardResponse(response, request, targetMethod, params, controllerInstance);
            } catch (InvocationTargetException e) {
                handleInternalServerError(response, "Error during standard response handling: " + e.getTargetException().getMessage());
            }
        }
    }

    private void handleRestApiResponse(HttpServletResponse response, Method targetMethod, Object[] params, Object controllerInstance, Gson gson) throws IOException, InvocationTargetException {
        try {
                            Object result = targetMethod.invoke(controllerInstance, params);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
            String jsonResponse = (result instanceof ModelView) ? gson.toJson(((ModelView) result).getData()) : gson.toJson(result);
            PrintWriter out = response.getWriter();
                            out.print(jsonResponse);
                            out.flush();
        } catch (IllegalAccessException e) {
            throw new InvocationTargetException(e, "Illegal access during method invocation");
        }
    }

    private void handleStandardResponse(HttpServletResponse response, HttpServletRequest request, Method targetMethod, Object[] params, Object controllerInstance) throws IOException, InvocationTargetException, ServletException {
        try {
                            Object result = targetMethod.invoke(controllerInstance, params);

                    if (result instanceof String) {
                PrintWriter out = response.getWriter();
                        out.println("Result of method execution: " + result);
                    } else if (result instanceof ModelView) {
                        ModelView modelView = (ModelView) result;
                        String destinationUrl = modelView.getUrl();
                        HashMap<String, Object> data = modelView.getData();

                        for (String key : data.keySet()) {
                            request.setAttribute(key, data.get(key));
                        }

                        // Forward to the JSP page
                        RequestDispatcher dispatcher = request.getRequestDispatcher(destinationUrl);
                        dispatcher.forward(request, response);
                    } else {
                PrintWriter out = response.getWriter();
                        out.println("The return type is neither String nor ModelView");
                    }
        } catch (IllegalAccessException e) {
            throw new InvocationTargetException(e, "Illegal access during method invocation");
        }
    }

    private void handleInternalServerError(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        PrintWriter out = response.getWriter();
        out.println("Internal Server Error: " + errorMessage);
    }

    private void displayError(HttpServletResponse response, String errorMessage) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>An error occurred</h1>");
        out.println("<p>" + errorMessage + "</p>");
        out.println("</body></html>");
    }
}
