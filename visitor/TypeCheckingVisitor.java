package visitor;

import java.util.*;
import syntaxtree.*;
import semantic.*;

public class TypeCheckingVisitor implements TypeVisitor {
    private static final String BAD_LVALUE = "Invalid l-value, %s is a %s, at line %d, character %d";
    private static final String BAD_RVALUE = "Invalid r-value: %s is a %s, at line %d, character %d";
    private static final String NON_METHOD = "Attempt to call a non-method at line %d, character %d";
    private static final String BAD_ARG_COUNT = "Call of method %s does not match its declared number of arguments at line %d, character %d";
    private static final String BAD_ARG_TYPE = "Call of method %s does not match its declared signature at line %d, character %d";
    private static final String NON_INT = "Non-integer operand for operator %c at line %d, character %d";
    private static final String NON_BOOL = "Attempt to use boolean operator %s on non-boolean operands at line %d, character %d";
    private static final String NON_ARRAY = "Length property only applies to arrays, line %d, character %d";
    private static final String CONDITION_NEEDS_BOOL = "Non-boolean expression used as the condition of %s statement at line %d, character %d";
    private static final String TYPE_MISMATCH = "Type mismatch during assignment at line %d, character %d";
    private static final String THIS_INSIDE_STATIC = "Illegal use of keyword ‘this’ in static method at line %d, character %d";

    private SymbolTable symbolTable;
    private PriorityQueue<TypeCheckingIssue> issues;

    private class TypeCheckingIssue implements Comparable<TypeCheckingIssue> {
        private String msg;
        private int line;
        private int col;
        private TypeCheckingIssue( int line, int col ) {
            this.line = line;
            this.col = col;
        }
        public TypeCheckingIssue( String err_str, int line, int col ){
            this( line, col );
            this.msg = String.format( err_str, line, col );
        }
        public TypeCheckingIssue( String err_str, String id, int line, int col ){
            this( line, col );
            this.msg = String.format( err_str, id, line, col );
        }
        public TypeCheckingIssue( String err_str, char c, int line, int col ){
            this( line, col );
            this.msg = String.format( err_str, c, line, col );
        }

        public int compareTo( TypeCheckingIssue o ) {
            int diff = line - o.line;
            if( diff == 0 )
                diff = col - o.col;
            return diff;
        }

        public String toString() {
            return msg;
        }
    }

    public TypeCheckingVisitor( SymbolTable s ) {
        symbolTable = s;
        issues = new PriorityQueue<TypeCheckingIssue>();
    }


    // MainClass m;
    // ClassDeclList cl;
    public Type visit(Program n) {
        n.m.accept(this);
        for ( int i = 0; i < n.cl.size(); i++ ) {
            n.cl.elementAt(i).accept(this);
        }
        checkIssues( "Type Checking" );
        return null;
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
    public Type visit(MainClass n) {
        symbolTable.enterScope( n.i1.s );
        n.i1.accept(this);
        n.i2.accept(this);
        n.s.accept(this);
        symbolTable.leaveScope();
        return null;
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public Type visit(ClassDeclSimple n) {
        symbolTable.enterScope( n.i.s );
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
        symbolTable.enterScope( n.i.s );
        n.i.accept(this);
        n.j.accept(this);
        for ( int i = 0; i < n.vl.size(); i++ ) {
            n.vl.elementAt(i).accept(this);
        }
        for ( int i = 0; i < n.ml.size(); i++ ) {
            n.ml.elementAt(i).accept(this);
        }
        symbolTable.enterScope( n.i.s );
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
        n.t.accept(this);
        n.i.accept(this);
        symbolTable.enterScope( n.i.s );
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
        return null;
    }

    public Type visit(BooleanType n) {
        return null;
    }

    public Type visit(IntegerType n) {
        return null;
    }

    // String s;
    public Type visit(IdentifierType n) {
        return null;
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
        SymbolType cond = SymbolType.get(n.e.accept(this));
        if( cond != SymbolType.BOOL )
            issues.add( new TypeCheckingIssue( CONDITION_NEEDS_BOOL, "if", n.e.line, n.e.col ) );
        n.s1.accept(this);
        n.s2.accept(this);
        return null;
    }

    // Exp e;
    // Statement s;
    public Type visit(While n) {
        SymbolType cond = SymbolType.get(n.e.accept(this));
        if( cond != SymbolType.BOOL )
            issues.add( new TypeCheckingIssue( CONDITION_NEEDS_BOOL, "while", n.e.line, n.e.col ) );
        n.s.accept(this);
        return null;
    }

    // Exp e;
    public Type visit(Print n) {
        if( SymbolType.get(n.e.accept(this)) != SymbolType.INT )
            issues.add( new TypeCheckingIssue( BAD_ARG_TYPE, "System.out.println", n.line, n.col ) );
        return null;
    }

    // Identifier i;
    // Exp e;
    public Type visit(Assign n) {
        SymbolType sti = symbolTable.lookupVariable( n.i.s ).type;
        SymbolType st1 = SymbolType.get(n.e.accept(this));
        if( ! st1.equals( sti ) )
            issues.add( new TypeCheckingIssue( TYPE_MISMATCH, n.e.line, n.e.col  ) );
        return null;
    }

    // Identifier i;
    // Exp e1,e2;
    public Type visit(ArrayAssign n) {
        SymbolType sti = symbolTable.lookupVariable( n.i.s ).type;
        SymbolType st1 = SymbolType.get(n.e1.accept(this));
        SymbolType st2 = SymbolType.get(n.e2.accept(this));
        if( sti != SymbolType.INTARRAY )
            issues.add( new TypeCheckingIssue( TYPE_MISMATCH, n.i.line, n.i.col  ) );
        if( st1 != SymbolType.INT )
            issues.add( new TypeCheckingIssue( TYPE_MISMATCH, n.e1.line, n.e1.col  ) );
        if( st2 != SymbolType.INT )
            issues.add( new TypeCheckingIssue( TYPE_MISMATCH, n.e2.line, n.e2.col  ) );
        return null;
    }

    // Exp e1,e2;
    public Type visit(And n) {
        SymbolType st1 = SymbolType.get(n.e1.accept(this));
        SymbolType st2 = SymbolType.get(n.e2.accept(this));
        if( st1 != SymbolType.BOOL )
            issues.add( new TypeCheckingIssue( NON_BOOL, "&&", n.e1.line, n.e1.col  ) );
        if( st2 != SymbolType.BOOL )
            issues.add( new TypeCheckingIssue( NON_BOOL, "&&", n.e2.line, n.e2.col  ) );
        return new IntegerType(n.line, n.col);
    }

    // Exp e1,e2;
    public Type visit(LessThan n) {
        SymbolType st1 = SymbolType.get(n.e1.accept(this));
        SymbolType st2 = SymbolType.get(n.e2.accept(this));
        if( st1 != SymbolType.INT )
            issues.add( new TypeCheckingIssue( NON_INT, '<', n.e1.line, n.e1.col  ) );
        if( st2 != SymbolType.INT )
            issues.add( new TypeCheckingIssue( NON_INT, '<', n.e2.line, n.e2.col  ) );
        return new BooleanType(n.line, n.col);
    }

    // Exp e1,e2;
    public Type visit(Plus n) {
        SymbolType st1 = SymbolType.get(n.e1.accept(this));
        SymbolType st2 = SymbolType.get(n.e2.accept(this));
        if( st1 != SymbolType.INT )
            issues.add( new TypeCheckingIssue( NON_INT, '+', n.e1.line, n.e1.col  ) );
        if( st2 != SymbolType.INT )
            issues.add( new TypeCheckingIssue( NON_INT, '+', n.e2.line, n.e2.col  ) );
        return new IntegerType(n.line, n.col);
    }

    // Exp e1,e2;
    public Type visit(Minus n) {
        SymbolType st1 = SymbolType.get(n.e1.accept(this));
        SymbolType st2 = SymbolType.get(n.e2.accept(this));
        if( st1 != SymbolType.INT )
            issues.add( new TypeCheckingIssue( NON_INT, '-', n.e1.line, n.e1.col  ) );
        if( st2 != SymbolType.INT )
            issues.add( new TypeCheckingIssue( NON_INT, '-', n.e2.line, n.e2.col  ) );
        return new IntegerType(n.line, n.col);
    }

    // Exp e1,e2;
    public Type visit(Times n) {
        SymbolType st1 = SymbolType.get(n.e1.accept(this));
        SymbolType st2 = SymbolType.get(n.e2.accept(this));
        if( st1 != SymbolType.INT )
            issues.add( new TypeCheckingIssue( NON_INT, '*', n.e1.line, n.e1.col  ) );
        if( st2 != SymbolType.INT )
            issues.add( new TypeCheckingIssue( NON_INT, '*', n.e2.line, n.e2.col  ) );
        return new IntegerType(n.line, n.col);
    }

    // Exp e1,e2;
    public Type visit(ArrayLookup n) {
        SymbolType st1 = SymbolType.get(n.e1.accept(this));
        SymbolType st2 = SymbolType.get(n.e2.accept(this));
        if( st1 != SymbolType.INTARRAY )
            issues.add( new TypeCheckingIssue( TYPE_MISMATCH, n.e1.line, n.e1.col  ) );
        if( st2 != SymbolType.INT )
            issues.add( new TypeCheckingIssue( TYPE_MISMATCH, n.e2.line, n.e2.col  ) );
        return new IntegerType(n.line, n.col);
    }

    // Exp e;
    public Type visit(ArrayLength n) {
        Type t = n.e.accept(this);
        if( SymbolType.get( t ) != SymbolType.INTARRAY ) {
            issues.add( new TypeCheckingIssue( NON_ARRAY, n.e.line, n.e.col) );
        }
        return new IntegerType( n.line, n.col );
    }

    // Exp e;
    // Identifier i;
    // ExpList el;
    public Type visit(Call n) {
        SymbolType st = SymbolType.get(n.e.accept(this));
        MethodSymbol m = symbolTable.lookupMethod( st.toString(), n.i.s );
        if( m == null ) {
            issues.add( new TypeCheckingIssue( NON_METHOD, n.i.line, n.i.col ) );
        } else {
            ArrayList<SymbolType> argtypes = m.argTypes();
            if( n.el.size() != argtypes.size() ) {
                issues.add( new TypeCheckingIssue( BAD_ARG_COUNT, n.i.s, n.i.line, n.i.col ) );
            } else {
                for( int i = 0 ; i < n.el.size(); i++ ) {
                    Exp e = n.el.elementAt( i );
                    Type t = e.accept(this);
                    if( ! argtypes.get(i).equals( SymbolType.get(t) ) ) {
                        issues.add( new TypeCheckingIssue( BAD_ARG_TYPE, n.i.s, e.line, e.col ) );
                    }
                }
            }
            return m.type.getAstType( n.line, n.col );
        }
        return null;
    }

    // int i;
    public Type visit(IntegerLiteral n) {
        return new IntegerType( n.line, n.col );
    }

    public Type visit(True n) {
        return new BooleanType( n.line, n.col );
    }

    public Type visit(False n) {
        return new BooleanType( n.line, n.col );
    }

    // String s;
    public Type visit(IdentifierExp n) {
        VariableSymbol v = symbolTable.lookupVariable( n.s );
        return v.type.getAstType( n.line, n.col );
    }

    public Type visit(This n) {
        ClassSymbol c = symbolTable.getCurActiveClassScope();
        if( c.function() == SymbolFunction.MAINCLASS ) {
            issues.add( new TypeCheckingIssue( THIS_INSIDE_STATIC, n.line, n.col ) );
        }
        return c.type.getAstType( n.line, n.col );
    }

    // Exp e;
    public Type visit(NewArray n) {
        Type t = n.e.accept(this);
        if( SymbolType.get(t) != SymbolType.INT ) {
            issues.add( new TypeCheckingIssue( TYPE_MISMATCH, n.e.line, n.e.col ) );
        }
        return new IntArrayType( n.line, n.col );
    }

    // Identifier i;
    public Type visit(NewObject n) {
        return n.i.accept(this);
    }

    // Exp e;
    public Type visit(Not n) {
        Type t = n.e.accept(this);
        if( SymbolType.get(t) != SymbolType.BOOL ) {
            issues.add( new TypeCheckingIssue( NON_BOOL, "!", n.line, n.col) );
        }
        return new BooleanType( n.line, n.col );
    }

    // String s;
    public Type visit(Identifier n) {
        return new IdentifierType( n.s, n.line, n.col );
    }
}
