package ir;

import java.util.*;
import visitor.symbol.*;

public class LivenessData {
    private static final boolean DEBUG_LIVENESS = true;
    private static final String OUTFRMT = "%-20s%-20s%-20s\n";
    ArrayList<HashSet<Symbol>> use;
    ArrayList<HashSet<Symbol>> def;
    ArrayList<HashSet<Symbol>> in;
    ArrayList<HashSet<Symbol>> out;

    public LivenessData( ArrayList<CFGNode> cfg ) {
        use = new ArrayList<HashSet<Symbol>>( cfg.size() );
        def = new ArrayList<HashSet<Symbol>>( cfg.size() );
        in = new ArrayList<HashSet<Symbol>>( cfg.size() );
        out = new ArrayList<HashSet<Symbol>>( cfg.size() );

        for( CFGNode c : cfg ) {
            Symbol res = c.quad.getResult();
            Symbol arg1 = c.quad.getFirstArgument();
            Symbol arg2 = c.quad.getSecondArgument();
            HashSet<Symbol> uses = new HashSet<Symbol>();
            HashSet<Symbol> defs = new HashSet<Symbol>();
            if( res != null && res instanceof VariableSymbol ) {
                defs.add( res );
            }
            if( arg1 != null && arg1 instanceof VariableSymbol ) {
                uses.add( arg1 );
            }
            if( arg2 != null && arg2 instanceof VariableSymbol) {
                uses.add(arg2);
            }
            use.add( uses );
            def.add( defs );
        }
        calculateLiveness( cfg );
    }

    public void calculateLiveness( ArrayList<CFGNode> cfg ) {
        in.clear();
        out.clear();
        for( int i = 0; i < cfg.size(); i++ ) {
            in.add( new HashSet<Symbol>() );
            out.add( new HashSet<Symbol>() );
        }

        BitSet change = new BitSet( cfg.size() );
        change.set( 0, cfg.size() );

        int iterationCount = 1;
        while( ! change.isEmpty() ) {
            for( int i = cfg.size()-1; i >= 0; i-- ) {
                CFGNode c = cfg.get( i );
                int n = c.id;
                HashSet<Symbol> inP = new HashSet<Symbol>( in.get(n) );
                HashSet<Symbol> outP = new HashSet<Symbol>( out.get(n) );

                //out[n] - def[n]
                out.get(n).removeAll( def.get(n) );

                // in[n] = use[n]
                in.get(n).clear();
                in.get(n).addAll( use.get(n) );

                // in[n] U (out[n] - def[n])
                in.get(n).addAll( out.get(n) );

                out.get(n).clear();

                for( CFGNode succ : c.next ) {
                    int s = succ.id;
                    out.get(n).addAll( in.get(s) );
                }

                // check if there was a change on this iteration
                if( inP.equals( in.get(n) ) && outP.equals( out.get(n) ) ) {
                    change.clear( n );
                } else {
                    change.set( n );
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


    private static String setToString( HashSet<Symbol> h ) {
        StringBuilder sb = new StringBuilder();
        for( Symbol s : h ) {
            if( sb.length() > 0 )
                sb.append(",");
            sb.append( s.getName() );
        }
        return sb.toString();
    }

}
