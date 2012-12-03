package visitor;

import syntaxtree.*;
import visitor.symbol.*;
import java.util.ArrayList;

public class TypeDepthFirstVisitor implements TypeVisitor {
	private SymbolTable symbolTable;
	private boolean wasError = false;

	public TypeDepthFirstVisitor(SymbolTable s) {
		symbolTable = s;
	}

	public boolean hadError() {
		return wasError;
	}

	public boolean equals(Type lhs, Type rhs) {
		if(lhs instanceof IntegerType && rhs instanceof IntegerType) {
			return true;
		}
		else if(lhs instanceof BooleanType && rhs instanceof BooleanType) {
			return true;
		}
		else if (lhs instanceof IntArrayType && rhs instanceof IntArrayType) {
			return true;
		}
		else if (lhs instanceof IdentifierType && rhs instanceof IdentifierType) {
			IdentifierType i1 = (IdentifierType)lhs;
			IdentifierType i2 = (IdentifierType)rhs;

			ClassSymbol c1 = symbolTable.getClassByName(new Identifier(i1.s));
			ClassSymbol c2 = symbolTable.getClassByName(new Identifier(i2.s));

			ClassSymbol current = c2;

			while(current != null) {
				if(((IdentifierType)current.getType()).s.equals(i1.s)) {
					return true;
				}

				current = symbolTable.getClassByName(new Identifier(((IdentifierType)current.getBaseClass()).s));
			}

		}
		return false;
	}

  // MainClass m;
  // ClassDeclList cl;
  public Type visit(Program n) {
    n.m.accept(this);
    for ( int i = 0; i < n.cl.size(); i++ ) {
        n.cl.elementAt(i).accept(this);
    }
    return null;
  }
  
  // Identifier i1,i2;
  // Statement s;
  public Type visit(MainClass n) {
	symbolTable.enterScope(n.i1);
    
	Identifier main = new Identifier("main");
	symbolTable.enterScope(main);
	
	n.i1.accept(this);
    n.i2.accept(this);
    n.s.accept(this);

	symbolTable.leaveScope();
	symbolTable.leaveScope();
    return null;
  }
  
  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public Type visit(ClassDeclSimple n) {
    symbolTable.enterScope(n.i);
	n.i.accept(this);
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        n.ml.elementAt(i).accept(this);
    }
	symbolTable.leaveScope();
    return null;
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public Type visit(ClassDeclExtends n) {
    
	ClassSymbol derived = symbolTable.getClassByName(n.i);
	ClassSymbol base = symbolTable.getClassByName(n.j);
	if(derived == null) {
		System.out.printf("Use of undefined identifier %s at line %d, character %d\n", 
								n.i, 
								n.i.getLineNumber(),
								n.i.getCharNumber());
		wasError = true;
	}
	else if(base == null) {
		System.out.printf("Use of undefined identifier %s at line %d, character %d\n", 
								n.j, 
								n.j.getLineNumber(),
								n.j.getCharNumber());
		wasError = true;
	}
	else {
		derived.extendsClass(base);
	}
	
	symbolTable.enterScope(n.i);

	for(MethodSymbol m: base.getMethods()) {
		symbolTable.addSymbol(m);
	}

	for(VariableSymbol b: base.getVariables()) {
		symbolTable.addSymbol(b);
	}
	
	n.i.accept(this);
    n.j.accept(this);
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        n.ml.elementAt(i).accept(this);
    }
	symbolTable.leaveScope();
    return null;
  }

  // Type t;
  // Identifier i;
  public Type visit(VarDecl n) {
    n.t.accept(this);
    n.i.accept(this);
    return null;
  }

  // Type t;
  // Identifier i;
  // FormalList fl;
  // VarDeclList vl;
  // StatementList sl;
  // Exp e;
  public Type visit(MethodDecl n) {
    symbolTable.enterScope(n.i);
	n.t.accept(this);
    n.i.accept(this);
    for ( int i = 0; i < n.fl.size(); i++ ) {
        n.fl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.elementAt(i).accept(this);
    }
    n.e.accept(this);
	symbolTable.leaveScope();
    return null;
  }

  // Type t;
  // Identifier i;
  public Type visit(Formal n) {
    n.t.accept(this);
    n.i.accept(this);
    return null;
  }

  public Type visit(IntArrayType n) {
    return n;
  }

  public Type visit(BooleanType n) {
    return n;
  }

  public Type visit(IntegerType n) {
    return n;
  }

  // String s;
  public Type visit(IdentifierType n) {
    return n;
  }

  // StatementList sl;
  public Type visit(Block n) {
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.elementAt(i).accept(this);
    }
    return null;
  }

  // Exp e;
  // Statement s1,s2;
  public Type visit(If n) {
    Type condType = n.e.accept(this);
	if(!(condType instanceof BooleanType)) {
		System.out.printf("Non-boolean expression used as the condition of %s statement at line %d, character %d\n", "if", n.e.getLineNumber(), n.e.getCharNumber());
		wasError = true;
	}
    n.s1.accept(this);
    n.s2.accept(this);
    return null;
  }

  // Exp e;
  // Statement s;
  public Type visit(While n) {
    Type condType = n.e.accept(this);
	if(!(condType instanceof BooleanType)) {
		System.out.printf("Non-boolean expression used as the condition of %s statement at line %d, character %d\n", "while", n.e.getLineNumber(), n.e.getCharNumber());
		wasError = true;
	}
    n.s.accept(this);
    return null;
  }

  // Exp e;
  public Type visit(Print n) {
    if(!(n.e.accept(this) instanceof IntegerType)) {
		System.out.printf("Call of method %s does not match its declared signature at line %d, character %d\n", "System.out.println", n.getLineNumber(), n.getCharNumber());
		wasError = true;
	}
    return null;
  }
  


  // Identifier i;
  // Exp e;
  public Type visit(Assign n) {
	  
	if(!symbolTable.isDefined(n.i)) {
		System.out.printf("Use of undefined identifier %s at line %d, character %d\n", 
								n.i, 
								n.i.getLineNumber(),
								n.i.getCharNumber());
		wasError = true;
	}

    Type lhs = n.i.accept(this);
    Type rhs = n.e.accept(this);
	if(!equals(lhs, rhs)) {
		System.out.printf("Type mismatch during assignment at line %d, character %d\n", n.getLineNumber(), n.getCharNumber());
		wasError = true;
	}

    return null;
  }

  // Identifier i;
  // Exp e1,e2;
  public Type visit(ArrayAssign n) {
	  
	if(!symbolTable.isDefined(n.i)) {
		System.out.printf("Use of undefined identifier %s at line %d, character %d\n", 
								n.i, 
								n.i.getLineNumber(),
								n.i.getCharNumber());
		wasError = true;
	}

    n.i.accept(this);
    n.e1.accept(this);
    n.e2.accept(this);
    return null;
  }

  // Exp e1,e2;
  public Type visit(And n) {
    Type lhs = n.e1.accept(this);
    Type rhs = n.e2.accept(this);
	if(!(lhs instanceof BooleanType) || !(rhs instanceof BooleanType)) {
		System.out.printf("Attempt to use boolean operator %s on non-boolean operands at line %d, character %d\n", 
			"&&",
			n.getLineNumber(),
			n.getCharNumber());
		wasError = true;
	}
    return new BooleanType();
  }

  // Exp e1,e2;
  public Type visit(LessThan n) {
    Type lhs = n.e1.accept(this);
    Type rhs = n.e2.accept(this);
	if(!(lhs instanceof IntegerType) || !(rhs instanceof IntegerType)) {
		System.out.printf("Non-integer operand for operator %c at line %d, character %d\n", 
			'<',
			n.getLineNumber(),
			n.getCharNumber());
		wasError = true;
	}
    return new BooleanType();
  }

  // Exp e1,e2;
  public Type visit(Plus n) {
    Type lhs = n.e1.accept(this);
    Type rhs = n.e2.accept(this);
	if(!(lhs instanceof IntegerType) || !(rhs instanceof IntegerType)) {
		System.out.printf("Non-integer operand for operator %c at line %d, character %d\n", 
			'+',
			n.getLineNumber(),
			n.getCharNumber());
		wasError = true;
	}
    return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(Minus n) {
    Type lhs = n.e1.accept(this);
    Type rhs = n.e2.accept(this);
	if(!(lhs instanceof IntegerType) || !(rhs instanceof IntegerType)) {
		System.out.printf("Non-integer operand for operator %c at line %d, character %d\n", 
			'-',
			n.getLineNumber(),
			n.getCharNumber());
		wasError = true;
	}
    return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(Times n) {
    Type lhs = n.e1.accept(this);
    Type rhs = n.e2.accept(this);
	if(!(lhs instanceof IntegerType) || !(rhs instanceof IntegerType)) {
		System.out.printf("Non-integer operand for operator %c at line %d, character %d\n", 
			'*',
			n.getLineNumber(),
			n.getCharNumber());
		wasError = true;
	}
    return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(ArrayLookup n) {
    n.e1.accept(this);
    n.e2.accept(this);
    return new IntegerType();
  }

  // Exp e;
  public Type visit(ArrayLength n) {
    Type t = n.e.accept(this);
	if(!(t instanceof IntArrayType)) {
		System.out.printf("Length property only applies to arrays, line %d, character %d\n", 
			n.getLineNumber(),
			n.getCharNumber());
		wasError = true;
	}
    return new IntegerType();
  }

  // Exp e;
  // Identifier i;
  // ExpList el;
  public Type visit(Call n) {
	Type returnType = null;
	ArrayList <VariableSymbol> parameters = null;
    
	Type t = n.e.accept(this);

	if(!(t instanceof IdentifierType)) {
		System.out.printf("Attempt to call a non-method at line %d, character %d\n",
			n.i.getLineNumber(),
			n.i.getCharNumber());
		wasError = true;
	}
	else {
		IdentifierType it = (IdentifierType)t;

		MethodSymbol m = symbolTable.getMethodByName(new Identifier(it.s),n.i);

		if(m == null) {
			System.out.printf("Use of undefined identifier %s at line %d, character %d\n",
				n.i,
				n.i.getLineNumber(),
				n.i.getCharNumber());
			wasError = true;
		}
		else {
			returnType = m.getType();
			parameters = m.getParameters();
		}
	}

    n.i.accept(this);

	int numParams = parameters == null ? 0 : parameters.size();

	if(numParams != n.el.size()) {
		System.out.printf("Call of method %s does not match its declared number of arguments at line %d, character %d\n",
			n.i,
			n.i.getLineNumber(),
			n.i.getCharNumber());
		wasError = true;
		return returnType;
	}

	for ( int i = 0; i < n.el.size(); i++ ) {
        Type actual = n.el.elementAt(i).accept(this);
		Symbol s = parameters.get(i);

		if(s==null || !equals(s.getType(), actual)) {
			System.out.printf("Call of method %s does not match its declared signature at line %d, character %d\n",
				n.i,
				n.i.getLineNumber(),
				n.i.getCharNumber());
			wasError = true;
		}
    }
    return returnType;
  }

  // int i;
  public Type visit(IntegerLiteral n) {
    return new IntegerType();
  }

  public Type visit(True n) {
    return new BooleanType();
  }

  public Type visit(False n) {
    return new BooleanType();
  }

  // String s;
  public Type visit(IdentifierExp n) {
	if(!symbolTable.isDefined(new Identifier(n.s))) {
		System.out.printf("Use of undefined identifier %s at line %d, character %d\n", 
								n.s, 
								n.getLineNumber(),
								n.getCharNumber());
		wasError = true;
	}
    return symbolTable.getType(new Identifier(n.s));
  }

  public Type visit(This n) {
    return symbolTable.getType(new Identifier("this"));
  }

  // Exp e;
  public Type visit(NewArray n) {
    n.e.accept(this);
    return new IntArrayType();
  }

  // Identifier i;
  public Type visit(NewObject n) {
    return new IdentifierType(n.i.s);
  }

  // Exp e;
  public Type visit(Not n) {
    if(!(n.e.accept(this) instanceof BooleanType)) {
		System.out.printf("Attempt to use boolean operator %s on non-boolean operands at line %d, character %d\n", "!", n.getLineNumber(), n.getCharNumber());
		wasError = true;
	}
    return new BooleanType();
  }

  // String s;
  public Type visit(Identifier n) {
    return symbolTable.getType(n);
  }
}
