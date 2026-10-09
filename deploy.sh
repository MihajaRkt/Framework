#!/bin/bash

# Définition des variables
JAR_NAME="Sprint"
SRC_DIR="src/main/java"
WEBAPP_DIR="src/main/webapp"
BUILD_DIR="build"
LIB_DIR="lib"
SERVLET_API_JAR="$LIB_DIR/servlet-api.jar"
TOMCAT_WEBAPPS="/home/ideapad/tomcat-10.0.16/tomcat/webapps"   # <-- 'webapps' corrigé (avec 's')

# Nettoyage et création des répertoires
rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR/classes

# 1. Compilation des fichiers Java AVEC l'option -parameters
find $SRC_DIR -name "*.java" > sources.txt
javac -parameters -cp "$SERVLET_API_JAR" -d $BUILD_DIR/classes @sources.txt
rm sources.txt

# 2. Génération du fichier .jar du Framework
cd $BUILD_DIR/classes || exit
jar -cvf ../$JAR_NAME.jar *
cd - > /dev/null

# 3. Déploiement dans le dossier de l'application Tomcat (/webapps/Sprint)
APP_DIR="$TOMCAT_WEBAPPS/$JAR_NAME"
rm -rf "$APP_DIR"
mkdir -p "$APP_DIR/WEB-INF/lib"

# Copie des fichiers Web (JSP, web.xml)
cp -r $WEBAPP_DIR/* "$APP_DIR/"

# Copie du JAR généré
cp $BUILD_DIR/$JAR_NAME.jar "$APP_DIR/WEB-INF/lib/"

echo ""
echo "Déploiement terminé avec succès !"
echo ""