package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.SymbolVisitor;
import visitor.symbol.Symbol;


public abstract class Type extends ASTNode {
    public abstract void accept(Visitor v);
    public abstract Type accept(TypeVisitor v);
    public abstract String toString();
    public abstract Symbol accept(SymbolVisitor v);
    public abstract boolean equals( Object o );
    public abstract int hashCode();
}
