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
            String test= (String) (request.getAttribute("lien"));
        %>

        <h3> Page </h3>
        <p> Lien utilisé : <%= test %> </p>

    </body>
</html>