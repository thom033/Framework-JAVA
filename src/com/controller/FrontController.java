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
import com.annotation.ParamAnnotation;
import com.annotation.ParamObjectAnnotation;

import java.lang.reflect.*;

import com.utilFrame.Mapping;
import com.utilFrame.Function;
import com.utilFrame.ModelView;

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
                Method[] methods = clazz.getDeclaredMethods();
                Method targetMethod = null;

                for (Method method : methods) {
                    if (method.getName().equals(m.getMethodName())) {
                        targetMethod = method;
                        break;
                    }
                }

                if (targetMethod != null) {
                    Object[] params = Function.getParameterValue(request, targetMethod, ParamAnnotation.class,
                            ParamObjectAnnotation.class);
                    Object result = targetMethod.invoke(clazz.newInstance(), params);

                    if (result instanceof String) {
                        out.println(
                                "Resultat de l'execution de la méthode " + " " + m.getMethodName() + " est " + result);
                    } else if (result instanceof ModelView) {
                        ModelView modelView = (ModelView) result;
                        String destinationUrl = modelView.getUrl();
                        HashMap<String, Object> data = modelView.getData();
                        for (String key : data.keySet()) {
                            request.setAttribute(key, data.get(key));
                            System.out.println(data.get(key));
                        }
                        RequestDispatcher dispatcher = request.getRequestDispatcher(destinationUrl);
                        dispatcher.forward(request, response);
                    } else {
                        out.println("Le type de retour n'est ni un String ni un ModelView");
                    }
                } else {
                    out.println("Méthode non trouvée : " + m.getMethodName());
                }
            } catch (Exception e) {
                out.println("Erreur lors de l'exécution de la méthode : " + e.getMessage());
                e.printStackTrace(out);
            }
        } else {
            out.println("404 NOT FOUND");
        }
    }
}
