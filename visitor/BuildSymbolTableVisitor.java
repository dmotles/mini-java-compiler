package visitor;

import syntaxtree.*;
import semantic.*;
import java.util.PriorityQueue;

public class BuildSymbolTableVisitor implements Visitor {
    private static final String MULTIPLEDEFS =
        "Multiply defined identifier %s at line %d, character %d";
    private static final String NODEFS =
        "Use of undefined identifier %s at line %d, character %d";

    private SymbolTable symbolTable;
    private PriorityQueue<NameAnalysisIssue> issues;


    private class NameAnalysisIssue implements Comparable<NameAnalysisIssue> {
        private String msg;
        private int line;
        private int col;
        public NameAnalysisIssue( String msg, int line, int col ){
            this.msg = msg;
            this.line = line;
            this.col = col;
        }

        public int compareTo( NameAnalysisIssue o ) {
            int diff = line - o.line;
            if( diff == 0 )
                diff = col - o.col;
            return diff;
        }

        public String toString() {
            return msg;
        }
    }

    public SymbolTable getLastSymbolTable() {
        return symbolTable;
    }

    private void duplicateDef( String id, int line, int col ) {
        String err = String.format( MULTIPLEDEFS, id, line, col );
        issues.offer( new NameAnalysisIssue(err,line,col) );
    }
    private void noDef( String id, int line, int col ) {
        String err = String.format( NODEFS, id, line, col );
        issues.offer( new NameAnalysisIssue(err,line,col) );
    }

    // MainClass m;
    // ClassDeclList cl;
    public void visit(Program n) {
        symbolTable = new SymbolTable();
        issues = new PriorityQueue<NameAnalysisIssue>();
        n.m.accept(this);
        for ( int i = 0; i < n.cl.size(); i++ ) {
            n.cl.elementAt(i).accept(this);
        }

        checkIssues( "Symbol Table Construction" );

        CheckUndefinedVisitor udef = new CheckUndefinedVisitor( );
        n.accept( udef );

        checkIssues( "Name Analysis" );

    }

    private void checkIssues( String phase ) {
        if( issues.size() > 0 ) {
            while( issues.size() > 0 )
                System.err.println( issues.poll() );
            throw new NameAnalysisException( "Errors detected during " + phase + " phase." );
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


    private class CheckUndefinedVisitor implements Visitor {
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
            symbolTable.enterScope( n.i1.toString() );
            n.s.accept(this);
            symbolTable.leaveScope();
        }

        // Identifier i;
        // VarDeclList vl;
        // MethodDeclList ml;
        public void visit(ClassDeclSimple n) {
            symbolTable.enterScope( n.i.toString() );
            for ( int i = 0; i < n.vl.size(); i++ ) {
                n.vl.elementAt(i).accept(this);
            }
            for ( int i = 0; i < n.ml.size(); i++ ) {
                n.ml.elementAt(i).accept(this);
            }
            symbolTable.leaveScope();
        }

        // Identifier i;
        // Identifier j;
        // VarDeclList vl;
        // MethodDeclList ml;
        public void visit(ClassDeclExtends n) {
            if( symbolTable.lookupClass( n.j.toString() ) == null ) {
                noDef( n.j.toString(), n.j.line, n.j.col );
            }
            symbolTable.enterScope( n.i.toString() );
            for ( int i = 0; i < n.vl.size(); i++ ) {
                n.vl.elementAt(i).accept(this);
            }
            for ( int i = 0; i < n.ml.size(); i++ ) {
                n.ml.elementAt(i).accept(this);
            }
            symbolTable.leaveScope();
        }

        // Type t;
        // Identifier i;
        public void visit(VarDecl n) {
            n.t.accept( this );
        }

        // Type t;
        // Identifier i;
        // FormalList fl;
        // VarDeclList vl;
        // StatementList sl;
        // Exp e;
        public void visit(MethodDecl n) {
            n.t.accept(this);
            symbolTable.enterScope( n.i.toString() );
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
        }

        // Type t;
        // Identifier i;
        public void visit(Formal n) {
            n.t.accept( this );
        }

        public void visit(IntArrayType n) {
        }

        public void visit(BooleanType n) {
        }

        public void visit(IntegerType n) {
        }

        // String s;
        public void visit(IdentifierType n) {
            if( symbolTable.lookupClass( n.s ) == null ) {
                noDef( n.s, n.line, n.col );
            }
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
                noDef( n.i.toString(), n.i.line, n.i.col );
            }
            n.e.accept( this );
        }

        // Identifier i;
        // Exp e1,e2;
        public void visit(ArrayAssign n) {
            if( symbolTable.lookupVariable( n.i.toString() ) == null ) {
                noDef( n.i.toString(), n.i.line, n.i.col );
            }
            n.e1.accept( this );
            n.e2.accept( this );
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
            if( ! symbolTable.methodExistsSomewhere( n.i.s ) )
                noDef( n.i.s, n.i.line, n.i.col );
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
            if( symbolTable.lookupVariable( n.s ) == null ) {
                noDef( n.s, n.line, n.col );
            }
        }

        public void visit(This n) {
        }

        // Exp e;
        public void visit(NewArray n) {
            n.e.accept(this);
        }

        // Identifier i;
        public void visit(NewObject n) {
            if( symbolTable.lookupClass( n.i.toString() ) == null ) {
                noDef( n.i.toString(), n.line, n.col );
            }
        }

        // Exp e;
        public void visit(Not n) {
            n.e.accept(this);
        }

        // String s;
        public void visit(Identifier n) {
        }

    }
}
