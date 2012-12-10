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

    /*private ArrayList<Instruction> processQuad( Quadruple q ) {
        ArrayList<Instruction> ilist = null;
        switch( q.quadType() ) {
            case Quadruple.ARRAYASSIGNMENTQUADRUPLE:
            case Quadruple.ARRAYLENGTHQUADRUPLE:
            case Quadruple.ARRAYLOOKUPQUADRUPLE:
            case Quadruple.ASSIGNMENTQUADRUPLE:
                ilist = new ArrayList<Instruction>();
                AssignmentQuadruple aq = (AssignmentQuadruple)q;
                Symbol result = aq.getResult();
                Symbol firstArg = aq.getFirstArgument();
                Symbol secondArg = aq.getSecondArgument();
                int op = aq.getOperator();

                if(op == 0)
                {
                    if( firstArg instanceof Constant)
                    {
                        ADDI addi = new ADDI();
                        addi.setRT(allocator.getRegister(result));
                        addi.setRS(allocator.getRegister(secondArg));
                        addi.setImmediate(firstArg);
                        ilist.add(addi);
                    }
                    else
                    {
                        ADD add = new ADD();
                        add.setRD( allocator.getRegister(result) );
                        add.setRS( allocator.getRegister(firstArg));
                        add.setRT( allocator.getRegister(secondArg));
                        ilist.add(add);
                        break;
                    }
                }
                else if(op == 1)
                {
                    SUB sub = new SUB();
                    sub.setRD(allocator.getRegister(result));
                    sub.setRS(allocator.getRegister(firstArg));
                    sub.setRT(allocator.getRegister(secondArg));
                    ilist.add(sub);
                }
                else if(op == 2)
                {
                    MULT mult = new MULT();
                    mult.setRS(allocator.getRegister(firstArg));
                    mult.setRT(allocator.getRegister(secondArg));
                    ilist.add(mult);
                }
                else if(op == 3)
                {
                    if(firstArg instanceof Constant)
                    {
                        SLTI slti = new SLTI();
                        slti.setRS(allocator.getRegister(firstArg));
                        slti.setImmediate( secondArg );
                        slti.setRT(allocator.getRegister(result));
                        ilist.add(slti);
                    }
                    else
                    {
                        SLT slt = new SLT();
                        slt.setRS(allocator.getRegister(firstArg));
                        slt.setRT(allocator.getRegister(secondArg));
                        slt.setRD(allocator.getRegister(result));
                        ilist.add(slt);
                    }
                }
                else
                {
                    if(firstArg instanceof Constant)
                    {
                        ANDI andi = new ANDI();
                        andi.setRS(allocator.getRegister(firstArg));
                        andi.setImmediate( secondArg );
                        andi.setRT(allocator.getRegister(result));
                        ilist.add(andi);
                    }
                    else
                    {
                        AND and = new AND();
                        and.setRS(allocator.getRegister(firstArg));
                        and.setRT(allocator.getRegister(secondArg));
                        and.setRD(allocator.getRegister(result));
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
                    li.setRS( allocator.getRegister( result ));
                    ilist.add(li);
                } else {
                    MOVE move = new MOVE();
                    move.setRT( allocator.getRegister( result ) );
                    move.setRS( allocator.getRegister( firstArg ) );
                    ilist.add(li);
                }
                break;
            case Quadruple.GOTOQUADRUPLE:
                ilist = new ArrayList<Instruction>();
                Symbol result = q.result(); //Not sure whether address is stored in firstarg or result
                JUMP jp = new JUMP();
                jp.setImmediate(firstArg);
                ilist.add(jp);
            case Quadruple.IFQUADRUPLE:
                ilist = new ArrayList<Instruction>();
                Symbol firstArg = q.getFirstArgument();
                Symbol branch = q.getResult();
                BEQ beq = new BEQ();
                beq.setRS(allocator.getRegister(firstArg));
                beq.setImmediate(branch);
            case Quadruple.NEWARRAYQUADRUPLE:
            case Quadruple.NEWOBJECTQUADRUPLE:
                ilist.add( new SW);
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
*/
}
