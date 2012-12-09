package mips.allocator;
import java.util.*;
import visitor.symbol.*;
import ir.*;
import mips.code.*;
import syntaxtree.*;

public class Allocator {
    private static final Register [] registers = Register.values();
    private static final ArrayList<Register> gpRegList;
    static {
        gpRegList = new ArrayList<Register>(16);
        gpRegList.add(Register.t0);
        gpRegList.add(Register.t1);
        gpRegList.add(Register.t2);
        gpRegList.add(Register.t3);
        gpRegList.add(Register.t4);
        gpRegList.add(Register.t5);
        gpRegList.add(Register.t6);
        gpRegList.add(Register.t7);
        gpRegList.add(Register.t8);
        gpRegList.add(Register.t9);
        gpRegList.add(Register.s0);
        gpRegList.add(Register.s1);
        gpRegList.add(Register.s2);
        gpRegList.add(Register.s3);
        gpRegList.add(Register.s4);
        gpRegList.add(Register.s5);
        gpRegList.add(Register.s6);
        gpRegList.add(Register.s7);
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


    public static Register register( int index ) {
        return registers[index];

    }


    public void allocate( Identifier c, Identifier m, ArrayList<Quadruple> ir ) {
        boolean allocationComplete = false;
        System.out.println( "CLASS: " + c.s + " METH: " + m.s );
        MethodSymbol meth = symbolTable.getMethodByName( c, m );
        while( ! allocationComplete ){
            setCFGData( ir );
            dumpIR( ir );
            LivenessData ld = new LivenessData( ir );
            InterferenceGraph ifg = new InterferenceGraph( meth, ld, ir );
            System.out.println("INTEFERNCE GRAPH:");
            System.out.println(ifg.toString());
            ifg.color(gpRegList);
            allocationComplete = true;
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
