package com.controller;

import java.io.IOException;
import java.io.PrintWriter;
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

import java.lang.reflect.*;

import com.utilFrame.Mapping;
import com.utilFrame.Function;
import com.utilFrame.ModelView;
import com.utilFrame.MySession;
public class FrontController extends HttpServlet {
    private List<String> controllers;
    private HashMap<String, Mapping> map;

    @Override
    public void init() throws ServletException {
        String packageToScan = this.getInitParameter("controller");
        try {
            this.controllers = new Function().getAllclazzsStringAnnotation(packageToScan, AnnotationController.class);
            this.map = new Function().scanControllersMethods(this.controllers);
        } catch (Exception e) {
            e.printStackTrace();
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
        PrintWriter out = response.getWriter();
        String path = new Function().getURIWithoutContextPath(request);

        if (path.contains("?")) {
            int index = path.indexOf("?");
            path = path.substring(0, index);
        }

        if (map.containsKey(path)) {
            Mapping m = map.get(path);
            try {
                Class<?> clazz = Class.forName(m.getClassName());
                Method targetMethod = null;

            // Find the method matching the Mapping
            for (Method method : clazz.getDeclaredMethods()) {
                    if (method.getName().equals(m.getMethodName())) {
                        targetMethod = method;
                        break;
                    }
                }

                if (targetMethod != null) {
                // Check for GET/POST annotation and HTTP method compatibility
                if (targetMethod.isAnnotationPresent(POST.class) && !request.getMethod().equals("POST")) {
                    throw new ServletException("This method must be called via POST.");
                }
                if (targetMethod.isAnnotationPresent(Get.class) && !request.getMethod().equals("GET")) {
                    throw new ServletException("This method must be called via GET.");
                }

                // Get the parameters for the method
                Object[] params = Function.getParameterValue(request, targetMethod, ParamAnnotation.class, ParamObjectAnnotation.class);
                Object controllerInstance = clazz.getDeclaredConstructor().newInstance();

                // Inject MySession if the field is present in the controller class
                for (Field field : clazz.getDeclaredFields()) {
                        if (field.getType().equals(MySession.class)) {
                            field.setAccessible(true);
                            field.set(controllerInstance, new MySession(request.getSession()));
                        }
                    }

                // If the method is annotated with @RestApi, return JSON response
                    if (targetMethod.isAnnotationPresent(RestApi.class)) {
                            Object result = targetMethod.invoke(controllerInstance, params);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            Gson gson = new Gson();
                            String jsonResponse;

                            if (result instanceof ModelView) {
                                ModelView modelView = (ModelView) result;
                                jsonResponse = gson.toJson(modelView.getData());
                            } else {
                                jsonResponse = gson.toJson(result);
                            }

                            out.print(jsonResponse);
                            out.flush();
                    } else {
                    // Standard processing for non-RestApi methods
                            Object result = targetMethod.invoke(controllerInstance, params);

                    if (result instanceof String) {
                        out.println("Result of method execution: " + result);
                    } else if (result instanceof ModelView) {
                        ModelView modelView = (ModelView) result;
                        String destinationUrl = modelView.getUrl();
                        HashMap<String, Object> data = modelView.getData();

                        // Set attributes for JSP forwarding
                        for (String key : data.keySet()) {
                            request.setAttribute(key, data.get(key));
                        }

                        // Forward to the JSP page
                        RequestDispatcher dispatcher = request.getRequestDispatcher(destinationUrl);
                        dispatcher.forward(request, response);
                    } else {
                        out.println("The return type is neither String nor ModelView");
                    }
                    }
                } else {
                out.println("Method not found: " + m.getMethodName());
                }
        } catch (ClassNotFoundException e) {
            out.println("Class not found: " + e.getMessage());
            } catch (Exception e) {
            e.printStackTrace(out); // Provide full exception stack trace for better debugging
            }
        } else {
            out.println("404 NOT FOUND");
        }
    }

    // protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    //         throws ServletException, IOException {
    //     PrintWriter out = response.getWriter();
    //     String path = new Function().getURIWithoutContextPath(request);

    //     if (path.contains("?")) {
    //         int index = path.indexOf("?");
    //         path = path.substring(0, index);
    //     }

    //     if (map.containsKey(path)) {
    //         Mapping m = map.get(path);
    //         try {
    //             Class<?> clazz = Class.forName(m.getClassName());
    //             Method[] methods = clazz.getDeclaredMethods();
    //             Method targetMethod = null;

    //             for (Method method : methods) {
    //                 if (method.getName().equals(m.getMethodName())) {
    //                     targetMethod = method;
    //                     break;
    //                 }
    //             }

    //             if (targetMethod != null) {
    //                 Object[] params = Function.getParameterValue(request, targetMethod, ParamAnnotation.class,
    //                         ParamObjectAnnotation.class);
    //                 Object controllerInstance = clazz.newInstance();

    //                 // Gestion de MySession
    //                 Field[] fields = clazz.getDeclaredFields();
    //                 for (Field field : fields) {
    //                     if (field.getType().equals(MySession.class)) {
    //                         field.setAccessible(true);
    //                         field.set(controllerInstance, new MySession(request.getSession()));
    //                     }
    //                 }

    //                 //  @RestApi
    //                 if (targetMethod.isAnnotationPresent(RestApi.class)) {
                        
    //                         Object result = targetMethod.invoke(controllerInstance, params);

    //                         // Configurer JSON
    //                         response.setContentType("application/json");
    //                         response.setCharacterEncoding("UTF-8");
    //                         Gson gson = new Gson();
    //                         String jsonResponse;

            
    //                         if (result instanceof ModelView) {
    //                             ModelView modelView = (ModelView) result;
    //                             jsonResponse = gson.toJson(modelView.getData());
    //                         } else {
    //                             jsonResponse = gson.toJson(result);
    //                         }

    //                         // Envoyer la réponse JSON
    //                         out.print(jsonResponse);
    //                         out.flush();

    //                 } else {
    //                         // Gestion normale sans @RestApi
    //                         Object result = targetMethod.invoke(controllerInstance, params);

    //                         if (result instanceof String) {
    //                             out.println(
    //                                     "Resultat de l'execution de la méthode " + " " + m.getMethodName() + " est " + result);
    //                         } else if (result instanceof ModelView) {
    //                             ModelView modelView = (ModelView) result;
    //                             String destinationUrl = modelView.getUrl();
    //                             HashMap<String, Object> data = modelView.getData();
    //                             for (String key : data.keySet()) {
    //                                 request.setAttribute(key, data.get(key));
    //                             }

    //                             // Dispatch vers la vue JSP
    //                             RequestDispatcher dispatcher = request.getRequestDispatcher(destinationUrl);
    //                             dispatcher.forward(request, response);
    //                         } else {
    //                             out.println("Le type de retour n'est ni un String ni un ModelView");
    //                         }
    //                 }
    //             } else {
    //                 out.println("Méthode non trouvée : " + m.getMethodName());
    //             }
    //         } catch (Exception e) {
    //             out.println("Erreur lors de l'exécution de la méthode : " + e.getMessage());
    //         }
    //     } else {
    //         out.println("404 NOT FOUND");
    //     }
    //     }




  
}
