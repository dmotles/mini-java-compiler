package semantic;
public enum SymbolFunction {
    CLASS("Class"),
    METHOD("Method"),
    VAR("Variable");

    String str;

    SymbolFunction( String s ) {
        str = s;
    }
}
