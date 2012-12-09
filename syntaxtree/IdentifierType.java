package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.SymbolVisitor;
import visitor.symbol.Symbol;


public class IdentifierType extends Type {
    public String s;

    public IdentifierType(String as) {
        s=as;
    }

    public void accept(Visitor v) {
        v.visit(this);
    }

    public Type accept(TypeVisitor v) {
        return v.visit(this);
    }

    public String toString() {
        return s;
    }

    public Symbol accept(SymbolVisitor v) {
        return v.visit(this);
    }
    public boolean equals( Object o ) {
        if (o instanceof IdentifierType ) {
            return s.equals( ((IdentifierType)o).s );
        }
        return false;
    }

    public int hashCode() {
        return s.hashCode();
    }
}
