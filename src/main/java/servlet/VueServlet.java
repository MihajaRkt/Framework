package servlet;

import annotation.Controller;
import annotation.Url;
import annotation.UrlMapping;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.URLDetails;
import tools.Util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.lang.RuntimeException;

public class VueServlet extends HttpServlet {

    private List<Class<?>> listeClasses = new ArrayList<>();

    private Map<String, Class<?>> mappingClasses = new HashMap<>();
    private Map<URLDetails, Method> mappingMethodes = new HashMap<>();

    @Override
    public void init() throws ServletException {

        String cheminJar = getServletContext().getRealPath("/WEB-INF/lib/Sprint.jar");
        File fichierJar = new File(cheminJar);

        Util util = new Util();

        List<Class<?>> toutesLesClasses = new ArrayList<>();
        util.scannerJar(fichierJar, toutesLesClasses);

        listeClasses = util.filtrerParAnnotation(
                toutesLesClasses,
                Controller.class);

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
    }

    public void afficherMethodesGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("text/html;charset=UTF-8");

        PrintWriter out = res.getWriter();

        String url = req.getPathInfo();
        String methode= req.getMethod();

        if (url == null || url.equals("/")) {

            for (Class<?> clazz : listeClasses) {

                out.println("<h3>" + clazz.getName() + "</h3>");
                out.println("URL: ");
                out.println("<ul>");

                for (Method m : clazz.getDeclaredMethods()) {

                    if (m.isAnnotationPresent(Url.class)) {
                        Url annotation = (Url) m.getAnnotation(Url.class);
                        String valueUrl = annotation.value();
                        String meth = annotation.methode();

                        URLDetails urldetails = new URLDetails(valueUrl, meth);

                        out.println("<li>" + urldetails.getUrl() + "</li>");

                    }
                }

                out.println("</ul><hr>");
            }

            return;
        }

        Class<?> clazz = mappingClasses.get(url);
        URLDetails details= new URLDetails(url, methode);
        Method method = mappingMethodes.get(details);

        if (clazz != null) {
            out.println("<h2>Classe : " + clazz.getName() + "</h2>");
            out.println("<h3>Méthodes " +methode+ ":</h3>");
            out.println("<ul>");
            if (method.getAnnotation(Url.class).methode().equals(methode)) {
                out.println("<li>" + method.getName() + "</li>");

            }
            out.println("</ul>");

            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                method.invoke(instance);

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        } else {
            out.println("Classe non trouvée");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        afficherMethodesGet(req, res);
    }
}