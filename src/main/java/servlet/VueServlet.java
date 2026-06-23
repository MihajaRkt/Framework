package servlet;

import annotation.Controller;
import annotation.UrlMapping;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.Util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VueServlet extends HttpServlet {

   private List<Class<?>> listeClasses = new ArrayList<>();

   private Map<String, Class<?>> mappingClasses = new HashMap<>();
   private Map<String, Method> mappingMethodes = new HashMap<>();

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

            if (methode.isAnnotationPresent(UrlMapping.class)) {

               UrlMapping annotation = methode.getAnnotation(UrlMapping.class);

               String url = annotation.value();

               mappingClasses.put(url, clazz);
               mappingMethodes.put(url, methode);
            }
         }
      }
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse res)
         throws ServletException, IOException {

      res.setContentType("text/html;charset=UTF-8");

      PrintWriter out = res.getWriter();

      String url = req.getPathInfo();

      if (url == null || url.equals("/")) {

         out.println("<h2>Liste des contrôleurs</h2>");

         for (Class<?> clazz : listeClasses) {

            out.println("<h3>" + clazz.getName() + "</h3>");
            out.println("<ul>");

            for (Method m : clazz.getDeclaredMethods()) {
               out.println("<li>" + m.getName() + "</li>");
            }

            out.println("</ul><hr>");
         }

         return;
      } else {
         out.println("url non disponible : " + url);
         out.println("<h2>Liste des contrôleurs</h2>");

         for (Class<?> clazz : listeClasses) {

            out.println("<h3>" + clazz.getName() + "</h3>");
            out.println("<ul>");

            for (Method m : clazz.getDeclaredMethods()) {
               out.println("<li>" + m.getName() + "</li>");
            }

            out.println("</ul><hr>");
         }

         Class<?> clazz = mappingClasses.get(url);
         Method method = mappingMethodes.get(url);

         out.println("<h2>Classe : " + clazz.getName() + "</h2>");
         out.println("<h3>Méthode :</h3>");
         out.println("<ul>");
         out.println("<li>" + method.getName() + "</li>");
         out.println("</ul>");

         return;

      }

   }
}