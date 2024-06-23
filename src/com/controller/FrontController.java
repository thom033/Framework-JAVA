package com.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.Mapping;
import util.ModelView;

public class FrontController extends HttpServlet {

    private Map<String , Mapping> url_mapping;
    private String _package;
    private ArrayList<String> url_exceptions ;

    public void init() throws ServletException {
        try {            
            this.url_mapping = new HashMap<>();
            this.url_exceptions =  new ArrayList<>();
            this.scan();
        } catch (Exception e) {
            this.url_exceptions.add(e.getMessage());
        }
    }

    protected void scan() throws Exception {
        String p = this.getInitParameter("app.controllers.packageName");
        if (p == null || p.isEmpty()) {
            throw new ClassNotFoundException("Controller package not specified");
        }
        p = p.replace(".","/");
        File directory = new File(getServletContext().getRealPath("/WEB-INF/classes/" + p));

        if (!directory.exists() || !directory.isDirectory()) {
            throw new ClassNotFoundException("Controller Package \"" + p + "\" not Found");
        }
        this._package = p;
        Reflect.scanFindClasses(this._package, directory, this.url_mapping, this.url_exceptions);
    }
    protected void processrequest (HttpServletRequest req, HttpServletResponse resp) {
        PrintWriter out = null;
        String context_path =  req.getContextPath();
        String uri =  req.getRequestURI();
        List<String> exceptions = new ArrayList<>();
        try {
            out = resp.getWriter();            
            Mapping m = this.url_mapping.get(uri.replace(context_path, ""));    // get mapping matching with the url 
            if(m != null){
                Object response = Reflect.excetudeMethod(m , req, resp);
                if (response instanceof String) {
                    Tools.printMessage((String) response, out);
                }
                else if(response instanceof ModelView){
                    ModelView model = (ModelView) response;
                    String url = model.getUrl();
                    RequestDispatcher dispacther = req.getRequestDispatcher(url);   // redirect to the url page.jsp
                    for (String key : model.getData().keySet()) {                   // set data in model in request attribute
                        req.setAttribute(key, model.getData().get(key));
                    }
                    dispacther.forward(req, resp);
                } else {
                    exceptions.add("The return type of the method is not supported.");
                }           
            }
            else { // no mapping matching with the url 
                exceptions.add("404 Not Found");
            }
        }
        catch (Exception e) {
            exceptions.add(e.getMessage());
        }finally {
            if (out != null) {
                List<String> list = new ArrayList<>();
                list.addAll( this.url_exceptions);
                list.addAll(exceptions);
                if (!list.isEmpty()) {
                    Tools.formatExceptionsAsHtml(list, out);
                    return;
                }
                out.close();
            }
        }
    }
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processrequest(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processrequest(req, resp);
    }
}
