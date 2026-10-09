package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ProcessRequest extends HttpServlet {
   public ProcessRequest() {
   }

   public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      this.processRequest(req, res);
   }

   public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      this.processRequest(req, res);
   }

   public void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      res.setContentType("text/html");
      PrintWriter out = res.getWriter();
      out.println("hello");
   }
}
