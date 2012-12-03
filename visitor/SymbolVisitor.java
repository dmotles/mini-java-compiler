package visitor;

import syntaxtree.*;
import visitor.symbol.Symbol;

public interface SymbolVisitor {
  public Symbol visit(Program n);
  public Symbol visit(MainClass n);
  public Symbol visit(ClassDeclSimple n);
  public Symbol visit(ClassDeclExtends n);
  public Symbol visit(VarDecl n);
  public Symbol visit(MethodDecl n);
  public Symbol visit(Formal n);
  public Symbol visit(IntArrayType n);
  public Symbol visit(BooleanType n);
  public Symbol visit(IntegerType n);
  public Symbol visit(IdentifierType n);
  public Symbol visit(Block n);
  public Symbol visit(If n);
  public Symbol visit(While n);
  public Symbol visit(Print n);
  public Symbol visit(Assign n);
  public Symbol visit(ArrayAssign n);
  public Symbol visit(And n);
  public Symbol visit(LessThan n);
  public Symbol visit(Plus n);
  public Symbol visit(Minus n);
  public Symbol visit(Times n);
  public Symbol visit(ArrayLookup n);
  public Symbol visit(ArrayLength n);
  public Symbol visit(Call n);
  public Symbol visit(IntegerLiteral n);
  public Symbol visit(True n);
  public Symbol visit(False n);
  public Symbol visit(IdentifierExp n);
  public Symbol visit(This n);
  public Symbol visit(NewArray n);
  public Symbol visit(NewObject n);
  public Symbol visit(Not n);
  public Symbol visit(Identifier n);
}
