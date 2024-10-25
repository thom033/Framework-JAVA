package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        // Chaîne de texte dans laquelle chercher
        String text = "http://localhost:8080/employee/list, " +
                      "http://localhost:8080/dept/details, " +
                      "http://localhost:8080/admin/users, " +
                      "http://localhost:8080/project/item.";

                      Mody ovaina kely ato indray le modif

        // Expression régulière pour trouver les URL et capturer les segments
        String regex = "http://localhost:8080/([^/]+)/([^/]+)";

        // Compiler l'expression régulière
        Pattern pattern = Pattern.compile(regex);

        // Créer un matcher pour trouver les correspondances
        Matcher matcher = pattern.matcher(text);

        // Afficher la deuxième valeur de chaque correspondance
        while (matcher.find()) {
            String secondValue = matcher.group(2); // Le deuxième groupe capturé
            System.out.println("Second value: " + secondValue + "  "+ matcher.group(1));

        }
    }
}
