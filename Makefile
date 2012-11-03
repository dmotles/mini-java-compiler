all: MiniJavaLexer.flex MiniJavaParser.cup MiniJavaPrettyPrinter.java
	/bin/sh Make.sh

clean:
	rm -f *.class
