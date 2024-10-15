package com.utilFrame;

import java.io.File;
import java.lang.reflect.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import com.annotation.AnnotationController;
import com.annotation.Get;
import com.annotation.MappingAnnotation;
import com.annotation.POST;
import com.annotation.ParamAnnotation;
import com.annotation.ParamObjectAnnotation;

public class Function {
    boolean isController(Class<?> c) {
        return c.isAnnotationPresent(AnnotationController.class);
    }

    public List<String> getAllclazzsStringAnnotation(String packageName,
            Class<? extends java.lang.annotation.Annotation> annotation) throws Exception {
        List<String> res = new ArrayList<>();
        // root package
        String path = this.getClass().getClassLoader().getResource(packageName.replace('.', '/')).getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        File packageDir = new File(decodedPath);

        // browse all the files inside the package repository
        File[] files = packageDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().replace(".class", "");
                    Class<?> clazz = Class.forName(className);
                    // Check if the class has a package
                    if (clazz.getPackage() == null) {
                        throw new Exception("La classe " + className + " n'est pas dans un package.");
                    }
                    if (clazz.isAnnotationPresent(annotation)) {
                        res.add(clazz.getName());
                    }
                }
            }
        }
        return res;
    }

    public HashMap<String, Mapping> scanControllersMethods(List<Class<?>> controllers) throws Exception {
        HashMap<String, Mapping> res = new HashMap<>();
        
        // Parcourir toutes les classes de contrôleurs
        for (Class<?> controller : controllers) {
            Method[] methods = controller.getDeclaredMethods();
            
            // Parcourir toutes les méthodes d'un contrôleur
            for (Method method : methods) {
                // Vérifier si la méthode a l'annotation MappingAnnotation
                if (method.isAnnotationPresent(MappingAnnotation.class)) {
                    MappingAnnotation mappingAnnotation = method.getAnnotation(MappingAnnotation.class);
                    String url = mappingAnnotation.url(); // Récupère l'URL de l'annotation
                    String httpVerb = method.isAnnotationPresent(POST.class) ? "POST" : "GET"; // Vérifie si c'est un POST sinon GET
                    
                    // Capture les types des paramètres
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    
                    // Vérifier si l'URL est déjà présente dans le HashMap
                    if (res.containsKey(url)) {
                        Mapping existingMapping = res.get(url);
                        
                        // Vérifier si cette URL est associée à une autre classe
                        if (!existingMapping.getClassName().equals(controller.getName())) {
                            // Si l'URL est associée à une classe différente, on lève une erreur via scanError
                            throw new Exception("Erreur : L'URL " + url + " est déjà utilisée par la classe " 
                                                + existingMapping.getClassName() + " et ne peut pas être utilisée par " 
                                                + controller.getName());
                        }
                        
                        // Si la classe est la même, ajouter l'action à la liste des verbes
                        existingMapping.addVerbAction(new VerbAction(method.getName(), httpVerb, parameterTypes));
                        
                    } else {
                        // Si l'URL n'existe pas encore, créer un nouveau mapping
                        Mapping newMapping = new Mapping();
                        newMapping.setClassName(controller.getName()); // Définit le nom de la classe
                        newMapping.addVerbAction(new VerbAction(method.getName(), httpVerb, parameterTypes)); // Ajoute l'action
                        res.put(url, newMapping); // Ajoute le mapping au HashMap
                    }
                }
            }
        }
        
        // Appel de scanError pour vérifier les erreurs restantes
        String errorCheck = scanError(res);
        if (!"ok".equals(errorCheck)) { // Si scanError retourne autre chose que "ok", on lève une exception
            throw new Exception("Scan error detected: " + errorCheck);
        }
        
        return res;
    }

    
    
    
   public String scanError(HashMap<String, Mapping> classe) {
        String error = "ok";
     
        HashMap<String, String> urlToClassMap = new HashMap<>();
        
        // Parcours des entrées du HashMap
        for (Map.Entry<String, Mapping> entry : classe.entrySet()) {
            String url = entry.getKey();
            String className = entry.getValue().getClassName();  
            

            if (urlToClassMap.containsKey(url)) {
            
                if (!urlToClassMap.get(url).equals(className)) {
                    // Erreur : même URL mais classes différentes
                    error = "Erreur : L'URL " + url + " est utilisée par plusieurs classes (" 
                            + urlToClassMap.get(url) + " et " + className + ")";
                    break;
                }
            } else {
               
                urlToClassMap.put(url, className);
            }
        }

        return error;
    }

    


    public String getURIWithoutContextPath(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    public static Object convertParameterValue(String value, Class<?> type) {
        if (type == String.class) {
            return value;
        } else if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == float.class || type == Float.class) {
            return Float.parseFloat(value);
        } else if (type == byte.class || type == Byte.class) {
            return Byte.parseByte(value);
        } else if (type == char.class || type == Character.class) {
            if (value.length() != 1) {
                throw new IllegalArgumentException("Invalid character value:" + value);
            }
            return value.charAt(0);
        }
        return null;
    }

    public static Object[] getParameterValue(HttpServletRequest request, Method method,
            Class<ParamAnnotation> annotationClass,
            Class<ParamObjectAnnotation> paramObjectAnnotationClass) {
        Parameter[] parameters = method.getParameters();
        Object[] parameterValues = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            String paramName;

            if (parameters[i].isAnnotationPresent(annotationClass)) {
                ParamAnnotation param = parameters[i].getAnnotation(annotationClass);
                paramName = param.value();
                String paramValue = request.getParameter(paramName);
                System.out.println("Parameter: " + paramName + " = " + paramValue);
                parameterValues[i] = convertParameterValue(paramValue, parameters[i].getType());
            } else if (parameters[i].isAnnotationPresent(paramObjectAnnotationClass)) {
                ParamObjectAnnotation paramObject = parameters[i].getAnnotation(paramObjectAnnotationClass);
                String objName = paramObject.objName();
                try {
                    Object paramObjectInstance = parameters[i].getType().getDeclaredConstructor().newInstance();
                    Field[] fields = parameters[i].getType().getDeclaredFields();
                    for (Field field : fields) {
                        String fieldName = field.getName();
                        String paramValue = request.getParameter(objName + "." + fieldName);
                        System.out.println("Field: " + objName + "." + fieldName + " = " + paramValue);
                        if (paramValue != null) {
                            field.setAccessible(true);
                            field.set(paramObjectInstance, convertParameterValue(paramValue, field.getType()));
                        }
                    }
                    parameterValues[i] = paramObjectInstance;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to create and populate parameter object: " + e.getMessage());
                }
            } else {
                paramName = parameters[i].getName();
                String paramValue = request.getParameter(paramName);
                System.out.println("Parameter: " + paramName + " = " + paramValue);
                parameterValues[i] = convertParameterValue(paramValue, parameters[i].getType());
            }
        }

        return parameterValues;
    }

}
