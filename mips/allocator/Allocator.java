package mips.allocator;
import java.util.*;
import visitor.symbol.*;
import ir.*;
import mips.code.*;
import syntaxtree.*;

public class Allocator {
    private static final ArrayList<ColorAllocation> registers;
    private static final ArrayList<ColorAllocation> generalPurposeRegisters;
    static {
        Register [] regs = Register.values();
        IdentifierType ridt = new IdentifierType("_MIPS_REGISTER_");
        registers = new ArrayList<ColorAllocation>();
        generalPurposeRegisters = new ArrayList<ColorAllocation>();
        for( int i = 0; i < regs.length; i++ ) {
            ColorAllocation a = new ColorAllocation( regs[i],
                        new VariableSymbol(
                            ridt,
                            new Identifier("$" + regs[i] )
                            )
                        );
            registers.add(a);
            if( ! regs[i].reserved ) {
                generalPurposeRegisters.add( a );
            }
        }
    }
    private SymbolTable symbolTable;
//    private HashMap<Symbol,Register> symbolRegMap;
//    private LabelMap labels;
//    private ClassFootprintMap classes;
//    private ActivationFrameMap afm;
//    private HashMap<VariableSymbol,Integer> [] spillOffsets;
//    private HashMap<VariableSymbol,Integer> [] fillOffsets;

    public Allocator( SymbolTable st ) {
        symbolTable = st;
    }



    public void allocate( Identifier c, Identifier m, ArrayList<Quadruple> ir ) {
        boolean allocationComplete = false;
        System.out.println( "CLASS: " + c.s + " METH: " + m.s );
        MethodSymbol meth = symbolTable.getMethodByName( c, m );
        while( ! allocationComplete ){
            setCFGData( ir );
            dumpIR( ir );
            LivenessData ld = new LivenessData( ir );
            InterferenceGraph ifg = new InterferenceGraph( meth, ld, ir, registers );
            allocationComplete = ifg.assignColors( generalPurposeRegisters );
            if( ! allocationComplete ) {

            }
        }
    }

    private static void dumpIR( ArrayList<Quadruple> ir ) {
        for( int i = 0 ; i < ir.size() ; i++ ) {
            Quadruple q = ir.get(i);
            System.out.print("\t" + q.ID + ": " + q );
            StringBuilder succ = new StringBuilder();
            for( Quadruple s : q.succ ) {
                if( succ.length() > 0 ) succ.append(',');
                succ.append( s.ID );
            }
            StringBuilder pred = new StringBuilder();
            for( Quadruple p : q.pred ) {
                if( pred.length() > 0 ) pred.append(',');
                pred.append( p.ID );
            }
            System.out.println("  # Succ: " + succ + " Pred: " + pred );
        }
    }

    private static void setCFGData( ArrayList<Quadruple> ir ) {
        for( int i = 0; i < ir.size(); i++ ) ir.get(i).ID = i;
        for( int i = 0; i < ir.size(); i++ ) {
            Quadruple quad = ir.get(i);
            Quadruple jump = null;
            switch( quad.quadType() ) {
                case Quadruple.IFQUADRUPLE:
                    if( i < (ir.size() - 1 ) )
                        quad.succ.add( ir.get(i+1 ) );
                case Quadruple.GOTOQUADRUPLE:
                    jump = ir.get( Integer.parseInt( quad.getResult().toString() ) );
                    quad.succ.add( jump );
                    break;
                default:
                    if( i < (ir.size() - 1 ) )
                        quad.succ.add( ir.get(i+1 ) );
                    break;
            }
            for( Quadruple succ : quad.succ ) {
                succ.pred.add( quad );
            }
        }
    }

    private static void reNumberQuads( ArrayList<Quadruple> ir ) {
        for( int i = 0; i < ir.size(); i++ ) ir.get(i).ID = i;
    }

    private static void adjustJumpLabels( ArrayList<Quadruple> ir ) {
        for( int i = 0 ; i < ir.size(); i++ ) {
            Quadruple q = ir.get(i);
            Symbol target = q.getResult();
            switch( q.quadType() ) {
                case Quadruple.GOTOQUADRUPLE:
                case Quadruple.IFQUADRUPLE:
                    if( target == null ) continue;
                    for( Quadruple succ : q.succ ) {
                        if( i < ir.size() - 1 && succ != ir.get(i+1) ) {
                            //TODO: finish

                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }


    //public ArrayList<Instruction> getSpillCode( Quadruple q ) {
        //ArrayList<Instruction> spillcode = new ArrayList<Instruction>();
        //for( VariableSymbol v: spillOffsets[q.ID].keySet() ) {
            //SW store = new SW();
            //store.setRT( symbolRegMap.get(v) );
            //store.setRS( Register.gp );
            //store.setImm( Integer.intValue( spillOffsets[q.id].get( v ) ) );
            //spillecode.add( store );
        //}
        //for( VariableSymbol v: fillOffsets[q.ID].keySet() ) {
            //LW load = new LW();
            //load.setRT( symbolRegMap.get(v) );
            //load.setRS( Register.gp );
            //load.setImm( Integer.intValue( fillOffsets[q.id].get( v ) ) );
            //spillecode.add( load );
        //}
        //return spillcode;
    //}



    //public Label getMipsLabel( String method ) {
        //return labels.get(method);
    //}
    //
    public Register getRegister( Symbol s )  {
        return Register.t0;
    }
}
