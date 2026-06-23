package servlet;

import annotation.Controller;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.Util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class VueServlet extends HttpServlet {

   private List<Class> listeControleurs = new ArrayList<>();

   @Override
   public void init() throws ServletException {
      String cheminJar = getServletContext().getRealPath("/WEB-INF/lib/Sprint.jar");
      File fichierJar = new File(cheminJar);

      List<Class> toutesLesClasses = new ArrayList<>();
      Util util = new Util();

      util.scannerJar(fichierJar, toutesLesClasses);
      this.listeControleurs = util.filtrerParAnnotation(toutesLesClasses, Controller.class);
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      afficherClassesAnnotees(req, res);
   }

   public void afficherClassesAnnotees(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      res.setContentType("text/html");
      PrintWriter out = res.getWriter();
      
      out.println("<h3>Liste des classes annotés " +Controller.class.getSimpleName()+ " dans le JAR :</h3>");
      if (this.listeControleurs.isEmpty()) {
         out.println("<p>Aucune classe trouvée</p>");
      } else {
         out.println("<ul>");
         for (Class c : this.listeControleurs) {
            out.println("<li>" + c.getName() + "</li>");
         }
         out.println("</ul>");
      }
   }
}