<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="objet.MySession" %>


<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Document</title> 
    </head>
    <body>

        <%
            MySession sess = (MySession) request.getAttribute("session");
        %>

        <p> Valeur de la session : <%= sess.get("auth") %> </p>
        <p> Role : <%= sess.get("role") %> </p>
    </body>
</html>
