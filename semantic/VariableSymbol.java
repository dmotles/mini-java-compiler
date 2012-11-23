package semantic;
import syntaxtree.Type;
import syntaxtree.IdentifierType;
public class VariableSymbol extends Symbol {
    public VariableSymbol( String id, int line, int col, Type t ) {
        super( id, line, col, SymbolType.get(t) );
    }

    public SymbolFunction function() {
        return SymbolFunction.VAR;
    }
}
