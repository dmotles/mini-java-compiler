package semantic;
import java.util.HashMap;
import syntaxtree.Type;
import syntaxtree.IntegerType;
import syntaxtree.IntArrayType;
import syntaxtree.BooleanType;
import syntaxtree.IdentifierType;
public class SymbolType {
    public static final SymbolType INT = new SymbolType( "int", new AstType() {
        public Type convert(String s, int line, int col) { return new IntegerType(line,col); }
    });
    public static final SymbolType INTARRAY = new SymbolType( "int[]", new AstType() {
        public Type convert(String s, int line, int col) { return new IntArrayType(line,col); }
    });
    public static final SymbolType BOOL = new SymbolType( "boolean", new AstType() {
        public Type convert(String s, int line, int col) { return new BooleanType(line,col); }
    });
    private static final HashMap<String,SymbolType> cache = new HashMap<String,SymbolType>();

    private static abstract class AstType {
        public abstract Type convert( String s, int line, int col );
    }

    private AstType type;
    private String str;

    private SymbolType( String s, AstType a ) {
        str = s;
        type = a;
    }

    public boolean equals( Object o ) {
        if( o != null && o instanceof SymbolType ) {
            SymbolType other = (SymbolType)o;
            return str.equals(other.str);
        }
        return false;
    }

    public String toString() {
        return str;
    }

    public static SymbolType get( Type t ) {
        if( t instanceof IntegerType )
            return INT;
        else if ( t instanceof IntArrayType )
            return INTARRAY;
        else if ( t instanceof BooleanType )
            return BOOL;
        else if ( t instanceof IdentifierType ) {
            IdentifierType it = (IdentifierType)t;
            return getIdType( it.s );
        }
        return null;
    }

    public static SymbolType getIdType( String s ) {
        SymbolType idsymtype = cache.get( s.intern() );
        if( idsymtype == null ) {
            idsymtype = new SymbolType( s, new AstType() {
                public Type convert( String s, int line, int col) {
                    return new IdentifierType( s,line,col); }
            } );
            cache.put( s.intern(), idsymtype );
        }
        return idsymtype;
    }

    public Type getAstType( int line, int col ) {
        return type.convert( str, line, col );
    }
}
