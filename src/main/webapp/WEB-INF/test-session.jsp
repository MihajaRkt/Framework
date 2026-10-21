<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body> 
    <h2> Formulaire </h2>
    <form action="${pageContext.request.contextPath}/test-session" method="post">
        <p> Valeur de session "user" <input type="text" name="user"> </p>
        <input type="submit" value="Tester Session">
    </form>
</body>
</html>
