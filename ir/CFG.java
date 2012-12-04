package ir;

import java.util.*;
public class CFG{
    ArrayList<CFGNode> cfg;
    public CFG( ArrayList<Quadruple> ir ) {
        cfg = new ArrayList<CFGNode>( ir.size() );
        for( int i = 0; i < ir.size(); i++ ) {
            cfg.add( new CFGNode( i, ir.get(i) ) );
        }
        for( int i = 0; i < cfg.size(); i++ ) {
            CFGNode c = cfg.get(i);
            if( c.quad instanceof GotoQuadruple ) {
                CFGNode next = cfg.get( Integer.parseInt( c.quad.getResult().toString() ) );
                c.next.add( next );
            } else if( c.quad instanceof IfQuadruple ) {
                CFGNode next = cfg.get( Integer.parseInt( c.quad.getResult().toString() ) );
                c.next.add( next );
                if ( i < (cfg.size() - 1) )
                    c.next.add( cfg.get( i+1 ) );
            } else {
                if ( i < (cfg.size() - 1) )
                    c.next.add( cfg.get( i+1 ) );
            }

            for( CFGNode succ : c.next ) {
                succ.prev.add( c );
            }
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for( CFGNode c: cfg ){
            if( s.length() > 0 ) {
                s.append( '\n' );
            }
            s.append( c.toString() );
        }
        return s.toString();
    }

    public LivenessData generateLivenessData() {
        return new LivenessData( cfg );
    }

}



class CFGNode {
    public int id;
    public Quadruple quad;
    public HashSet<CFGNode> next;
    public HashSet<CFGNode> prev;
    public CFGNode( int i , Quadruple q ) {
        prev = new HashSet<CFGNode>();
        next = new HashSet<CFGNode>();
        id = i;
        quad = q;
    }
    public String toString() {
        return String.format( "%d. %-20s // %-25s, %s",
                id,
                quad.toString(),
                String.format( "Predecessors: %s", prevToString() ),
                String.format( "Sucessors: %s", nextToString() ) );
    }

    public String nextToString() {
        StringBuilder sb = new StringBuilder();
        if( ! next.isEmpty() ) {
            for( CFGNode c: next ) {
                if( sb.length() > 0 )
                    sb.append(',');
                sb.append( c.id );
            }

            return sb.toString();
        }
        return "END BLOCK";
    }

    public String prevToString(){
        StringBuilder sb = new StringBuilder();
        for( CFGNode c: prev ){
            if( sb.length() > 0 ) {
                sb.append( "," );
            }
            sb.append( c.id );
        }
        if( sb.length() == 0 ) {
            sb.append( "START BLOCK" );
        }
        return sb.toString();
    }

}
