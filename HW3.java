import java.util.ArrayList;
import visitor.symbol.*;
import syntaxtree.*;
import ir.*;
public class HW3 {

    public static void main( String [] args ) {
        ArrayList<Quadruple> ir = new ArrayList<Quadruple>();

        IntegerType inttype = new IntegerType();
        BooleanType bool = new BooleanType();
        VariableSymbol x = new VariableSymbol( inttype, new Identifier("x") );
        VariableSymbol a = new VariableSymbol( inttype, new Identifier("a") );
        VariableSymbol c = new VariableSymbol( inttype, new Identifier("c") );
        VariableSymbol b = new VariableSymbol( bool, new Identifier("b") );
        Label L1 = new Label( 1 );
        Label L2 = new Label( 5 );
        Constant zero = new Constant( inttype, 0 );
        Constant two = new Constant( inttype, 2 );
        Constant five = new Constant( inttype, 5 );
        Constant ten = new Constant( inttype, 10 );

        ir.add( new CopyQuadruple( zero, x ) ); // x:= 0
        ir.add( new AssignmentQuadruple( 2, x, two, a ) ); // a:=x*2
        ir.add( new AssignmentQuadruple( 3, a, five, b ) ); // b:=a<5
        ir.add( new IfQuadruple( b, L2 ) ); //iftrue b goto l2
        ir.add( new AssignmentQuadruple( 2, a, two, a ) ); // a:=a+2
        ir.add( new AssignmentQuadruple( 2, a, x, c ) ); // c:=a+x
        ir.add( new AssignmentQuadruple( 3, x, ten, b ) ); // b:=x<10
        ir.add( new IfQuadruple( b, L1 ) ); //iftrue b goto l2
        ir.add( new ReturnQuadruple( c ) ); // return c

        for( Quadruple q : ir ){
            System.out.println( q );
        }

        CFG cfg = new CFG( ir );
        System.out.println("\nCFG Object:");
        System.out.println( cfg );

        LivenessData livedata = cfg.generateLivenessData();

        System.out.println( "\nLiveness Analysis:" );
        System.out.println( livedata );

        InterferenceGraph ifg = new InterferenceGraph( livedata );

        System.out.println( "\nInterference Graph:" );
        System.out.println( ifg );
    }

}
