package servlet;

import annotation.Url;
import annotation.WebAPI;
import donnees.ModelAndView;
import jakarta.servlet.ServletContext;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import objet.MapFile;
import objet.Model;
import objet.MySession;
import tools.SessionUtil;
import tools.URLDetails;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Utilitaire {

    private final ServletContext context;
    private final Map<String, Class<?>> mappingClasses;
    private final Map<URLDetails, Method> mappingMethodes;
    private final Map<String, MySession> sessions;
    private final String prefix;
    private final String suffix;
    private final String auth;
    private final Map<String, Object> instances;

    public Utilitaire(ServletContext context,
            Map<String, Class<?>> mappingClasses,
            Map<URLDetails, Method> mappingMethodes,
            Map<String, MySession> sessions,
            Map<String, Object> instances,
            String prefix,
            String suffix,
            String auth) {
        this.context = context;
        this.mappingClasses = mappingClasses;
        this.mappingMethodes = mappingMethodes;
        this.sessions = sessions;
        this.instances = instances;
        this.prefix = prefix;
        this.suffix = suffix;
        this.auth = auth;
    }

    public Method trouverMethode(String url, String methodeHttp) {
        if (mappingMethodes == null) {
            return null;
        }
        return mappingMethodes.get(new URLDetails(url, methodeHttp));
    }

    public boolean verifierAcces(Method methode, HttpServletRequest req,
            HttpServletResponse res) throws IOException {
        HttpSession httpSession = req.getSession(false);
        MySession currentSession = null;

        if (httpSession != null) {
            currentSession = sessions.computeIfAbsent(httpSession.getId(), key -> new MySession());
            SessionUtil.copyToMySession(httpSession, currentSession);
        }

        if (methode.isAnnotationPresent(annotation.Authentification.class)
                && (httpSession == null || currentSession.get(auth) == null)) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Accès refusé : Veuillez vous connecter.");
            return false;
        }

        if (methode.isAnnotationPresent(annotation.Role.class)) {
            if (httpSession == null || currentSession.get(auth) == null) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Accès refusé : Authentification requise pour vérifier les rôles.");
                return false;
            }

            String[] authorizedRoles = methode.getAnnotation(annotation.Role.class).roles();
            Object userRole = currentSession.get("role");
            boolean hasRole = false;
            if (userRole != null) {
                for (String role : authorizedRoles) {
                    if (role.equals(userRole.toString())) {
                        hasRole = true;
                        break;
                    }
                }
            }

            if (!hasRole) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Accès refusé : Rôle insuffisant.");
                return false;
            }
        }
        return true;
    }

    public boolean estAuthentifie(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        if (auth == null || auth.isBlank()) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Le paramètre d'authentification est absent.");
            return false;
        }

        HttpSession httpSession = req.getSession(false);
        if (httpSession == null) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Accès refusé : veuillez vous connecter.");
            return false;
        }

        MySession currentSession = sessions.computeIfAbsent(
                httpSession.getId(), key -> new MySession());
        SessionUtil.copyToMySession(httpSession, currentSession);

        if (currentSession.get(auth) == null) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Accès refusé : veuillez vous connecter.");
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public void afficherMethodes(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        List<Class<?>> listeClasses = (List<Class<?>>) context.getAttribute("listeClasses");
        String url = req.getPathInfo();
        String methode = req.getMethod();

        if (url == null || url.equals("/")) {
            if (listeClasses != null) {
                for (Class<?> clazz : listeClasses) {
                    if (clazz.getSimpleName().equals("TestController")) {
                        out.println("<h3>" + clazz.getName() + "</h3>");
                        out.println("URL: <ul>");
                        for (Method method : clazz.getDeclaredMethods()) {
                            if (method.isAnnotationPresent(Url.class)) {
                                Url annotation = method.getAnnotation(Url.class);
                                out.println("<li>" + annotation.value() + " ["
                                        + annotation.methode() + "]</li>");
                            }
                        }
                        out.println("</ul><hr>");
                    }
                }
            }
            return;
        }

        Class<?> clazz = mappingClasses != null ? mappingClasses.get(url) : null;
        Method method = mappingMethodes != null
                ? mappingMethodes.get(new URLDetails(url, methode))
                : null;
        if (clazz == null || method == null) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            throw new RuntimeException("<p>Aucun contrôleur ou aucune méthode ne correspond à l'URL " + url);
        }

        out.println("<h2>Classe : " + clazz.getName() + "</h2>");
        out.println("<h3>Méthode exécutée (" + methode + ") :</h3>");
        out.println("<ul><li>" + method.getName() + "()</li></ul>");
        try {
            Object instance = verifierInstance(clazz);
            method.invoke(instance);
        } catch (Exception e) {
            System.err.println("Erreur d'invocation : " + e.getMessage());
            out.println("<p style='color:red;'>Erreur lors de l'exécution de l'action.</p>");
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
        forwardModelAndView(req, res, mav);
    }

    public void verifierAPI(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String url = req.getServletPath();
        if (url == null || url.isEmpty()) {
            url = req.getPathInfo();
        }
        if (url.equals("/")) {
            afficherMethodes(req, res);
            return;
        }

        Method method = trouverMethode(url, req.getMethod());
        if (method == null) {
            afficherPage(req, res, url + " (Lien non valide)");
            return;
        }
        if (!method.isAnnotationPresent(WebAPI.class)) {
            afficherMethodes(req, res);
            return;
        }

        try {
            Object instance = verifierInstance(method.getDeclaringClass());
            Object result = method.invoke(instance);
            if (result instanceof String) {
                repondreEnJSONString(res, method, (String) result);
            } else if (result instanceof ModelAndView) {
                forwardModelAndView(req, res, (ModelAndView) result);
            } else {
                repondreEnJSON(res, method, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void forwardModelAndView(HttpServletRequest req, HttpServletResponse res,
            ModelAndView mav) throws ServletException, IOException {
        for (Map.Entry<String, Object> entry : mav.getHashmap().entrySet()) {
            req.setAttribute(entry.getKey(), entry.getValue());
        }
        RequestDispatcher dispatcher = req.getRequestDispatcher(
                prefix + mav.getView() + suffix);
        dispatcher.forward(req, res);
    }

    private void repondreEnJSON(HttpServletResponse res, Method method, Object result) {
        try {
            res.setContentType("application/json;charset=UTF-8");
            PrintWriter out = res.getWriter();
            String jsonResponse = "{\n  \"Methode\": \"" + method.getName()
                    + "\",\n  \"Retour\": " + result + "\n}";
            if (result instanceof Model model) {
                jsonResponse = "{\n  \"Methode\": \"" + method.getName()
                        + "\",\n  \"Id\": " + model.getId()
                        + ",\n  \"Nom\": \"" + model.getNom() + "\"\n}";
            }
            out.print(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void repondreEnJSONString(HttpServletResponse res, Method method, String value) {
        try {
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().print("{\n  \"Methode\": \"" + method.getName()
                    + "\",\n  \"Retour\": \"" + value + "\"\n}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void verifierForm(HttpServletRequest req, HttpServletResponse res) throws Exception {
        try {
            String url = req.getServletPath();
            if (url == null || url.isEmpty()) {
                url = req.getPathInfo();
            }
            Method method = trouverMethode(url, req.getMethod());
            if (method == null) {
                return;
            }

            method.setAccessible(true);
            Parameter[] parameters = method.getParameters();
            Object[] args = new Object[parameters.length];
            MySession mySessionInstance = null;

            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Class<?> parameterType = parameter.getType();
                if (parameterType.equals(HttpSession.class)) {
                    args[i] = req.getSession();
                } else if (parameterType.equals(MySession.class)) {
                    HttpSession httpSession = req.getSession();
                    mySessionInstance = sessions.computeIfAbsent(
                            httpSession.getId(), key -> new MySession());
                    String sessionValue = req.getParameter("user");
                    if (sessionValue != null) {
                        mySessionInstance.add("user", sessionValue);
                    }
                    SessionUtil.copyToMySession(httpSession, mySessionInstance);
                    args[i] = mySessionInstance;
                } else if (isTypeSimple(parameterType)) {
                    args[i] = convertirObjet(req.getParameter(parameter.getName()), parameterType);
                } else {
                    args[i] = remplirObjet(parameterType, req);
                }
            }

            Object instance = verifierInstance(method.getDeclaringClass());
            Object result = method.invoke(instance, args);
            if (mySessionInstance != null) {
                HttpSession httpSession = req.getSession();
                SessionUtil.copyToHttpSession(mySessionInstance, httpSession);
                sessions.put(httpSession.getId(), mySessionInstance);
            }
            if (result instanceof ModelAndView) {
                forwardModelAndView(req, res, (ModelAndView) result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object getPrimitiveDefault(Class<?> type) {
        if (type == int.class)
            return 0;
        if (type == double.class)
            return 0.0;
        if (type == boolean.class)
            return false;
        return 0;
    }

    private Object convertirObjet(String value, Class<?> type) {
        if (value == null || value.trim().isEmpty()) {
            return type.isPrimitive() ? getPrimitiveDefault(type) : null;
        } else if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == MapFile.class) {
            return MapFile.parsMapFile(value);
        }
        return value;
    }

    private Object remplirObjet(Class<?> type, HttpServletRequest req) {
        try {
            Object instance = verifierInstance(type);
            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                Part part = req.getPart(field.getName());
                if (part == null) {
                    continue;
                }
                String value;
                if (field.getType().getSimpleName().contains("MapFile")) {
                    value = part.getSubmittedFileName();
                    uploadFile(field.getName(), req);
                } else {
                    value = req.getParameter(field.getName());
                }
                field.set(instance, convertirObjet(value, field.getType()));
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isTypeSimple(Class<?> type) {
        return type.isPrimitive() || type.equals(String.class)
                || type.equals(Integer.class) || type.equals(Double.class)
                || type.equals(Float.class) || type.equals(Boolean.class)
                || type.equals(Long.class);
    }

    private void uploadFile(String fieldName, HttpServletRequest req) {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("app.properties")) {
            properties.load(input);
            String uploadDir = properties.getProperty("path");
            Part filePart = req.getPart(fieldName);
            if (filePart == null || filePart.getSubmittedFileName() == null
                    || filePart.getSubmittedFileName().isEmpty()) {
                throw new Exception("Fichier non disponible ou non existant");
            }
            File destinationFolder = new File(uploadDir);
            File targetFile = new File(destinationFolder, filePart.getSubmittedFileName());
            try (InputStream fileStream = filePart.getInputStream()) {
                Files.copy(fileStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Fichier enregistré");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Sprint 11 : Singleton
    // TODO: Vérifier si l'instance est déjà présente,
    // Utiliser les instances déjà crées au lieu de faire new Instance()
    private Object verifierInstance(Class<?> c) throws Exception {
        String key = c.getName();
        if (instances == null || !instances.containsKey(key)) {
            Object instance = c.getDeclaredConstructor().newInstance();
            instances.put(key, instance);
            System.out.println("Nouvelle instance créé: " + instance);
        } else {
            System.out.println("Instance déjà présente: " + instances.get(key));
        }

        return instances.get(key);
    }
}
