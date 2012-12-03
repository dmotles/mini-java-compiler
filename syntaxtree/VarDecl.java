package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.SymbolVisitor;
import visitor.symbol.Symbol;


public class VarDecl extends ASTNode {
  public Type t;
  public Identifier i;
  
  public VarDecl(Type at, Identifier ai) {
    t=at; i=ai;
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
