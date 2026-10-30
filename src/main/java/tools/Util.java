package tools;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import jakarta.servlet.ServletContext;

public class Util {

    public void scannerJar(File fichierJar, List<Class<?>> listeClasses) {
        try (JarFile jar = new JarFile(fichierJar)) {
            Enumeration<JarEntry> entrees = jar.entries();

            while (entrees.hasMoreElements()) {
                JarEntry entree = entrees.nextElement();
                String nomEntree = entree.getName();

                if (nomEntree.endsWith(".class")) {
                    String nomClasse = nomEntree.replace("/", ".")
                            .replace(".class", "");

                    try {
                        Class<?> clazz = Class.forName(nomClasse);
                        listeClasses.add(clazz);
                    } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la lecture du JAR : " + e.getMessage());
        }
    }

    public void scannerClassesApplication(ServletContext context, List<Class<?>> listeClasses) {
        scannerRessources(context, "/WEB-INF/classes/", listeClasses);
    }

    private void scannerRessources(ServletContext context, String chemin,
            List<Class<?>> listeClasses) {
        var ressources = context.getResourcePaths(chemin);
        if (ressources == null) {
            throw new IllegalStateException("Impossible de scanner les classes de l'application : "
                    + chemin);
        }

        for (String ressource : ressources) {
            if (ressource.endsWith("/")) {
                scannerRessources(context, ressource, listeClasses);
            } else if (ressource.endsWith(".class")) {
                String nomClasse = ressource.substring("/WEB-INF/classes/".length())
                        .replace('/', '.')
                        .replace(".class", "");
                if (!nomClasse.equals("module-info")
                        && !nomClasse.endsWith(".module-info")) {
                    chargerClasse(nomClasse, listeClasses);
                }
            }
        }
    }

    private void chargerClasse(String nomClasse, List<Class<?>> listeClasses) {
        try {
            listeClasses.add(Class.forName(nomClasse));
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            // Certaines classes compilées peuvent dépendre d'API non disponibles au scan.
        }
    }

    public List<Class<?>> filtrerParAnnotation(
            List<Class<?>> listeClasses,
            Class<? extends Annotation> annotationRecherchee) {

        List<Class<?>> classesAnnotees = new ArrayList<>();

        for (Class<?> clazz : listeClasses) {
            if (clazz.isAnnotationPresent(annotationRecherchee)) {
                classesAnnotees.add(clazz);
            }
        }

        return classesAnnotees;
    }

    public Class<?> trouverClasseParNom(List<Class<?>> listeClasses, String nomClasse) {

        for (Class<?> clazz : listeClasses) {
            if (clazz.getSimpleName().equalsIgnoreCase(nomClasse)) {
                return clazz;
            }
        }

        return null;
    }

    public List<Method> recupererMethodes(Class<?> clazz) {

        List<Method> methodes = new ArrayList<>();

        for (Method methode : clazz.getDeclaredMethods()) {
            methodes.add(methode);
        }

        return methodes;
    }
}