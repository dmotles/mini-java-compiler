package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class NewArray extends Exp {
  public Exp e;
  
  public NewArray(Exp ae, int l, int c) {
    super( l, c );
    e=ae; 
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
