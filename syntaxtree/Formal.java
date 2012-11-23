package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class Formal {
  public Type t;
  public Identifier i;
  public int line, col;

  public Formal(Type at, Identifier ai, int l, int c) {
      line = l; col = c;
    t=at; i=ai;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
