# READ ME FOR THE SPRINT 
# Framwork
S4- Framework Sping 

Bonjour tous le monde,
#sprint07
Objectif:
Permettre de mettre en paramètre d'une fonction de mapping un objet et de setup ses attributs.
Etape 1: Créer une annotation pour l'objet en paramètre
Etape 2: Créer un process qui va s'effectuer automatiquement lors que le programme détecte l'annotation créée plus tôt
             -> Ce process va bouclé tous les attributs de l'objet pour obtenir leurs valeurs attribuées dans request.getParameter
            -> Créer une nouvelle annotation de type ElementType.FIELD pour donner le choix aux utilisateurs du framework le choix entre utilisé le même nom dans sa classe et son formulaire ou annoté l'attribut avec le nom présent dans son formulaire sans devoir à utilisé le même nom
A part cela, ce sera le même process que le #sprint6.
Remarque:
Dans le #sprint6, il nous a été demandé que si jamais l'utilisateur du framework n'avais pas annoté ses paramètres d'utilisé le nom des paramètres en question.
Problème: Reflect API, pour des raisons de sécurités, ne renvoi que des noms génériques si nous ne spécifions pas -parameters lors de la compilation du projet.
Pour pallier à cela, nous allons utilisés une librairie externe paranamer de Throughwork: https://mvnrepository.com/.../com.../paranamer/2.8
L'image ci-jointe donne un exemple de code


Hello daholo
#sprint6
SPRINT 6 : Envoyer des données d'un formulaire vers un contrôleur
FRAMEWORK :
Étape 1 : Créer une annotation @Param
Attribut :
-   String name
Étape 2 : Ajouter un argument HttpServletRequest request dans la fonction invoquant les méthodes des contrôleurs
-   Boucler sur les arguments de la méthode du contrôleur et récupérer les noms des annotations @Param de chaque argument
-   Attribuer la valeur de chaque argument en utilisant request.getParameter avec le nom de son annotation comme argument
TEST :
-   Créer un formulaire d'envoi (ex : Entrer votre nom)
-   Créer une méthode dans un contrôleur pour récupérer le nom entré
-   Renvoyer un ModelView pour vérifier si le nom est bien récupéré
NOTE :
Pour les liens GET tels que "emplist?ville=105"
-   Lors de la récupération du Mapping, enlever le texte après "?" et utiliser le lien avant "?"

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

Salama daholo
#sprint_4
Objectif:
Envoyer des données du controller vers view
Etapes:
Côté Framework
_créer une classe ModelView qui aura pour attributs: 
 _String url[url de destination après l'exécution de la méthode], 
 _HashMap<String : nom de la variable, Object: sa valeur)> data [donnée à envoyer vers cette view],
    _créer une fonction "AddObject" qui a comme type de retour void pour pouvoir mettre les données dans HashMap
Dans FrontController,
 dans ProcessRequest, récupérer les données issues de la méthode annotée Get
     _si les data sont de type string, retourner la valeur directement
     _si les données sont de type ModelView, récupérer le url et dispatcher les données vers cet url 
  _boucle de data: y faire request.setAttribute
     _si autre, retourner "non reconnu"
Test: 
Les méthodes des controlleurs qui seront annotées ont pour type de retour "String" ou "ModelView"

Salama 🙃, - SPRINT 03 -
Objectif :
Exécuter la méthode de la classe associée à une URL donnée
Étapes :
# Dans le FrontController ( ProcessRequest ):
Si on trouve le Mapping associé à l'URL ,
- Récupérer la classe par son nom
- Récupérer la méthode par son nom
- Invoquer la méthode sur l'instance de la classe
- Afficher la valeur retournée par la méhode
# Projet Test
Les méhodes des controlleurs qui seront annotées ont pour type de retour "String"


Sprint 02
Objectif :
 Récupérer la classe et la méthode associées à une URL donnée
Étapes :
 # Créer une annotation GET pour annoter les méthodes dans les contrôleurs
 # Créer la classe Mapping qui aura pour attributs :
 String className
 String methodName
 # Dans FrontController :
 - Enlever l'attribut boolean
 - Créer un HashMap (String url, Mapping)
 - init :
 Faire les scans pour avoir les contrôleurs
 Pour chaque contrôleur, prendre toutes les méthodes et voir s'il y a l'annotation GET
 S'il y en a, créer un nouveau Mapping : (controller.name, method.name)
 HashMap.associer(annotation.value, Mapping)
 # ProcessRequest
 Prendre le Mapping associé au chemin URL de la requête
 Si on trouve le Mapping associé, afficher le chemin URL et le Mapping
 Sinon, afficher qu'il n'y a pas de méthode associée à ce chemin


Sprint 1 :
    Pour avoir la liste des controllers annoter:
        -cree un projet test
        -copier le bin/myLib/framework.jar dans le lib du projet
        -puis dans le fichier src 
            placer les classes controllers dans le package com/controller
        ps:si vous voulez modifier le package, il suffit de changer le xml dans la balise init param
        - annoter les classes comme suis "@Annotation controller"
        - puis deployer sur tomcat

Sprint 01:
Creer une nouvelle branche "sprint1-ETU"
Et envoyer le framework sur git.
Modif dans mon framework :
1-Creer AnnotationController
2- Annoter mes controleurs avec AnnotationController
3- Mettre mes controleurs dans le meme package
Modif dans FrontController :
(Prendre le nom du package où se trouvent mes controleurs)
1- Tester si j'ai déjà scanner mes controleurs
+ Si oui, afficher la liste des noms de mes controleurs 
+Sinon scanner, puis afficher la liste des noms de mes controleurs 
Modif dans le projet de test:
Web.xml
     + declarer  le nom du package (misy ny controller rehetra) (using init-param)
     + declarer mon frontServlet
‌Creer un ReadMe file pour décrire précisément les configs à faire pour utiliser mon framework.(envoyer le ReadMe file avec mon framework sur Git)
Enjoy !!!🙃

Sprint0:
Objectif: Créer un servlet qui réceptionnera toutes les requêtes clients et qui les traitera
Etapes:
    -Coté Framework:
        -Créer un servlet FrontController dont la methode processRequest affichera l'url dans 
        lequel on se trouve
    -Coté Test
        -Associer le FrontController à l'url pattern "/" dans le web.xml du projet
        -Tester n'importe quel url associé au projet web
NB: La partie Test ne sera pas envoyée sur Git
