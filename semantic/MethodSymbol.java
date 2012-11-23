package semantic;
import java.util.ArrayList;
import syntaxtree.Type;
import syntaxtree.IdentifierType;

public class MethodSymbol extends Symbol {
    ArrayList<SymbolType> paramTypes;
    public MethodSymbol( String id, int line, int col, Type t ) {
        super( id, line, col, SymbolType.get( t ) );
        paramTypes = new ArrayList<SymbolType>();
    }
    public SymbolFunction function() {
        return SymbolFunction.METHOD;
    }
    public boolean addParamType( Type t ) {
        return paramTypes.add( SymbolType.get( t ) );
    }
}
