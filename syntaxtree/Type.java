package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public abstract class Type {
  public int line, col;
  Type( int l, int c ) {
    line = l; col = c;
  }
  public abstract void accept(Visitor v);
  public abstract Type accept(TypeVisitor v);
}
