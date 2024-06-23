package util;

public class Mapping {
    String method;
    String controller;
    public Mapping(String method, String controller) {
        setController(controller);setMethod(method);
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public String getController() {
        return controller;
    }
    public void setController(String controller) {
        this.controller = controller;
    }
    public String str (){
        return "Method : "+ this.getMethod() + " Controller " + this.getController();
    }

}
