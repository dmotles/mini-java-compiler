JAVACUP="java-cup-11a.jar"
JFLAGS = -g -classpath ".:${JAVACUP}"
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES =

default: classes

classes: MiniJavaParser.java MiniJavaLexer.java syntaxtree $(CLASSES:.java=.class)

MiniJavaParser.java: MiniJavaParser.cup
	java -jar ${JAVACUP} -interface -parser MiniJavaParser MiniJavaParser.cup

MiniJavaLexer.java: MiniJavaLexer.flex
	jflex MiniJavaLexer.flex

syntaxtree:
	$(JC) $(JFLAGS) syntaxtree/*.java

clean:
	find . -name '*.class' -type f | xargs $(RM)
