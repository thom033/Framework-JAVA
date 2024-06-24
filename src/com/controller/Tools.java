package com.controller;

import java.io.PrintWriter;
import java.util.List;

public class Tools {
    public static void printMessage(String message, PrintWriter out) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<title>Error</title>");
        html.append("</head>");
        html.append("<body>");
        html.append("<h1>Message: "+message+" </h1>");
        html.append("</body>");
        html.append("</html>");
        out.println(html.toString());
    }
    public static void formatExceptionsAsHtml(List<String> exceptions, PrintWriter out) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<title>Error</title>");
        html.append("</head>");
        html.append("<body>");
        html.append("<h1>Error </h1>");
        html.append("<ul>");
        for (String exception : exceptions) {
            html.append("<li>").append(exception).append("</li>");
        }
        html.append("</ul>");
        html.append("</body>");
        html.append("</html>");
        out.println(html.toString());
    }
}
