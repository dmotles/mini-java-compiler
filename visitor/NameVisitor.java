package visitor;

import syntaxtree.*;
import visitor.symbol.*;

/**
This class builds the symbol table and finds multiply declared identifiers in a 
given scope.
*/
public class NameVisitor implements Visitor {
	private SymbolTable symbolTable;
	private boolean wasError = false;

	public NameVisitor(SymbolTable s) {
		symbolTable = s;
	}

	public boolean hadError() {
		return wasError;
	}

  // MainClass m;
  // ClassDeclList cl;
  public void visit(Program n) {
    n.m.accept(this);

    for ( int i = 0; i < n.cl.size(); i++ ) {
        n.cl.elementAt(i).accept(this);
    }
  }
  
  // Identifier i1,i2;
  // Statement s;
  public void visit(MainClass n) {
	
	ClassSymbol c = new ClassSymbol(n.i1);
	symbolTable.addSymbol(c);
	
	symbolTable.enterScope(n.i1);
	
	Identifier main = new Identifier("main");
	symbolTable.enterScope(main);
    
	n.i1.accept(this);
	
	MethodSymbol m = new MethodSymbol(new IdentifierType("void"), main);
	m.addParameter(new VariableSymbol(new IdentifierType("String []"), n.i2));
	
	c.addMethod(m);

	n.i2.accept(this);
    n.s.accept(this);
	
	symbolTable.leaveScope();
    symbolTable.leaveScope();
  }
  
  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclSimple n) {
	
    ClassSymbol c = new ClassSymbol(n.i);
	symbolTable.addSymbol(c);
	n.i.accept(this);
    if(symbolTable.enterScope(n.i)) {
		System.out.printf("Multiply defined identifier %s at line %d, character %d\n", 
								n.i, 
								n.i.getLineNumber(),
								n.i.getCharNumber());
		wasError = true;
	}
	
	for ( int i = 0; i < n.vl.size(); i++ ) {
        VarDecl v = (VarDecl)n.vl.elementAt(i);
		c.addVariable(new VariableSymbol(v.t,v.i));
		v.accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
		MethodDecl m = (MethodDecl)n.ml.elementAt(i);
		MethodSymbol ms = new MethodSymbol(m.t,m.i);
		c.addMethod(ms);
		FormalList fl = m.fl;
		for(int j=0;j<fl.size();j++) {
			Formal f = (Formal)fl.elementAt(j);
			ms.addParameter(new VariableSymbol(f.t, f.i));
		}
        n.ml.elementAt(i).accept(this);
    }
	symbolTable.leaveScope();
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclExtends n) {
	ClassSymbol c = new ClassSymbol(n.i, n.j);
	symbolTable.addSymbol(c);

	if(symbolTable.enterScope(n.i)) {
		System.out.printf("Multiply defined identifier %s at line %d, character %d\n", 
								n.i, 
								n.i.getLineNumber(),
								n.i.getCharNumber());
		wasError = true;
	}
	n.i.accept(this);
    n.j.accept(this);
    for ( int i = 0; i < n.vl.size(); i++ ) {
        VarDecl v = (VarDecl)n.vl.elementAt(i);
		c.addVariable(new VariableSymbol(v.t,v.i));
		v.accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        MethodDecl m = (MethodDecl)n.ml.elementAt(i);
		MethodSymbol ms = new MethodSymbol(m.t,m.i);
		c.addMethod(ms);
		FormalList fl = m.fl;
		for(int j=0;j<fl.size();j++) {
			Formal f = (Formal)fl.elementAt(j);
			ms.addParameter(new VariableSymbol(f.t, f.i));
		}
		n.ml.elementAt(i).accept(this);
    }
	symbolTable.leaveScope();
  }

  // Type t;
  // Identifier i;
  public void visit(VarDecl n) {
    n.t.accept(this);
    n.i.accept(this);
	VariableSymbol v = new VariableSymbol(n.t, n.i);
	if(symbolTable.isDefined(n.i)) {
		System.out.printf("Multiply defined identifier %s at line %d, character %d\n", 
								n.i, 
								n.i.getLineNumber(),
								n.i.getCharNumber());
		wasError = true;
	}
	
	symbolTable.addSymbol(v);
  }

  // Type t;
  // Identifier i;
  // FormalList fl;
  // VarDeclList vl;
  // StatementList sl;
  // Exp e;
  public void visit(MethodDecl n) {
	
    n.t.accept(this);
    n.i.accept(this);

	VariableSymbol thisVar = new VariableSymbol(new IdentifierType(symbolTable.getScopeName().s), new Identifier("this"));

	MethodSymbol ms = new MethodSymbol(n.t,n.i);
	symbolTable.addSymbol(ms);
	
	if(symbolTable.enterScope(n.i)) {
		System.out.printf("Multiply defined identifier %s at line %d, character %d\n", 
								n.i, 
								n.i.getLineNumber(),
								n.i.getCharNumber());
		wasError = true;
	}
	symbolTable.addSymbol(thisVar);
    for ( int i = 0; i < n.fl.size(); i++ ) {
        n.fl.elementAt(i).accept(this);
		Formal f = (Formal)n.fl.elementAt(i);
		ms.addParameter(new VariableSymbol(f.t, f.i));
    }
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.elementAt(i).accept(this);
    }
    n.e.accept(this);
	symbolTable.leaveScope();
  }

  // Type t;
  // Identifier i;
  public void visit(Formal n) {
    VariableSymbol v = new VariableSymbol(n.t, n.i);
	if(symbolTable.isDefined(n.i)) {
		System.out.printf("Multiply defined identifier %s at line %d, character %d\n", 
								n.i, 
								n.i.getLineNumber(),
								n.i.getCharNumber());
		wasError = true;
	}
	
	symbolTable.addSymbol(v);
    n.t.accept(this);
    n.i.accept(this);
  }

  public void visit(IntArrayType n) {
  }

  public void visit(BooleanType n) {
  }

  public void visit(IntegerType n) {
  }

  // String s;
  public void visit(IdentifierType n) {
  }

  // StatementList sl;
  public void visit(Block n) {
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.elementAt(i).accept(this);
    }
  }

  // Exp e;
  // Statement s1,s2;
  public void visit(If n) {
    n.e.accept(this);
    n.s1.accept(this);
    n.s2.accept(this);
  }

  // Exp e;
  // Statement s;
  public void visit(While n) {
    n.e.accept(this);
    n.s.accept(this);
  }

  // Exp e;
  public void visit(Print n) {
    n.e.accept(this);
  }
  
  // Identifier i;
  // Exp e;
  public void visit(Assign n) {
    n.i.accept(this);
    n.e.accept(this);
  }

  // Identifier i;
  // Exp e1,e2;
  public void visit(ArrayAssign n) {

    n.i.accept(this);
    n.e1.accept(this);
    n.e2.accept(this);
  }

  // Exp e1,e2;
  public void visit(And n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  // Exp e1,e2;
  public void visit(LessThan n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  // Exp e1,e2;
  public void visit(Plus n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  // Exp e1,e2;
  public void visit(Minus n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  // Exp e1,e2;
  public void visit(Times n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  // Exp e1,e2;
  public void visit(ArrayLookup n) {
    n.e1.accept(this);
    n.e2.accept(this);
  }

  // Exp e;
  public void visit(ArrayLength n) {
    n.e.accept(this);
  }

  // Exp e;
  // Identifier i;
  // ExpList el;
  public void visit(Call n) {
    n.e.accept(this);
    n.i.accept(this);
    for ( int i = 0; i < n.el.size(); i++ ) {
        n.el.elementAt(i).accept(this);
    }
  }

  // int i;
  public void visit(IntegerLiteral n) {
  }

  public void visit(True n) {
  }

  public void visit(False n) {
  }

  // String s;
  public void visit(IdentifierExp n) { 
	  
	
  }

  public void visit(This n) {
  }

  // Exp e;
  public void visit(NewArray n) {
    n.e.accept(this);
  }

  // Identifier i;
  public void visit(NewObject n) {
	n.i.accept(this);
  }

  // Exp e;
  public void visit(Not n) {
    n.e.accept(this);
  }

  // String s;
  public void visit(Identifier n) {
  }
}
