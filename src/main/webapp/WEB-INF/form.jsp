<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body> 
    <h2> Formulaire </h2>
    <form action="${pageContext.request.contextPath}/save" method="post">
        <p> Nom : <input type="text" name="nom"> </p>
        <p> Age : <input type="number" name="age"> </p>
        <input type="submit" value="Valider">
    </form>
</body>
</html>