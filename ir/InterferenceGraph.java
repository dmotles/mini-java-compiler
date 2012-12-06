package ir;

import java.util.*;
import visitor.symbol.*;
public class InterferenceGraph {
    HashMap<Symbol, Node> graphNodes;
    private static final boolean DEBUG_COLOR = true;

    public InterferenceGraph( LivenessData ld ) {
        graphNodes = new HashMap<Symbol,Node>();
        for( int i = 0; i < ld.in.size(); i++ ) {
            HashSet<Symbol> inset = ld.in.get(i);
            HashSet<Symbol> outset = ld.out.get(i);
            for( Symbol livein : inset ) {
                if( outset.contains(livein) ) {
                for( Symbol liveout : outset ) {
                    if( livein != liveout ) {
                        Node inNode = getNode( livein );
                        Node outNode = getNode( liveout );
                        inNode.adjacent.add( outNode );
                        outNode.adjacent.add( inNode );
                    }
                }

                }
            }
        }
    }

    public String toString() {
        return nodeCollectionToString( graphNodes.values() );
    }

    private String nodeCollectionToString( Collection<Node> c ){
        StringBuilder sb = new StringBuilder();
        for( Node n : c ) {
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
        Stack<Node> stack = new Stack<Node>();
        HashSet<Node> graph = new HashSet<Node>( graphNodes.values() );
        int i = 1;
        while( !graph.isEmpty() ) {
            // find node < degree
            for( Node n : graph ){
                if( n.degree() < k ) {
                    graph.remove( n );
                    n.remove();
                    //add to stack
                    stack.push( n );
                    break;
                }
            }

            if( DEBUG_COLOR ) {
                System.err.println( "\n== ITERATION: " + i++ );
                System.err.println( "Stack:" );
                System.err.println( nodeCollectionToString(stack));
                System.err.println( "Graph:" );
                System.err.println( nodeCollectionToString(graph));
            }
        }
        if(DEBUG_COLOR) System.err.println("\nRe-Adding Nodes!\n");
        // add each node 1 by 1 back to graph and color
        while( ! stack.empty() ) {
            Node n = stack.pop();
            n.add();
            graph.add( n );
            n.color( k );
            if( DEBUG_COLOR ) {
                System.err.println( "\n== ITERATION: " + i++ );
                System.err.println( "Stack:" );
                System.err.println( nodeCollectionToString(stack));
                System.err.println( "Graph:" );
                System.err.println( nodeCollectionToString(graph));
            }
        }

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

        public boolean color( int k ) {
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
