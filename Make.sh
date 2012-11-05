#!/bin/sh
# CS1622 Project 3
# Daniel Motles <dmm141@pitt.edu>

JAVACUP=java-cup-11a.jar
JFLAGS="-g -classpath .:$JAVACUP"

banner() {
    echo "============================================================================="
    echo "                  $@"
    echo "============================================================================="
    echo "Press s+[enter] to skip or [enter] to continue..."
    read l
    if [ "$l" == "s" ]; then
        return 1
    fi
    return 0
}

banner "Building Parser" && java -jar ${JAVACUP} -parser MiniJavaParser MiniJavaParser.cup || true && \
banner "Building Lexer" && jflex MiniJavaLexer.flex || true && \
banner "Compiling AST Classes" && javac $JFLAGS syntaxtree/*.java visitor/*.java || true && \
banner "Compiling Student and Generated Code" && javac $JFLAGS *.java
exit $?
