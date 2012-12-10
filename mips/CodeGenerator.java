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
                ilist = new ArrayList<Instruction>();
                Symbol result = q.getResult();
                Symbol firstArg = q.getFirstArgument();
                Symbol secondArg = q.getSecondArgument();
                int op = q.getOperator();

                if(op == 0)
                {
                    if( firstArg instanceof Constant)
                    {
                        ADDI addi = new ADDI();
                        addi.setRT(regAllocator.getRegister(result));
                        addi.setRS(regAllocator.getRegister(secondArg));
                        addi.setImmediate(firstArg);
                        ilist.add(addi);
                    }
                    else
                    {
                        ADD add = new ADD();
                        add.setRD( regAllocator.getRegister(result) );
                        add.setRS( regAllocator.getRegister(firstArg));
                        add.setRT( regAllocator.getRegister(secondArg));
                        ilist.add(add);
                        break;
                    }
                }
                if else(op == 1)
                {
                    SUB sub = new SUB();
                    sub.setRD(regAllocator.getRegister(result));
                    sub.setRS(regAllocator.getRegister(firstArg));
                    sub.setRT(regAllocator.getRegister(secondArg));
                    ilist.add(sub);
                }
                if else(op == 2)
                {
                    MULT mult = new MULT();
                    mult.setRS(regAllocator.getRegister(firstArg));
                    mult.setRT(regAllocator.getRegister(secondArg));
                    ilist.add(mult);
                }
                if else(op == 3)
                {
                    if(firstArg instanceof Constant)
                    {
                        SLTI slti = new SLTI();
                        slti.setRS(regAllocator.getRegister(firstArg));
                        slti.setImmediate(regAllocator.getRegister(secondArg));
                        slti.setRT(regAllocator.getRegister(result));
                        ilist.add(slti);
                    }
                    else
                    {
                        SLT slt = new SLT();
                        slt.setRS(regAllocator.getRegister(firstArg));
                        slt.setRT(regAllocator.getRegister(secondArg));
                        slt.setRD(regAllocator.getRegister(result));
                        ilist.add(slt);
                    }
                }
                else
                {
                    if(firstArg instanceof Constant)
                    {
                        ANDI andi = new ANDI();
                        andi.setRS(regAllocator.getRegister(firstArg));
                        andi.setImmediate(regAllocator.getRegister(secondArg));
                        andi.setRT(regAllocator.getRegister(result));
                        ilist.add(andi);
                    }
                    else
                    {
                        AND and = new AND();
                        and.setRS(regAllocator.getRegister(firstArg));
                        and.setRT(regAllocator.getRegister(secondArg));
                        and.setRD(regAllocator.getRegister(result));
                        ilist.add(and);
                    }
                }


            case Quadruple.CALLQUADRUPLE:
            case Quadruple.COPYQUADRUPLE:
                ilist = new ArrayList<Instruction>();
                Symbol result = q.getResult();
                Symbol firstArg = q.getFirstArgument();
                if( firstArg instanceof Constant ) {
                    LI li = new LI();
                    li.setImmediate( firstArg );
                    li.setRS( regAllocator.getRegister( result ));
                    ilist.add(li);
                }
                break;
            case Quadruple.GOTOQUADRUPLE:
                ilist = new ArrayList<Instruction>();
                Symbol firstArg = q.getFirstArgument(); //Not sure whether address is stored in firstarg or result
                JUMP jp = new JUMP();
                jp.setImmediate(firstArg);
                ilist.add(jp);
            case Quadruple.IFQUADRUPLE:
                ilist = new ArrayList<Instruction>();
                Symbol firstArg = q.getFirstArgument();
                Symbol branch = q.getResult();
                BEQ beq = new BEQ();
                beq.setRS(regAllocator.getRegister(firstArg));
                beq.setImmediate(branch);
                //Haven't finished!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            case Quadruple.NEWARRAYQUADRUPLE:
            case Quadruple.NEWOBJECTQUADRUPLE:
            case Quadruple.PARAMETERQUADRUPLE:
            case Quadruple.RETURNQUADRUPLE:
                ilist = new ArrayList<Instruction>();
                Symbol returnAdd = q.RETURNQUADRUPLE();
                JR jr = new JR();
                jr.setImmediate(returnAdd);
                ilist.add(jr);
            case Quadruple.UNARYASSIGNMENTQUADRUPLE:
            default:
                break;
        }
        return ilist;
    }
}
