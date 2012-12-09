package mips;
import mips.allocator.*;
import mips.code.*;
import ir.*;
import visitor.symbol.*;
import java.util.*;
import syntaxtree.*;

public class CodeGenerator {
    IR ir;
    SymbolTable symbolTable;
    Allocator allocator;

    public CodeGenerator( IR ir, SymbolTable st ) {
        this.ir = ir;
        symbolTable = st;
        allocator = new Allocator( st );
    }

    public void generate() {
        for( Identifier c : ir.classes() ) {
            HashMap<Identifier,ArrayList<Quadruple>> methodIRMap = ir.getClass(c);
            for( Identifier m: methodIRMap.keySet() ) {
                ArrayList<Quadruple> methodIR = methodIRMap.get(m);
                allocator.allocate( c, m, methodIR );
            }
        }
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
                    li.setRS( allocator.getRegister( result ));
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
