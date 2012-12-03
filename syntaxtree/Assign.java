package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.SymbolVisitor;
import visitor.symbol.Symbol;

public class Assign extends Statement {
  public Identifier i;
  public Exp e;

  public Assign(Identifier ai, Exp ae) {
    i=ai; e=ae; 
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }

  public Symbol accept(SymbolVisitor v) {
    return v.visit(this);
  }
}

