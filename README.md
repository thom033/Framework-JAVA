# Framwork
S4- Framework Sping 


sprint 4
    A modifier dans web.xml projet test:
    - mettre controllers.FrontController comme servlet initial
    - importer annotations.* pour chaque controller
    - mettre tous les controlleurs dans un meme package et les annoter 
    <!-- @AnnotationController -->
    - ajouter en utilisant init-param avec le servlet initial le package contenant les controlleurs et mettre comme param-name 'controller'
        <!-- <init-param>
        <param-name>controller</param-name>
        <param-value>nom_de_votre_package</param-value>
        </init-param> -->
    - annoter vos methodes dans vos controllers de la maniere suivante
        <!-- @GET("votre_nom_de_methode") -->
    - mettre mes fichiers jsp dans:
        webapps/mon_projet/mes_fichiers.jsp
    - pour direger vers un view utiliser ModelView
    et ajouter les donnees a envoyer vers le ficher jsp a l'aide de addData dans la class ModelView
    exemple:
    <!-- @GET("listeEmp")
        public ModelView listerData() {
            ModelView mv = new ModelView("test.jsp");
            String anarana = "Jean";
            int age = 20;
            mv.addData("nom", anarana);
            mv.addData("nbr", age);
            return mv;
        } -->


Sprint 1 :
    Pour avoir la liste des controllers annoter:
        -cree un projet test
        -copier le bin/myLib/framework.jar dans le lib du projet
        -puis dans le fichier src 
            placer les classes controllers dans le package com/controller
        ps:si vous voulez modifier le package, il suffit de changer le xml dans la balise init param
        - annoter les classes comme suis "@Annotation controller"
        - puis deployer sur tomcat

