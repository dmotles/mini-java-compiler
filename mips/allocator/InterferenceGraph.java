package mips.allocator;

import java.util.*;
import visitor.symbol.*;
import ir.*;
public class InterferenceGraph {
    HashMap<Symbol, Node> graphNodes;
    private static final boolean DEBUG_COLOR = true;

    public InterferenceGraph( MethodSymbol meth, LivenessData ld, ArrayList<Quadruple> ir ) {
        graphNodes = new HashMap<Symbol,Node>();
        for( int i = 0; i < ir.size(); i++ ) {
            Quadruple q = ir.get( i );
            if( q.isDef() ) {
                Symbol def = q.getResult();
                HashSet<Symbol> outset = ld.out.get(i);
                for( Symbol liveout : outset ) {
                    if( def != liveout ) {
                        if( !q.isCopy() || q.getFirstArgument() != liveout ) {
                            Node defnode = getNode( (Symbol)def );
                            Node outnode = getNode( liveout );
                            defnode.adjacent.add( outnode );
                            outnode.adjacent.add( defnode );
                        }
                    }
                }
            }
        }
        ArrayList<VariableSymbol> params = meth.getParameters();
        switch( params.size() ) {
            default:
            case 4:
                preColorSymbol(params.get(3),Register.a3);
                //fall-through
            case 3:
                preColorSymbol(params.get(2),Register.a2);
                //fall-through
            case 2:
                preColorSymbol(params.get(1),Register.a1);
                //fall-through
            case 1:
                preColorSymbol(params.get(0),Register.a0);
                //fall-through
            case 0:
                break;
        }
    }

    public void preColorSymbol( Symbol s, Register r ) {
        getNode(s).preColor(r);
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

    public void color( Collection<Register> allowedRegisters ) {
        Stack<Node> stack = new Stack<Node>();
        HashSet<Node> graph = new HashSet<Node>( graphNodes.values() );
        int i = 1;
        boolean removedSomething = true;
        while( removedSomething ) {
            removedSomething = false;
            // find node < degree
            for( Node n : graph ){
                if( n.degree() < allowedRegisters.size() ) {
                    if( ! n.precolored ) {
                        graph.remove( n );
                        n.remove();
                        //add to stack
                        stack.push( n );
                        removedSomething = true;
                        break;
                    }
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
            n.color( allowedRegisters );
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
        public Register register;
        public boolean precolored;
        public boolean spillCandidate;

        Node( Symbol s ) {
            symbol = s;
            adjacent = new HashSet<Node>();
            register = null;
            spillCandidate = false;
            precolored = false;
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

        public void preColor( Register r ){
            register = r;
            precolored = true;
        }

        public boolean color( Collection<Register> allowedRegisters ) {
            if( !precolored ) {
                for( Register r : allowedRegisters ) {
                    boolean available = true;
                    for( Node n : adjacent ) {
                        if( n.register == r) {
                            available = false;
                            break;
                        }
                    }
                    if( available ) {
                        register = r;
                        break;
                    }
                }
                if( register == null ) return true;
                return false;
            }
            return true;
        }

        public String toString() {
            String reg = (register==null)?"none":register.toString();

            return String.format( "%s Register: %s Degree: %d Adj:%s",
                    symbol.getName(),
                    reg,
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
