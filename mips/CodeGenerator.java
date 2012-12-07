package mips;
import mips.register.*;
import mips.code.*;
import ir.*;
import visitor.symbol.*;
import java.util.*;

public class CodeGenerator {
    HashMap<String, ArrayList<Quadruple>> methodIR;
    Allocator regAllocator;
    LabelMapping labels;

    public CodeGenerator( HashMap<String, ArrayList<Quadruple>> methodIR ) {
        this.methodIR = methodIR;
        regAllocator = new Allocator();
        labels = new LabelMapping();
    }

    ArrayList<Instruction> generate() {
        ArrayList<Instruction> finishedCode = new ArrayList<Instruction>();
        for( String methodname : methodIR.keySet() ) {
            ArrayList<Quadruple> IR = methodIR.get( methodname );
            labels.add( methodName );
            finishedCode.add( labels.get(methodname) );
            Allocator.allocateRegisters( IR );
            for( Quadruple quad : IR ) {
                finishedCode.addAll( processQuad( quad ) );
            }
        }
        return finishedCode;
    }

    private ArrayList<Instruction> processQuad( Quadruple q ) {
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
                    li.setRS( regAllocator.getRegister( result );
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
