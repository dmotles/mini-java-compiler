package visitor;

import syntaxtree.*;
import java.util.ArrayList;

public class IRVisitor implements Visitor {
    String t0, t1, t2;
    Quad q;
    public ArrayList<Quad> quadAL = new ArrayList<Quad>();

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
        n.s.accept(this);
        q = new Quad( null, "return", "0 // Program Exit", null );
        quadAL.add(q);
        t0 = q.result;
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclSimple n) {
        for ( int i = 0; i < n.ml.size(); i++ ) {
            n.ml.elementAt(i).accept(this);
        }
    }

    // Identifier i;
    // Identifier j;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclExtends n) {
        for ( int i = 0; i < n.ml.size(); i++ ) {
            n.ml.elementAt(i).accept(this);
        }
    }

    // Type t;
    // Identifier i;
    public void visit(VarDecl n) {
        n.t.accept(this);
        n.i.accept(this);
    }

    // Type t;
    // Identifier i;
    // FormalList fl;
    // VarDeclList vl;
    // StatementList sl;
    // Exp e;
    public void visit(MethodDecl n) {
        for ( int i = 0; i < n.sl.size(); i++ ) {
            n.sl.elementAt(i).accept(this);
        }
        n.e.accept(this);
        q = new Quad( null, "return", t0, null );
        quadAL.add(q);
        t0 = q.result;

    }

    // Type t;
    // Identifier i;
    public void visit(Formal n) {
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
        int ifStart = quadAL.size();
        quadAL.add( new Quad( null, "ifgoto", t0, "?") );
        n.s2.accept(this);
        int elseEnd = quadAL.size();
        quadAL.add( new Quad( null, "goto", "?", null) );
        quadAL.get( ifStart ).var2 = Integer.toString( elseEnd+1 );
        n.s1.accept(this);
        int ifEnd = quadAL.size();
        quadAL.get( elseEnd ).var1 = Integer.toString( ifEnd+1 );
        t0 = null;
    }

    // Exp e;
    // Statement s;
    public void visit(While n) {
        n.e.accept(this);
        int loopStart = quadAL.size();
        quadAL.add( new Quad( null, "ifgoto", t0, "?") );
        n.s.accept(this);
        quadAL.add( new Quad( null, "goto", Integer.toString(loopStart), null ) );
        int loopEnd = quadAL.size();
        quadAL.get( loopStart ).var2 = Integer.toString( loopEnd );
        t0 = null;
    }

    // Exp e;
    public void visit(Print n) {
        n.e.accept(this);
        q = new Quad( null, "param", t0, null);
        quadAL.add( q );
        q = new Quad( nextTmp(), "call", "print", "1");
        quadAL.add( q );
        t0 = q.result;
    }

    // Identifier i;
    // Exp e;
    public void visit(Assign n) {
        n.e.accept(this);
        t1 = t0;
        n.i.accept(this);
        q = new Quad( t0, null, t1, null);
        quadAL.add(q);
        t0 = q.result;
    }

    // Identifier i;
    // Exp e1,e2;
    public void visit(ArrayAssign n) {
        n.e1.accept(this);
        t1 = t0;
        n.e2.accept(this);
        t2 = t0;
        n.i.accept(this);
        q = new Quad( t0, null, t1, t2);
        quadAL.add(q);
        t0 = q.result;
    }

    // Exp e1,e2;
    public void visit(And n) {
        n.e1.accept(this);
        t1 = t0;
        n.e2.accept(this);
        q = new Quad( nextTmp(), "&&", t1, t0);
        quadAL.add(q);
        t0 = q.result;
    }

    // Exp e1,e2;
    public void visit(LessThan n) {
        n.e1.accept(this);
        t1 = t0;
        n.e2.accept(this);
        q = new Quad( nextTmp(), "<", t1, t0);
        quadAL.add(q);
        t0 = q.result;
    }

    // Exp e1,e2;
    public void visit(Plus n) {
        n.e1.accept(this);
        t1 = t0;
        n.e2.accept(this);
        q = new Quad( nextTmp(), "+", t1, t0);
        quadAL.add(q);
        t0 = q.result;
    }

    // Exp e1,e2;
    public void visit(Minus n) {
        n.e1.accept(this);
        t1 = t0;
        n.e2.accept(this);
        q = new Quad( nextTmp(), "-", t1, t0);
        quadAL.add(q);
        t0 = q.result;
    }

    // Exp e1,e2;
    public void visit(Times n) {
        n.e1.accept(this);
        t1 = t0;
        n.e2.accept(this);
        q = new Quad( nextTmp(), "*", t1, t0);
        quadAL.add(q);
        t0 = q.result;
    }

    // Exp e1,e2;
    public void visit(ArrayLookup n) {
        n.e1.accept(this);
        t1 = t0;
        n.e2.accept(this);
        q = new Quad( nextTmp(), null, t1, t0);
        quadAL.add(q);
        t0 = q.result;
    }

    // Exp e;
    public void visit(ArrayLength n) {
        n.e.accept(this);
        q = new Quad( nextTmp(), "length", t0, null);
        quadAL.add(q);
        t0 = q.result;
    }

    // Exp e;
    // Identifier i;
    // ExpList el;
    public void visit(Call n) {
        int paramcount = 0;
        n.e.accept(this);
        q = new Quad( null, "param", t0, null);
        quadAL.add( q );
        paramcount++;
        for ( int i = 0; i < n.el.size(); i++ ) {
            n.el.elementAt(i).accept(this);
            q = new Quad( null, "param", t0, null);
            quadAL.add( q );
            paramcount++;
        }
        n.i.accept(this);
        q = new Quad( nextTmp(), "call", t0, Integer.toString(paramcount));
        quadAL.add( q );
        t0 = q.result;
    }

    // int i;
    public void visit(IntegerLiteral n) {
        t0 = Integer.toString( n.i );
    }

    public void visit(True n) {
        t0 =  "1";
    }

    public void visit(False n) {
        t0 = "0"; //this needs to be fixed.
    }

    // String s;
    public void visit(IdentifierExp n) {
        t0 = n.s;
    }

    public void visit(This n) {
        t0 = "this";
    }

    // Exp e;
    public void visit(NewArray n) {
        n.e.accept(this);
        q = new Quad( nextTmp(), "new", "int", t0 );
        quadAL.add( q );
        t0 = q.result;
    }

    // Identifier i;
    public void visit(NewObject n) {
        n.i.accept( this );
        q = new Quad( nextTmp(), "new", t0, null );
        quadAL.add( q );
        t0 = q.result;
    }

    // Exp e;
    public void visit(Not n) {
        n.e.accept(this);
        q = new Quad( nextTmp(), "!", t0, null );
        quadAL.add( q );
        t0 = q.result;
    }

    // String s;
    public void visit(Identifier n) {
        t0 = n.s;
    }

    private String nextTmp() {
        return "t" + quadAL.size();
    }
}
