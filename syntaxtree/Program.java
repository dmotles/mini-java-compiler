package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class Program {
  public MainClass m;
  public ClassDeclList cl;

  public Program(MainClass am, ClassDeclList acl) {
    m=am; cl=acl; 
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
