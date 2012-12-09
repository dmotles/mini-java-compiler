package visitor;

import java.util.*;
import ir.*;
import visitor.symbol.*;
import syntaxtree.*;

public class IRVisitor implements SymbolVisitor
{

    SymbolTable symbolTable;

    int	temporaryNumber	= 0;

    public ArrayList<Quadruple> IR = new ArrayList<Quadruple>();

    IR irmap;


    private void reset() {
        temporaryNumber	= 0;
        IR = new ArrayList <Quadruple>();
    }

    public IR getIR() {
        return irmap;
    }


    public IRVisitor(SymbolTable s) {
        symbolTable = s;
        irmap = new IR();

        //Add two "system calls" to the symbol table. We'll call assembly routines for them directly.
        MethodSymbol print = new MethodSymbol(new IdentifierType("void"), new Identifier("System.out.println"));
        print.addParameter(new VariableSymbol(new IntegerType(), new Identifier("x")));
        MethodSymbol exit = new MethodSymbol(new IdentifierType("void"), new Identifier("System.exit"));
        symbolTable.addSymbol(print);
        symbolTable.addSymbol(exit);
    }

    // MainClass m;
    // ClassDeclList cl;
    public Symbol visit(Program n) {
        n.m.accept (this);
        for	(int i = 0;	i <	n.cl.size (); i++) {
            n.cl.elementAt(i).accept(this);
        }
        return null;
    }

    // Identifier i1,i2;
    // Statement s;
    public Symbol visit(MainClass n) {
        symbolTable.enterScope(n.i1);
        irmap.addMainClass( n.i1 );

        Identifier main = new Identifier("main");
        symbolTable.enterScope(main);

        n.i1.accept(this);
        n.i2.accept(this);

        n.s.accept(this);

        IR.add(new CallQuadruple(symbolTable.getSymbol(new Identifier("System.exit")), null, true));

        irmap.addMainMethod( main, IR );
        reset();

        symbolTable.leaveScope();
        symbolTable.leaveScope();

        return null;
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public Symbol visit (ClassDeclSimple n)
    {
        symbolTable.enterScope(n.i);
        irmap.addClass( n.i );

        n.i.accept (this);
        for	(int i = 0;	i <	n.vl.size (); i++) {
            n.vl.elementAt (i).accept (this);
        }
        for	(int i = 0;	i <	n.ml.size (); i++) {
            n.ml.elementAt (i).accept (this);
        }

        symbolTable.leaveScope();

        return null;
    }

    // Identifier i;
    // Identifier j;
    // VarDeclList vl;
    // MethodDeclList ml;
    public Symbol visit(ClassDeclExtends n) {
        symbolTable.enterScope(n.i);
        irmap.addClass( n.i );

        n.i.accept(this);
        n.j.accept(this);
        for	(int i = 0;	i <	n.vl.size (); i++) {
            n.vl.elementAt(i).accept(this);
        }
        for	(int i = 0;	i <	n.ml.size (); i++) {
            n.ml.elementAt(i).accept(this);
        }

        symbolTable.leaveScope();

        return null;
    }

    // Type	t;
    // Identifier i;
    public Symbol visit (VarDecl n) {
        n.t.accept (this);
        n.i.accept (this);

        return null;
    }

    // Type	t;
    // Identifier i;
    // FormalList fl;
    // VarDeclList vl;
    // StatementList sl;
    // Exp e;
    public Symbol visit (MethodDecl n)
    {
        Identifier cName = symbolTable.getScopeName();

        symbolTable.enterScope(n.i);

        n.t.accept (this);
        n.i.accept (this);
        for	(int i = 0;	i <	n.fl.size (); i++)
        {
            n.fl.elementAt (i).accept (this);
        }
        for	(int i = 0;	i <	n.vl.size (); i++)
        {
            n.vl.elementAt (i).accept (this);
        }
        for	(int i = 0;	i <	n.sl.size (); i++)
        {
            n.sl.elementAt (i).accept (this);
        }

        Symbol ret = n.e.accept (this);
        IR.add(new ReturnQuadruple(ret));

        irmap.addMethod( cName, n.i, IR );
        reset();

        symbolTable.leaveScope();

        return null;
    }

    // Type	t;
    // Identifier i;
    public Symbol visit (Formal n)
    {
        n.t.accept (this);
        n.i.accept (this);

        return null;
    }

    public Symbol visit (IntArrayType n) {

        return null;
    }

    public Symbol visit (BooleanType n) {
        return null;
    }

    public Symbol visit (IntegerType n) {
        return null;
    }

    // String s;
    public Symbol visit (IdentifierType n) {
        return null;
    }

    // StatementList sl;
    public Symbol visit (Block n) {
        for	(int i = 0;	i <	n.sl.size (); i++) {
            n.sl.elementAt (i).accept (this);
        }
        return null;
    }


    /****************************************************************
      Statements do not yield a value, and so return null
     *****************************************************************/

    // Exp e;
    // Statement s1,s2;
    public Symbol visit (If n) {
        Symbol condition = n.e.accept (this);

        //If I had been nice, an iffalse would have been more useful
        Label l_if = new Label();
        IR.add(new IfQuadruple(condition, l_if));

        //But I wasn't nice (okay, i left it off for simplicity but
        // that made things more complex... sigh)
        //
        //So we'll just switch the order of the code generation and
        // put the if after the else.
        n.s2.accept(this);

        Label l_else = new Label();
        IR.add(new GotoQuadruple(l_else));

        int label = IR.size();
        l_if.backpatch(label);

        n.s1.accept(this);

        label = IR.size();
        l_else.backpatch(label);

        return null;
    }

    // Exp e;
    // Statement s;
    public Symbol visit (While n) {
        int backedge = IR.size();

        Symbol condition = n.e.accept (this);

        Label l_cond = new Label();
        IR.add(new GotoQuadruple(l_cond));



        n.s.accept (this);

        int label = IR.size();
        l_cond.backpatch(label);

        IR.add(new IfQuadruple(condition, new Label(backedge)));

        return null;
    }

    // Exp e;
    public Symbol visit (Print n) {
        Symbol s = n.e.accept (this);

        IR.add(new ParameterQuadruple(s));
        IR.add(new CallQuadruple(symbolTable.getSymbol(new Identifier("System.out.println")),null,true));

        return null;
    }

    // Identifier i;
    // Exp e;
    public Symbol visit (Assign n) {
        Symbol lhs = n.i.accept(this);
        Symbol rhs = n.e.accept(this);
        IR.add(new CopyQuadruple(rhs, lhs));
        return null;
    }

    // Identifier i;
    // Exp e1,e2;
    public Symbol visit (ArrayAssign n) {
        Symbol array = n.i.accept(this);
        Symbol subscript = n.e1.accept(this);
        Symbol rhs = n.e2.accept(this);

        IR.add(new ArrayAssignmentQuadruple(subscript, rhs, array));

        return null;
    }

    /*********************************************************************
      Expressions return the symbol they assign to, which has been added
      to the symbol table
     ***********************************************************************/

    // Exp e1,e2;
    public Symbol visit (And n) {
        Symbol lhs = n.e1.accept(this);
        Symbol rhs = n.e2.accept(this);

        Symbol temp = new VariableSymbol(symbolTable.getType(lhs.getName()), new Identifier("_t" + temporaryNumber++));
        symbolTable.addSymbol(temp);

        IR.add(new AssignmentQuadruple(AssignmentQuadruple.AND, lhs, rhs, temp));
        return temp;
    }

    // Exp e1,e2;
    public Symbol visit (LessThan n) {
        Symbol lhs = n.e1.accept(this);
        Symbol rhs = n.e2.accept(this);

        Symbol temp = new VariableSymbol(symbolTable.getType(lhs.getName()), new Identifier("_t" + temporaryNumber++));
        symbolTable.addSymbol(temp);

        IR.add(new AssignmentQuadruple(AssignmentQuadruple.LT, lhs, rhs, temp));
        return temp;
    }

    // Exp e1,e2;
    public Symbol visit(Plus n) {
        Symbol lhs = n.e1.accept(this);
        Symbol rhs = n.e2.accept(this);

        Symbol temp = new VariableSymbol(symbolTable.getType(lhs.getName()), new Identifier("_t" + temporaryNumber++));
        symbolTable.addSymbol(temp);

        IR.add(new AssignmentQuadruple(AssignmentQuadruple.ADD, lhs, rhs, temp));
        return temp;
    }

    // Exp e1,e2;
    public Symbol visit (Minus n) {
        Symbol lhs = n.e1.accept(this);
        Symbol rhs = n.e2.accept(this);

        Symbol temp = new VariableSymbol(symbolTable.getType(lhs.getName()), new Identifier("_t" + temporaryNumber++));
        symbolTable.addSymbol(temp);

        IR.add(new AssignmentQuadruple(AssignmentQuadruple.SUB, lhs, rhs, temp));
        return temp;
    }

    // Exp e1,e2;
    public Symbol visit (Times n) {
        Symbol lhs = n.e1.accept(this);
        Symbol rhs = n.e2.accept(this);

        Symbol temp = new VariableSymbol(symbolTable.getType(lhs.getName()), new Identifier("_t" + temporaryNumber++));
        symbolTable.addSymbol(temp);

        IR.add(new AssignmentQuadruple(AssignmentQuadruple.MUL, lhs, rhs, temp));
        return temp;
    }

    // Exp e1,e2;
    public Symbol visit (ArrayLookup n) {
        Symbol array = n.e1.accept(this);
        Symbol subscript = n.e2.accept(this);

        Symbol temp = new VariableSymbol(new IntegerType(), new Identifier("_t" + temporaryNumber++));
        symbolTable.addSymbol(temp);

        IR.add(new ArrayLookupQuadruple(array, subscript, temp));

        return temp;
    }

    // Exp e;
    public Symbol visit (ArrayLength n) {
        Symbol array = n.e.accept (this);

        Symbol temp = new VariableSymbol(new IntegerType(), new Identifier("_t" + temporaryNumber++));
        symbolTable.addSymbol(temp);

        IR.add(new ArrayLengthQuadruple(array, temp));

        return temp;
    }

    // Exp e;
    // Identifier i;
    // ExpList el;
    public Symbol visit (Call n) {
        ArrayList<Quadruple> params = new ArrayList<Quadruple>();
        Symbol thisParam = n.e.accept(this);

        //Implicit this parameter is always the first one
        //We can't add it right to IR yet as we will omit other code
        // as we visit other expressions.
        params.add(new ParameterQuadruple(thisParam));

        n.i.accept(this);

        Symbol func = symbolTable.getMethodByName(new Identifier(((IdentifierType)thisParam.getType()).s), n.i);

        for	(int i = 0;	i <	n.el.size(); i++) {
            Symbol param = n.el.elementAt(i).accept(this);
            params.add(new ParameterQuadruple(param));
        }

        IR.addAll(params);

        Symbol temp = new VariableSymbol(func.getType(), new Identifier("_t" + temporaryNumber++));
        symbolTable.addSymbol(temp);

        IR.add(new CallQuadruple(func, temp));

        return temp;
    }

    // int i;
    public Symbol visit (IntegerLiteral n) {
        Symbol temp = new VariableSymbol(new IntegerType(), new Identifier("_t" + temporaryNumber++));
        symbolTable.addSymbol(temp);

        IR.add(new CopyQuadruple(new Constant(new IntegerType(), n.i), temp));
        return temp;
    }

    /**
      I choose to use -1 for true since booleans are opaque in java
      */
    public Symbol visit (True n) {
        Symbol temp = new VariableSymbol(new BooleanType(), new Identifier("_t" + temporaryNumber++));
        symbolTable.addSymbol(temp);

        IR.add(new CopyQuadruple(new Constant(new BooleanType(), -1), temp));
        return temp;
    }

    public Symbol visit (False n) {
        Symbol temp = new VariableSymbol(new BooleanType(), new Identifier("_t" + temporaryNumber++));
        symbolTable.addSymbol(temp);

        IR.add(new CopyQuadruple(new Constant(new BooleanType(), 0), temp));
        return temp;
    }

    // String s;
    public Symbol visit (IdentifierExp n) {
        return symbolTable.getSymbol(new Identifier(n.s));
    }

    public Symbol visit (This n) {
        return symbolTable.getSymbol(new Identifier("this"));
    }

    // Exp e;
    public Symbol visit (NewArray n) {
        Symbol size = n.e.accept (this);

        Symbol temp = new VariableSymbol(new IntArrayType(), new Identifier("_t" + temporaryNumber++));
        symbolTable.addSymbol(temp);

        IR.add(new NewArrayQuadruple(size, temp));

        return temp;
    }

    // Identifier i;
    public Symbol visit (NewObject n) {
        Symbol c = n.i.accept (this);

        Symbol temp = new VariableSymbol(c.getType(), new Identifier("_t" + temporaryNumber++));
        symbolTable.addSymbol(temp);

        IR.add(new NewObjectQuadruple(c, temp));

        return temp;
    }

    // Exp e;
    public Symbol visit (Not n) {
        Symbol rhs = n.e.accept(this);

        Symbol temp = new VariableSymbol(new BooleanType(), new Identifier("_t" + temporaryNumber++));
        symbolTable.addSymbol(temp);

        IR.add(new UnaryAssignmentQuadruple(UnaryAssignmentQuadruple.NOT, rhs, temp));
        return temp;
    }

    // String s;
    public Symbol visit(Identifier n) {
        return symbolTable.getSymbol(n);
    }
}

