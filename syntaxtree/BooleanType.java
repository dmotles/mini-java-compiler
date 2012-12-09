package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.SymbolVisitor;
import visitor.symbol.Symbol;

public class BooleanType extends Type {
    public void accept(Visitor v) {
        v.visit(this);
    }

    public Type accept(TypeVisitor v) {
        return v.visit(this);
    }

    public String toString() {
        return "bool";
    }

    public Symbol accept(SymbolVisitor v) {
        return v.visit(this);
    }

    public boolean equals( Object o ) {
        return (o instanceof BooleanType );
    }

    public int hashCode() {
        return "bool".hashCode();
    }
}
