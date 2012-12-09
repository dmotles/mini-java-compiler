package mips.allocator;
import java.util.*;
import visitor.symbol.*;
import syntaxtree.*;

public class ActivationFrameMap {
    EnumMap<Register,Integer> regOffsets;
    HashMap<VariableSymbol, Integer> argOffsets;
    BitSet allocatedWords;
    public ActivationFrameMap( MethodSymbol m ) {
        ArrayList<VariableSymbol> params = m.getParameters();

        int numOverflowParams = (params.size() > 4) ? params.size() - 4 : 0;
        allocatedWords = new BitSet( 1+numOverflowParams );
        allocatedWords.set( 0, allocatedBytes.size() );

        regOffsets = new EnumMap<Register,Integer>(Register.class);
        regOffsets.put( Register.ra, 0 );

        varOffsets = new HashMap<VariableSymbol,Integer>();

        params = params.subList(4,params.size()); // only get "overflow" params
        for( int i = 0; i < params.size() ; i++ ){
            varOffsets.put( params.get(i), Integer.valueOf( 1 + i ) );
        }

    }
    public Integer getRegOffset( Register r ) {
        Integer ret = regOffsets.get( r );
        if( ret != null ) {
            ret = Integer.parseInt( reg.intValue() * 4 );
        }
        return ret;
    }
    public Integer getVarOffset( VariableSymbol s ) {
        Integer ret = varOffsets.get( s );
        if( ret != null ) {
            ret = Integer.parseInt( reg.intValue() * 4 );
        }
        return ret;
    }
    public void spillRegister( Register r ) {
        if( regOffsets.get(r) == null ){
            int nextoffset = allocatedWords.nextClearBit(0);
            regOffsets.put( r, Integer.valueOf(nextoffset) );
            allocatedWords.set(nextoffset);
        }
    }
    public void storeSymbol( VariableSymbol s ) {
        if( varOffsets.get(s) == null ){
            int nextoffset = allocatedWords.nextClearBit(0);
            varOffsets.put( s, Integer.valueOf(nextoffset) );
            allocatedWords.set(nextoffset);
        }
    }
}
