package semantic;
public class MainClassSymbol extends ClassSymbol {
    public MainClassSymbol( String id, int line, int col ) {
        super( id, line, col );
    }

    public SymbolFunction function() {
        return SymbolFunction.MAINCLASS;
    }
}
