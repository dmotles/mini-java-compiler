package semantic;
public enum SymbolFunction {
    CLASS("Class"),
    MAINCLASS("MainClass"),
    METHOD("Method"),
    VAR("Variable");

    String str;

    SymbolFunction( String s ) {
        str = s;
    }
}
