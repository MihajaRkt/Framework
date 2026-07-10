package servlet;

import annotation.Controller;
import annotation.Url;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.URLDetails;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class VueServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        // 💨 Tout le travail de scan a migré dans InitListener !
    }

<<<<<<< Updated upstream
    public void afficherMethodesGet(HttpServletRequest req, HttpServletResponse res)
=======
    @SuppressWarnings("unchecked")
    public void afficherMethodes(HttpServletRequest req, HttpServletResponse res)
>>>>>>> Stashed changes
            throws ServletException, IOException {

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();

        ServletContext context = getServletContext();
        List<Class<?>> listeClasses = (List<Class<?>>) context.getAttribute("listeClasses");
        Map<String, Class<?>> mappingClasses = (Map<String, Class<?>>) context.getAttribute("mappingClasses");
        Map<URLDetails, Method> mappingMethodes = (Map<URLDetails, Method>) context.getAttribute("mappingMethodes");

        String url = req.getPathInfo();
        String methode = req.getMethod();

        // Cas 1 : URL racine ou nulle -> On liste tout
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

        // Cas 2 : URL spécifique -> Routage dynamique vers l'action
        Class<?> clazz = (mappingClasses != null) ? mappingClasses.get(url) : null;
        URLDetails details = new URLDetails(url, methode);
        Method method = (mappingMethodes != null) ? mappingMethodes.get(details) : null;

        if (clazz != null && method != null) {
            out.println("<h2>Classe : " + clazz.getName() + "</h2>");
            out.println("<h3>Méthode exécutée (" + methode + ") :</h3>");
            out.println("<ul><li>" + method.getName() + "()</li></ul>");

            try {
                // Instanciation du contrôleur et invocation de la méthode correspondante
                Object instance = clazz.getDeclaredConstructor().newInstance();
                method.invoke(instance);
            } catch (Exception e) {
                System.err.println("Erreur d'invocation : " + e.getMessage());
                out.println("<p style='color:red;'>Erreur lors de l'exécution de l'action.</p>");
            }
        } else {
            res.setStatus(res.SC_NOT_FOUND);
            out.println("<h2>404 - Ressource non trouvée</h2>");
            out.println("<p>Aucun contrôleur ou aucune méthode ne correspond à l'URL " + url + " avec le verbe " + methode + ".</p>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        afficherMethodesGet(req, res);
    }
<<<<<<< Updated upstream
=======

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        afficherMethodes(req, res);
    }
>>>>>>> Stashed changes
}