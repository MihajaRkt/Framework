package servlet;

import annotation.Controller;
import annotation.Url;
import donnees.ModelAndView;

import java.io.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import tools.URLDetails;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VueServlet extends HttpServlet {

    private Map<String, Class<?>> mappingClasses;
    private Map<URLDetails, Method> mappingMethodes;
    private String prefix;
    private String suffix;

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        this.prefix = (String) context.getAttribute("prefix");
        this.suffix = (String) context.getAttribute("suffix");
        this.mappingClasses = (Map<String, Class<?>>) context.getAttribute("mappingClasses");
        this.mappingMethodes = (Map<URLDetails, Method>) context.getAttribute("mappingMethodes");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // afficherMethodes(req, res);
        afficherPage(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // afficherMethodes(req, res);
    }

    @SuppressWarnings("unchecked")
    public void afficherMethodes(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();

        ServletContext context = getServletContext();
        List<Class<?>> listeClasses = (List<Class<?>>) context.getAttribute("listeClasses");

        String url = req.getPathInfo();
        String methode = req.getMethod();

        // Liste entiere
        if (url == null || url.equals("/")) {
            if (listeClasses != null) {
                for (Class<?> clazz : listeClasses) {
                    out.println("<h3>" + clazz.getName() + "</h3>");
                    out.println("URL: <ul>");

                    for (Method m : clazz.getDeclaredMethods()) {
                        if (m.isAnnotationPresent(Url.class)) {
                            Url annotation = m.getAnnotation(Url.class);
                            out.println("<li>" + annotation.value() + " [" + annotation.methode() + "]</li>");
                        }
                    }
                    out.println("</ul><hr>");
                }
            }
            return;
        }

        // Filtre selon l'url
        Class<?> clazz = (mappingClasses != null) ? mappingClasses.get(url) : null;
        URLDetails details = new URLDetails(url, methode);
        Method method = (mappingMethodes != null) ? mappingMethodes.get(details) : null;

        if (clazz != null && method != null) {
            out.println("<h2>Classe : " + clazz.getName() + "</h2>");
            out.println("<h3>Méthode exécutée (" + methode + ") :</h3>");
            out.println("<ul><li>" + method.getName() + "()</li></ul>");

            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                method.invoke(instance);
            } catch (Exception e) {
                System.err.println("Erreur d'invocation : " + e.getMessage());
                out.println("<p style='color:red;'>Erreur lors de l'exécution de l'action.</p>");
            }
        } else {
            res.setStatus(res.SC_NOT_FOUND);
            throw new RuntimeException("<p>Aucun contrôleur ou aucune méthode ne correspond à l'URL " + url);
        }
    }

    public void afficherPage(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        ModelAndView mav = new ModelAndView();
        mav.setView("index");

        Map<String, Object> map = new HashMap<>();
        map.put("test", "1er test");
        map.put("Another test", "2e test");
        mav.setHashmap(map);

        String url = this.prefix + mav.getView() + this.suffix;

        System.out.println("URL DE LA PAGEEEEEEEEEEEEEEEEEEE" + url);

        for (Map.Entry<String, Object> entry : mav.getHashmap().entrySet()) {
            req.setAttribute(entry.getKey(), entry.getValue());
        }

        RequestDispatcher dispat = req.getRequestDispatcher(url);
        dispat.forward(req, res);
    }

}