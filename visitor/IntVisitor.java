package visitor;

import syntaxtree.*;

public interface IntVisitor {
  public int visit(Program n);
  public int visit(MainClass n);
  public int visit(ClassDeclSimple n);
  public int visit(ClassDeclExtends n);
  public int visit(VarDecl n);
  public int visit(MethodDecl n);
  public int visit(Formal n);
  public int visit(IntArrayType n);
  public int visit(BooleanType n);
  public int visit(IntegerType n);
  public int visit(IdentifierType n);
  public int visit(Block n);
  public int visit(If n);
  public int visit(While n);
  public int visit(Print n);
  public int visit(Assign n);
  public int visit(ArrayAssign n);
  public int visit(And n);
  public int visit(LessThan n);
  public int visit(Plus n);
  public int visit(Minus n);
  public int visit(Times n);
  public int visit(ArrayLookup n);
  public int visit(ArrayLength n);
  public int visit(Call n);
  public int visit(IntegerLiteral n);
  public int visit(True n);
  public int visit(False n);
  public int visit(IdentifierExp n);
  public int visit(This n);
  public int visit(NewArray n);
  public int visit(NewObject n);
  public int visit(Not n);
  public int visit(Identifier n);
}
