package com.utilFrame;

import java.util.ArrayList;
import java.util.List;

public class Mapping {
    private String className; // Class name associated with the mapping
    private List<VerbAction> verbActions; // List of VerbActions associated with the class

    // Default constructor
    public Mapping() {
        this.verbActions = new ArrayList<>(); 
    }

    // Constructor with className and an empty list of verbActions
    public Mapping(String className) {
        this.className = className;
        this.verbActions = new ArrayList<>(); 
    }

    // Getter for className
    public String getClassName() {
        return className;
    }

    // Setter for className
    public void setClassName(String className) {
        this.className = className;
    }

    // Getter for verbActions
    public List<VerbAction> getVerbActions() {
        return verbActions;
    }

    // Setter for verbActions
    public void setVerbActions(List<VerbAction> verbActions) {
        this.verbActions = verbActions;
    }

    // Method to add a new VerbAction to the list
    public void addVerbAction(VerbAction verbAction) {
        this.verbActions.add(verbAction);
    }

        @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ClassName: ").append(this.className).append(", VerbActions: [");

        // Parcours de la liste de verbActions
        for (int i = 0; i < verbActions.size(); i++) {
            VerbAction va = verbActions.get(i);
            sb.append("{methodName: ").append(va.getMethodName())
            .append(", verb: ").append(va.getVerb()).append("}");
            
            if (i < verbActions.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("]");
        return sb.toString();
    }

}
