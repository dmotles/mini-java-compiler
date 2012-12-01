package visitor;

import syntaxtree.*;

public interface IRVisit {
  public void visit(Program n);
  public void visit(MainClass n);
  public void visit(ClassDeclSimple n);
  public void visit(ClassDeclExtends n);
  public void visit(VarDecl n);
  public void visit(MethodDecl n);
  public void visit(Formal n);
  public void visit(IntArrayType n);
  public void visit(BooleanType n);
  public void visit(IntegerType n);
  public void visit(IdentifierType n);
  public void visit(Block n);
  public void visit(If n);
  public void visit(While n);
  public void visit(Print n);
  public void visit(Assign n);
  public void visit(ArrayAssign n);

  public String visit(True n);
  public String visit(False n);
  public String visit(Call n);
  public String visit(Identifier n);
  public String visit(IdentifierExp n);
  public String visit(IntegerLiteral n);
  public String visit(Plus n);
  public String visit(Minus n);
  public String visit(Times n);
  public String visit(And n);
  public String visit(LessThan n);
  public String visit(Not n);
  public String visit(NewArray n);
  public String visit(NewObject n);
  public String visit(This n);
  public String visit(ArrayLookup n);
  public String visit(ArrayLength n);
}
