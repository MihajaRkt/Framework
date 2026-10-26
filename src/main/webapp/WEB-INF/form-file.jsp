<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body> 
    <h2> Formulaire </h2>
    <form action="${pageContext.request.contextPath}/upload-file" method="post" enctype="multipart/form-data">
        <p> Nom : <input type="text" name="nom" value="BMW"> </p>
        <p> Vitesse : <input type="number" name="vitesse" value="100"> </p>
        <p> Desc : <textarea name="description"></textarea> </p>
        <p> Photo : <input type="file" name="photo"> </p>
        <input type="submit" value="Valider">
    </form>
</body>
</html>