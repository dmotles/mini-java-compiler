package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.SymbolVisitor;
import visitor.symbol.Symbol;

public abstract class Exp extends ASTNode {
  public abstract void accept(Visitor v);
  public abstract Type accept(TypeVisitor v);
  
  public abstract Symbol accept(SymbolVisitor v);
}
