package servlet;

import annotation.Controller;
import annotation.Url;
import annotation.WebAPI;
import donnees.ModelAndView;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import objet.Model;
import tools.URLDetails;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        processRequest(req, res);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse res) {
        try {
            String url = req.getServletPath();
            if (url == null || url.isEmpty()) {
                url = req.getPathInfo();
            }

            if (url == null || url.equals("/")) {
                afficherMethodes(req, res);
                return;
            }

            String methodeHttp = req.getMethod();

            if ("POST".equalsIgnoreCase(methodeHttp)) {
                verifierForm(req, res);
            } else {
                verifierAPI(req, res);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void afficherPage(HttpServletRequest req, HttpServletResponse res, String objet)
            throws ServletException, IOException {

        ModelAndView mav = new ModelAndView();
        mav.setView("index");

        Map<String, Object> map = new HashMap<>();
        map.put("test", "Just a test");
        map.put("lien", objet);
        mav.setHashmap(map);

        String url = this.prefix + mav.getView() + this.suffix;

        for (Map.Entry<String, Object> entry : mav.getHashmap().entrySet()) {
            req.setAttribute(entry.getKey(), entry.getValue());
        }

        RequestDispatcher dispat = req.getRequestDispatcher(url);
        dispat.forward(req, res);

    }

    private Method trouverMethode(List<Class<?>> listeClasses, String url) {
        if (listeClasses != null) {
            for (Class<?> clazz : listeClasses) {
                if (clazz.isAnnotationPresent(Controller.class)) {
                    for (Method m : clazz.getDeclaredMethods()) {
                        if (m.isAnnotationPresent(Url.class)) {
                            Url annotationUrl = m.getAnnotation(Url.class);
                            if (annotationUrl.value().equals(url)) {
                                return m;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    // Condition pour l'existence de l'annotation
    // Vrai (Printwriter pour JSON)
    // SI String, tonga de apetaka sinon manao toJSON
    // Faux (Dispatcher)
    public void verifierAPI(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        String url = req.getServletPath();
        if (url == null || url.isEmpty()) {
            url = req.getPathInfo();
        }

        ServletContext context = getServletContext();
        List<Class<?>> listeClasses = (List<Class<?>>) context.getAttribute("listeClasses");
        if (url.equals("/")) {
            afficherMethodes(req, res);
            return;
        }

        Method m = trouverMethode(listeClasses, url);

        if (m != null) {
            if (m.isAnnotationPresent(WebAPI.class)) {
                try {
                    Class<?> clazz = m.getDeclaringClass();
                    Object instance = clazz.getDeclaredConstructor().newInstance();

                    Object o = m.invoke(instance);

                    if (o instanceof String) {
                        repondreEnJSONString(res, m, (String) o);
                    } else if (o instanceof ModelAndView) {
                        ModelAndView mav = (ModelAndView) o;
                        String lien = this.prefix + mav.getView() + this.suffix;

                        for (Map.Entry<String, Object> entry : mav.getHashmap().entrySet()) {
                            req.setAttribute(entry.getKey(), entry.getValue());
                        }

                        RequestDispatcher dispat = req.getRequestDispatcher(lien);
                        dispat.forward(req, res);
                    } else {
                        repondreEnJSON(res, m, o);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                afficherMethodes(req, res);
            }
            return;
        }

        String erreur = url + " (Lien non valide)";
        afficherPage(req, res, erreur);
    }

    private void repondreEnJSON(HttpServletResponse res, Method m, Object o) {
        try {
            res.setContentType("application/json;charset=UTF-8");
            PrintWriter out = res.getWriter();

            String jsonResponse = "{\n" +
                    "  \"Methode\": \"" + m.getName() + "\",\n" +
                    "  \"Retour\": " + o + "\n" +
                    "}";

            if (o instanceof Model) {
                Model model = (Model) o;
                jsonResponse = "{\n" +
                        "  \"Methode\": \"" + m.getName() + "\",\n" +
                        "  \"Id\": " + model.getId() + ",\n" +
                        "  \"Nom\": \"" + model.getNom() + "\"\n" +
                        "}";
            }

            out.print(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void repondreEnJSONString(HttpServletResponse res, Method m, String s) {
        try {
            res.setContentType("application/json;charset=UTF-8");
            PrintWriter out = res.getWriter();

            String jsonResponse = "{\n" +
                    "  \"Methode\": \"" + m.getName() + "\",\n" +
                    "  \"Retour\": \"" + s + "\"\n" +
                    "}";

            out.print(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Alaina automatique ny valeur ao @ form
    // Comparena amle type ao @ form
    public void verifierForm(HttpServletRequest req, HttpServletResponse res) {
        try {
            String url = req.getServletPath();
            if (url == null || url.isEmpty()) {
                url = req.getPathInfo();
            }

            ServletContext context = getServletContext();
            List<Class<?>> listeClasses = (List<Class<?>>) context.getAttribute("listeClasses");

            Method m = trouverMethode(listeClasses, url);
            m.setAccessible(true);
            Class<?> clazz = m.getDeclaringClass();
            System.out.println(clazz);

            if (m != null && clazz != null) {
                Object instance = clazz.getDeclaredConstructor().newInstance();

                Parameter[] parameters = m.getParameters();
                Object[] args = new Object[parameters.length];

                for (int i = 0; i < parameters.length; i++) {
                    Parameter param = parameters[i];

                    Class<?> paramType = param.getType();

                    if (isTypeSimple(paramType)) {
                        String paramName = param.getName();
                        String paramValue = req.getParameter(paramName);
                        args[i] = convertirObjet(paramValue, paramType);
                    } else{
                        args[i] = remplirObjet(paramType, req);
                    }
                }

                Object result = m.invoke(instance, args);
                if (result instanceof ModelAndView) {
                    ModelAndView mav = (ModelAndView) result;
                    String lien = this.prefix + mav.getView() + this.suffix;

                    for (Map.Entry<String, Object> entry : mav.getHashmap().entrySet()) {
                        req.setAttribute(entry.getKey(), entry.getValue());
                    }

                    RequestDispatcher dispat = req.getRequestDispatcher(lien);
                    dispat.forward(req, res);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode utilitaire pour les types primitifs quand la valeur reçue est
    // null/vide
    private Object getPrimitiveDefault(Class<?> type) {
        if (type == int.class)
            return 0;
        if (type == double.class)
            return 0.0;
        if (type == boolean.class)
            return false;
        return 0;
    }

    private Object convertirObjet(String paramValue, Class<?> paramType) {
        if (paramValue == null || paramValue.trim().isEmpty()) {
            return paramType.isPrimitive() ? getPrimitiveDefault(paramType) : null;
        } else if (paramType == int.class || paramType == Integer.class) {
            return Integer.parseInt(paramValue);
        } else if (paramType == double.class || paramType == Double.class) {
            return Double.parseDouble(paramValue);
        } else if (paramType == boolean.class || paramType == Boolean.class) {
            return Boolean.parseBoolean(paramValue);
        } else {
            return paramValue;
        }
    }

    private Object remplirObjet(Class<?> clazz, HttpServletRequest req) {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                String fieldName = field.getName();
                String paramValue = req.getParameter(fieldName);

                if (paramValue != null) {
                    Class<?> fieldType = field.getType();
                    Object convertedValue = convertirObjet(paramValue, fieldType);

                    field.set(instance, convertedValue);
                }
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isTypeSimple(Class<?> type) {
        return type.isPrimitive()
                || type.equals(String.class)
                || type.equals(Integer.class)
                || type.equals(Double.class)
                || type.equals(Float.class)
                || type.equals(Boolean.class)
                || type.equals(Long.class);
    }
}