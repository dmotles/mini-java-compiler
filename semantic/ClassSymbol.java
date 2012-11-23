package semantic;
public class ClassSymbol extends Symbol {
    SymbolType extendsClass;
    public ClassSymbol( String id, int line, int col ) {
        super( id, line, col, SymbolType.getIdType( id ) );
        extendsClass = null;
    }
    public ClassSymbol( String id, int line, int col, String e ) {
        this( id, line, col );
        extendsClass = SymbolType.getIdType( e );
    }

    public SymbolFunction function() {
        return SymbolFunction.CLASS;
    }

    public SymbolType extendsClass() {
        return extendsClass;
    }
}
