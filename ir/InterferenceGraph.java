package ir;

import java.util.*;
import visitor.symbol.*;
public class InterferenceGraph {
    HashMap<Symbol, Node> graphNodes;

    public InterferenceGraph( LivenessData ld ) {
        graphNodes = new HashMap<Symbol,Node>();
        for( int i = 0; i < ld.def.size(); i++ ) {
            for( Symbol def : ld.def.get(i) ) {
                Node n = getNode( def );
                for( Symbol liveout : ld.out.get(i) ) {
                    if( liveout != def )
                        n.adjacent.add( getNode( liveout ) );
                }
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for( Node n : graphNodes.values() ) {
            if( sb.length() > 0 ) {
                sb.append('\n' );
            }
            sb.append( n.toString() );
        }
        return sb.toString();
    }

    private Node getNode( Symbol s ) {
        Node n = graphNodes.get( s );
        if( n == null ) {
            n = new Node( s );
            graphNodes.put( s, n );
        }
        return n;
    }

    public void color( int k ) {

    }

    private class Node {
        public Symbol symbol;
        public HashSet<Node> adjacent;
        public int color;

        Node( Symbol s ) {
            symbol = s;
            adjacent = new HashSet<Node>();
            color = -1;
        }

        public int degree() {
            return adjacent.size();
        }

        public void remove(){
            for( Node n : adjacent ) {
                n.adjacent.remove( this );
            }
        }

        public void add() {
            for( Node n : adjacent ) {
                n.adjacent.add( this );
            }
        }

        public boolean pickColor( int k ) {
            for( int i = 0; i < k; i++ ) {
                boolean available = true;
                for( Node n : adjacent ) {
                    if( n.color == i ) {
                        available = false;
                        break;
                    }
                }
                if( available ) {
                    color = i;
                    break;
                }
            }
            if( color > -1 ) return true;
            return false;
        }

        public String toString() {
            return String.format( "%s Color: %d Degree: %d Adj:%s",
                    symbol.getName(),
                    color,
                    degree(),
                    adjToString()
                    );
        }

        private String adjToString() {
            StringBuilder sb = new StringBuilder();
            for( Node n : adjacent ) {
                if( sb.length() > 0 )
                    sb.append( ',' );
                sb.append( n.symbol.getName() );
            }
            return sb.toString();
        }
    }
}
