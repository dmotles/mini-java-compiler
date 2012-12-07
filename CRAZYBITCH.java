import visitor.symbol.*;
import ir.*;
import mips.*;
import mips.code.*;
import mips.register.*;
import syntaxtree.*;
import java.util.*;
public class CRAZYBITCH {

    public static void main( String [] args ) {
        Type inttype = new IntegerType();
        Identifier id = new Identifier( "_t0" );
        VariableSymbol t0 = new VariableSymbol( inttype, id );
        Constant nine = new Constant( inttype, 9);
        CopyQuadruple copy = new CopyQuadruple( nine,t0 );
        ArrayList<Instruction> ins = CodeGenerator.processIR( copy );
        for( Instruction i : ins ) {
            System.out.println( i );
        }

    }
}
