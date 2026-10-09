package tools;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Util {

    public void scannerJar(File fichierJar, List<Class> listeClasses) {
        try (JarFile jar = new JarFile(fichierJar)) {
            Enumeration<JarEntry> entrees = jar.entries();

            while (entrees.hasMoreElements()) {
                JarEntry entree = entrees.nextElement();
                String nomEntree = entree.getName();

                if (nomEntree.endsWith(".class")) {
                    String nomClasse = nomEntree.replace("/", ".")
                                                .replace(".class", "");
                    
                    try {
                        Class clazz = Class.forName(nomClasse);
                        listeClasses.add(clazz);
                    } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la lecture du JAR : " + e.getMessage());
        }
    }

    // Filtre générique pour n'importe quelle annotation (ex: Controller.class)
    public List<Class> filtrerParAnnotation(List<Class> listeClasses, Class<? extends Annotation> annotationRecherchee) {
        List<Class> classesAnnotees = new ArrayList<>();
        for (Class clazz : listeClasses) {
            if (clazz.isAnnotationPresent(annotationRecherchee)) {
                classesAnnotees.add(clazz);
            }
        }
        return classesAnnotees;
    }
}