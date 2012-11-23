package visitor;

import syntaxtree.*;
import semantic.*;

public class BuildSymbolTableVisitor implements Visitor {
    private static final String MULTIPLEDEFS =
        "Multiply defined identifier %s at line %d, character %d\n";
    private static final String NODEFS =
        "Use of undefined identifier %s at line %d, character %d\n";

    private SymbolTable symbolTable;

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    private static void duplicateDef( String id, int line, int col ) {
        System.err.printf( MULTIPLEDEFS, id, line, col );
    }
    private static void noDef( String id, int line, int col ) {
        System.err.printf( NODEFS, id, line, col );
    }

    // MainClass m;
    // ClassDeclList cl;
    public void visit(Program n) {
        symbolTable = new SymbolTable();
        n.m.accept(this);
        for ( int i = 0; i < n.cl.size(); i++ ) {
            n.cl.elementAt(i).accept(this);
        }
    }

    // Identifier i1,i2;
    // Statement s;
    public void visit(MainClass n) {
        if( symbolTable.addClass( n.i1.toString(), n.line, n.col ) == null ) {
            duplicateDef( n.i1.toString(), n.line, n.col );
        } else {
            symbolTable.leaveScope();
        }
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclSimple n) {
        if( symbolTable.addClass( n.i.toString(), n.line, n.col ) != null ) {
            for ( int i = 0; i < n.vl.size(); i++ ) {
                n.vl.elementAt(i).accept(this);
            }
            for ( int i = 0; i < n.ml.size(); i++ ) {
                n.ml.elementAt(i).accept(this);
            }
            symbolTable.leaveScope();
        } else {
            duplicateDef( n.i.toString(), n.line, n.col );
        }

    }

    // Identifier i;
    // Identifier j;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclExtends n) {
        if( symbolTable.addClass( n.i.toString(), n.line, n.col, n.j.toString() ) != null ) {
            for ( int i = 0; i < n.vl.size(); i++ ) {
                n.vl.elementAt(i).accept(this);
            }
            for ( int i = 0; i < n.ml.size(); i++ ) {
                n.ml.elementAt(i).accept(this);
            }
            symbolTable.leaveScope();
        } else {
            duplicateDef( n.i.toString(), n.line, n.col );
        }
    }

    // Type t;
    // Identifier i;
    public void visit(VarDecl n) {
        if( symbolTable.addVariable( n.i.s, n.t, n.line, n.col ) == null )
            duplicateDef( n.i.toString(), n.line, n.col );
    }

    // Type t;
    // Identifier i;
    // FormalList fl;
    // VarDeclList vl;
    // StatementList sl;
    // Exp e;
    public void visit(MethodDecl n) {
        MethodSymbol m = symbolTable.addMethod( n.i.toString(),
                n.t,
                n.line,
                n.col );

        if( m != null ) {
            for ( int i = 0; i < n.fl.size(); i++ ) {
                Formal fm = n.fl.elementAt( i );
                m.addParamType( fm.t );
                fm.accept( this );
            }
            for ( int i = 0; i < n.vl.size(); i++ ) {
                n.vl.elementAt(i).accept(this);
            }
            for( int i = 0; i < n.sl.size(); i++ ) {
                n.sl.elementAt(i).accept(this);
            }
            symbolTable.leaveScope();
        } else {
            duplicateDef( n.i.toString(), n.line, n.col );
        }
    }

    // Type t;
    // Identifier i;
    public void visit(Formal n) {
        if( symbolTable.addVariable( n.i.toString(), n.t, n.line, n.col ) == null ) {
            duplicateDef( n.i.toString(), n.line, n.col );
        }
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
    /////////////
    // In order to assign a variable, the symbol _must_ be defined already. Thus, we can
    // check to see if the symbol has been defined yet.
    public void visit(Assign n) {
        if( symbolTable.lookupVariable( n.i.toString() ) == null ) {
            noDef( n.i.toString(), n.line, n.col );
        }
    }

    // Identifier i;
    // Exp e1,e2;
    public void visit(ArrayAssign n) {
        if( symbolTable.lookupVariable( n.i.toString() ) == null ) {
            noDef( n.i.toString(), n.line, n.col );
        }
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
        n.i.accept(this); // we cant really check this ID yet
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
        // cannot be checked with certainty.
    }

    // Exp e;
    public void visit(Not n) {
        n.e.accept(this);
    }

    // String s;
    public void visit(Identifier n) {
    }
}
