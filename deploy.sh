#!/bin/bash

# Définition des variables
JAR_NAME="Sprint"
SRC_DIR="src/main/java"
WEBAPP_DIR="src/main/webapp"
BUILD_DIR="build"
LIB_DIR="lib"
SERVLET_API_JAR="$LIB_DIR/servlet-api.jar"
TOMCAT_WEBAPPS="/home/ideapad/tomcat-10.0.16/tomcat/webapp"   # <-- adapte ce chemin

# Nettoyage et création du répertoire temporaire pour les classes
rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR/classes

# 1. Compilation des fichiers Java du Framework
find $SRC_DIR -name "*.java" > sources.txt
javac -cp $SERVLET_API_JAR -d $BUILD_DIR/classes @sources.txt
rm sources.txt

# 2. Génération du fichier .jar (framework uniquement, pas les JSP)
cd $BUILD_DIR/classes || exit
jar -cvf ../$JAR_NAME.jar *
cd - > /dev/null

# 3. Déploiement complet dans Tomcat
rm -rf "$TOMCAT_WEBAPPS"
mkdir -p "$TOMCAT_WEBAPPS/WEB-INF/lib"

# Copie de toute la webapp (web.xml, JSP, WEB-INF, etc.)
cp -r $WEBAPP_DIR/* "$TOMCAT_WEBAPPS/"

# Copie du jar du framework dans WEB-INF/lib
cp $BUILD_DIR/$JAR_NAME.jar "$TOMCAT_WEBAPPS/WEB-INF/lib/"

echo ""
echo "Déploiement terminé"
echo ""