package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class NewObject extends Exp {
  public Identifier i;
  
  public NewObject(Identifier ai, int l, int c) {
    super( l, c );
    i=ai;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
