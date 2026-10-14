<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>


<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Document</title> 
    </head>
    <body>

        <%
            String nom= (String) (request.getAttribute("nom"));
            int age= (int) (request.getAttribute("age"));
        %>

        <h3>Informations</h3>
        <p>Nom: <%= nom %> </p>
        <p>Age : <%= age %> ans</p>

    </body>
</html>