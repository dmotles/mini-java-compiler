#!/bin/bash
java -jar JFlex.jar minijava.flex
java -jar java-cup-11a.jar -interface -parser MiniJavaParser minijava.cup
javac MiniJavaC.java
