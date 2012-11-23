package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class True extends Exp {
    public True( int l , int c ) {
        super( l,c );
    }
  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
