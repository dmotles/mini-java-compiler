package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class LessThan extends Exp {
  public Exp e1,e2;
  
  public LessThan(Exp ae1, Exp ae2, int l, int c) {
    super( l, c );
    e1=ae1; e2=ae2;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
