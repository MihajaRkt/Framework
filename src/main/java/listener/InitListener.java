package listener;

import annotation.Controller;
import annotation.Url;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import tools.URLDetails;
import tools.Util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebListener
public class InitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        String prefix = context.getInitParameter("prefix");
        String suffix = context.getInitParameter("suffix");

        String cheminJar = context.getRealPath("/WEB-INF/lib/Sprint.jar");
        File fichierJar = new File(cheminJar);

        Util util = new Util();
        List<Class<?>> toutesLesClasses = new ArrayList<>();
        util.scannerJar(fichierJar, toutesLesClasses);

        List<Class<?>> listeClasses = util.filtrerParAnnotation(
                toutesLesClasses,
                Controller.class);

        Map<String, Class<?>> mappingClasses = new HashMap<>();
        Map<URLDetails, Method> mappingMethodes = new HashMap<>();

        for (Class<?> clazz : listeClasses) {
            Method[] methodes = clazz.getDeclaredMethods();

            for (Method methode : methodes) {
                if (methode.isAnnotationPresent(Url.class)) {
                    Url annotation = (Url) methode.getAnnotation(Url.class);

                    String url = annotation.value();
                    String meth = annotation.methode();
                    URLDetails urldetails = new URLDetails(url, meth);

                    mappingClasses.put(url, clazz);

                    if (mappingMethodes.containsKey(urldetails)) {
                        throw new RuntimeException("Même méthode et même valeur sur l'url " + urldetails.getUrl());
                    } else {
                        mappingMethodes.put(urldetails, methode);
                    }
                }
            }
        }

        context.setAttribute("listeClasses", listeClasses);
        context.setAttribute("mappingClasses", mappingClasses);
        context.setAttribute("mappingMethodes", mappingMethodes);

        context.setAttribute("prefix", prefix);
        context.setAttribute("suffix", suffix);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}