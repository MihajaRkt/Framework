#!/bin/bash

# Définition des variables pour le Framework
JAR_NAME="Sprint"
SRC_DIR="src/main/java"
BUILD_DIR="build"
LIB_DIR="lib"
SERVLET_API_JAR="$LIB_DIR/servlet-api.jar"

# Nettoyage et création du répertoire temporaire pour les classes
rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR/classes

# 1. Compilation des fichiers Java du Framework
# On cherche tous les fichiers .java et on les compile dans build/classes
find $SRC_DIR -name "*.java" > sources.txt
javac -cp $SERVLET_API_JAR -d $BUILD_DIR/classes @sources.txt
rm sources.txt

# 2. Génération du fichier .jar
# On entre dans le dossier où sont stockées les classes compilées
cd $BUILD_DIR/classes || exit

# On crée le fichier Sprint.jar à la racine du dossier build (../../../build/)
jar -cvf ../$JAR_NAME.jar *

# On revient au dossier d'origine
cd - > /dev/null

echo ""
echo "Génération terminée ! Votre fichier se trouve ici : $BUILD_DIR/$JAR_NAME.jar"
echo ""