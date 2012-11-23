package semantic;

public abstract class Symbol {
    public String id;
    public int line;
    public int col;
    public SymbolType type;
    public abstract SymbolFunction function();

    public Symbol( String id, int line, int col, SymbolType t ) {
        this.id = id;
        this.line = line;
        this.col = col;
        this.type = t;
    }

    public boolean equals( Object o ) {
        if( o instanceof Symbol ) {
            Symbol sym = (Symbol)o;
            if( id.intern().equals( sym.id.intern() ) &&
                    function() == sym.function() &&
                    type.equals(sym.type) &&
                    line == sym.line &&
                    col == sym.col )
            {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return String.format("%s(line:%d,col%d,func:%s,type:%s)", id,
                line,
                col,
                function().str,
                type.toString()
                );
    }
}
