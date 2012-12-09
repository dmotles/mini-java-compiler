package mips.allocator;
import java.util.*;
import visitor.symbol.*;
import ir.*;

public class LivenessData {
    private static final boolean DEBUG_LIVENESS = true;
    private static final String OUTFRMT = "%-15s%-15s%-15s\n";
    ArrayList<HashSet<Symbol>> use;
    ArrayList<HashSet<Symbol>> def;
    ArrayList<HashSet<Symbol>> in;
    ArrayList<HashSet<Symbol>> out;

    public LivenessData( ArrayList<Quadruple> ir ) {
        use = new ArrayList<HashSet<Symbol>>( ir.size() );
        def = new ArrayList<HashSet<Symbol>>( ir.size() );
        in = new ArrayList<HashSet<Symbol>>( ir.size() );
        out = new ArrayList<HashSet<Symbol>>( ir.size() );

        for( int i = 0 ; i < ir.size(); i++ ) {
            Quadruple q = ir.get(i);
            Symbol res = q.getResult();
            Symbol arg1 = q.getFirstArgument();
            Symbol arg2 = q.getSecondArgument();
            HashSet<Symbol> uses = new HashSet<Symbol>();
            HashSet<Symbol> defs = new HashSet<Symbol>();
            if( res != null && res instanceof VariableSymbol || res instanceof MemorySymbol ) {
                defs.add( res );
            }
            if( arg1 != null && arg1 instanceof VariableSymbol || res instanceof MemorySymbol ) {
                uses.add( arg1 );
            }
            if( arg2 != null && arg2 instanceof VariableSymbol || res instanceof MemorySymbol ) {
                uses.add( arg2);
            }
            use.add( uses );
            def.add( defs );
        }
        calculateLiveness( ir );
    }

    public void calculateLiveness( ArrayList<Quadruple> ir ) {
        in.clear();
        out.clear();
        for( int i = 0; i < ir.size(); i++ ) {
            in.add( new HashSet<Symbol>() );
            out.add( new HashSet<Symbol>() );
        }

        BitSet change = new BitSet( ir.size() );
        change.set( 0, ir.size() );

        int iterationCount = 1;
        while( ! change.isEmpty() ) {
            for( int i = ir.size()-1; i >= 0; i-- ) {
                Quadruple q = ir.get( i );
                HashSet<Symbol> inP = new HashSet<Symbol>( in.get(i) );
                HashSet<Symbol> outP = new HashSet<Symbol>( out.get(i) );

                //out[n] - def[n]
                out.get(i).removeAll( def.get(i) );

                // in[n] = use[n]
                in.get(i).clear();
                in.get(i).addAll( use.get(i) );

                // in[n] U (out[n] - def[n])
                in.get(i).addAll( out.get(i) );

                out.get(i).clear();

                for( Quadruple s : q.succ ) {
                    out.get(i).addAll( in.get(s.ID) );
                }

                // check if there was a change on this iteration
                if( inP.equals( in.get(i) ) && outP.equals( out.get(i) ) ) {
                    change.clear( i );
                } else {
                    change.set( i );
                }
            }

            if( DEBUG_LIVENESS ) {
                System.err.println( "\nLiveness Algo Iter " + iterationCount );
                System.err.println( toString() );
                iterationCount++;
            }
        }
    }

    public String useDefToString() {
        StringBuilder sb = new StringBuilder();

        sb.append( String.format( OUTFRMT, "-Block-", "-Defs-", "-Uses-") );
        for( int i = 0; i < def.size(); i++ ) {
            sb.append( String.format(
                        OUTFRMT,
                        Integer.toString( i ),
                        setToString( def.get(i) ),
                        setToString( use.get(i) )
                        )
                    );
        }

        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append( String.format( OUTFRMT, "-Block-", "-In-", "-Out-") );
        for( int i = 0; i < in.size(); i++ ) {
            sb.append( String.format(
                        OUTFRMT,
                        Integer.toString( i ),
                        setToString( in.get(i) ),
                        setToString( out.get(i) )
                        )
                    );
        }

        return sb.toString();
    }


    private static String setToString( HashSet<? extends Symbol> h ) {
        StringBuilder sb = new StringBuilder();
        for( Symbol s : h ) {
            if( sb.length() > 0 )
                sb.append(",");
            sb.append( s.getName() );
        }
        return sb.toString();
    }

}
