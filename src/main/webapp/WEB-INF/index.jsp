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
            String test= (String) (request.getAttribute("test"));
            String test2= (String) (request.getAttribute("Another test"));
        %>

        <h3> Liste des attributs </h3>
        <ul>
            <li> test : <%= test %> </li>
            <li> Another test : <%= test2 %> </li>

        </ul>

    </body>
</html>