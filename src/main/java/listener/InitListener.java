package listener;

import annotation.Controller;
import annotation.Url;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import objet.MySession;
import tools.URLDetails;
import tools.Util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        String prefix = context.getInitParameter("prefix");
        String suffix = context.getInitParameter("suffix");

        String cheminJar = context.getRealPath("/WEB-INF/lib/Sprint.jar");
        if (cheminJar == null) {
            throw new IllegalStateException("Impossible de localiser Sprint.jar dans WEB-INF/lib");
        }
        File fichierJar = new File(cheminJar);

        Util util = new Util();
        List<Class<?>> classesApplication = new ArrayList<>();
        util.scannerClassesApplication(context, classesApplication);
        List<Class<?>> classesFramework = new ArrayList<>();
        util.scannerJar(fichierJar, classesFramework);

        List<Class<?>> listeClasses = new ArrayList<>();
        listeClasses.addAll(util.filtrerParAnnotation(classesApplication, Controller.class));
        listeClasses.addAll(util.filtrerParAnnotation(classesFramework, Controller.class));

        Map<String, Class<?>> mappingClasses = new HashMap<>();
        Map<URLDetails, Method> mappingMethodes = new HashMap<>();

        for (Class<?> clazz : listeClasses) {
            boolean classeApplication = classesApplication.contains(clazz);
            Method[] methodes = clazz.getDeclaredMethods();

            for (Method methode : methodes) {
                if (methode.isAnnotationPresent(Url.class)) {
                    Url annotation = (Url) methode.getAnnotation(Url.class);

                    String url = annotation.value();
                    String meth = annotation.methode();
                    URLDetails urldetails = new URLDetails(url, meth);

                    if (mappingMethodes.containsKey(urldetails)) {
                        if (!classeApplication) {
                            continue;
                        }
                        throw new RuntimeException("Même méthode et même valeur sur l'url " + urldetails.getUrl());
                    } else {
                        mappingMethodes.put(urldetails, methode);
                        mappingClasses.putIfAbsent(url, clazz);
                    }
                }
            }
        }

        Map<String, MySession> sessions = new ConcurrentHashMap<>();
        Map<String, Object> instances = new ConcurrentHashMap<>();

        context.setAttribute("listeClasses", listeClasses);
        context.setAttribute("mappingClasses", mappingClasses);
        context.setAttribute("mappingMethodes", mappingMethodes);

        context.setAttribute("prefix", prefix);
        context.setAttribute("suffix", suffix);
        context.setAttribute("sessions", sessions);
        context.setAttribute("instances", instances);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
