<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="objet.Voiture" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Document</title> 
    </head>
    <body>

        <%
            Voiture v= (Voiture) (request.getAttribute("voiture"));
        %>

        <h3>Informations</h3>
        <p> Nom: <%= v.getNom() %> </p>
        <p> Vitesse : <%= v.getVitesse() %> </p>
        <p> Description : <%= v.getDescription() %> </p>

    </body>
</html>