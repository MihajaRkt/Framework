<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body> 
    <h2> Formulaire </h2>
    <form action="${pageContext.request.contextPath}/auth-test" method="post">
        <p> Nom <input type="text" name="user"> </p>
        <p> Mot de passe <input type="password"> </p>
        <input type="submit" value="Entrer">
    </form>
</body>
</html>
