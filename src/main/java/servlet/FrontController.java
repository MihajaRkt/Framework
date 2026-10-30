package servlet;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import objet.MySession;
import tools.URLDetails;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

@MultipartConfig
public class FrontController extends HttpServlet {

    private Utilitaire functions;

    @Override
    @SuppressWarnings("unchecked")
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        Map<String, Class<?>> mappingClasses = (Map<String, Class<?>>) context.getAttribute("mappingClasses");
        Map<URLDetails, Method> mappingMethodes = (Map<URLDetails, Method>) context.getAttribute("mappingMethodes");
        Map<String, MySession> sessions = (Map<String, MySession>) context.getAttribute("sessions");
        Map<String, Object> instances = (Map<String, Object>) context.getAttribute("instances");
        this.functions = new Utilitaire(
                context,
                mappingClasses,
                mappingMethodes,
                sessions,
                instances,
                (String) context.getAttribute("prefix"),
                (String) context.getAttribute("suffix"),
                getInitParameter("authentification"));
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
            if (req.getDispatcherType() == DispatcherType.ERROR) {
                return;
            }

            String url = req.getServletPath();
            if (url == null || url.isEmpty()) {
                url = req.getPathInfo();
            }

            if (url == null || url.equals("/")) {
                functions.afficherMethodes(req, res);
                return;
            }

            String methodeHttp = req.getMethod();
            Method methode = functions.trouverMethode(url, methodeHttp);
            if (methode != null && !functions.verifierAcces(methode, req, res)) {
                return;
            }

            if ("POST".equalsIgnoreCase(methodeHttp)) {
                functions.verifierForm(req, res);
            } else {
                functions.verifierAPI(req, res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
