package mips;
import mips.register.*;
import mips.code.*;
import ir.*;
import visitor.symbol.*;
import java.util.*;

public class CodeGenerator {
    public static ArrayList<Instruction> processIR( Quadruple q ) {
        ArrayList<Instruction> ilist = null;
        switch( q.quadType() ) {
            case Quadruple.ARRAYASSIGNMENTQUADRUPLE:
            case Quadruple.ARRAYLENGTHQUADRUPLE:
            case Quadruple.ARRAYLOOKUPQUADRUPLE:
            case Quadruple.ASSIGNMENTQUADRUPLE:
            case Quadruple.CALLQUADRUPLE:
            case Quadruple.COPYQUADRUPLE:
                ilist = new ArrayList<Instruction>();
                Symbol result = q.getResult();
                Symbol firstArg = q.getFirstArgument();
                if( firstArg instanceof Constant ) {
                    LI li = new LI();
                    li.setImmediate( firstArg );
                    li.setRS( Allocator.parseRegister( result.getName().toString() ) );
                    ilist.add(li);
                }
                break;
            case Quadruple.GOTOQUADRUPLE:
            case Quadruple.IFQUADRUPLE:
            case Quadruple.NEWARRAYQUADRUPLE:
            case Quadruple.NEWOBJECTQUADRUPLE:
            case Quadruple.PARAMETERQUADRUPLE:
            case Quadruple.RETURNQUADRUPLE:
            case Quadruple.UNARYASSIGNMENTQUADRUPLE:
            default:
                break;
        }
        return ilist;
    }
}
