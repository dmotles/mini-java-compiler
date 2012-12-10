package mips.allocator;

import java.util.*;
import visitor.symbol.*;
import ir.*;
import syntaxtree.*;
public class InterferenceGraph {
    HashMap<Symbol, Node> graphNodes;
    private static final boolean DEBUG_COLOR = false;
    private static final boolean DEBUG_COALESCE = true;
    private static final boolean DEBUG_SIMPLIFY = true;
    private int K;
    HashMap<Integer,Move> instMoveStatus;
    HashMap<Node,HashSet<Node>> adjSet;
    ArrayList<Quadruple> myIR;
    LivenessData liveness;
    Stack<Node> stack;

    public InterferenceGraph( MethodSymbol meth, LivenessData ld, ArrayList<Quadruple> ir, ArrayList<ColorAllocation> regs ) {
        myIR = ir;
        liveness = ld;
        K = 16;
        graphNodes = new HashMap<Symbol,Node>();
        instMoveStatus = new HashMap<Integer,Move>();
        adjSet = new HashMap<Node,HashSet<Node>>();
        build( ld, ir );
        makeWorklist( );
        stack = new Stack<Node>();

        do {
            if( !simplifyEmpty() ) {
                simplify(stack);
            } else if ( !workListEmpty() ) {
                coalesce();
            } else if ( !freezeEmpty() ) {
                freeze();
            } else if ( !spillWorkEmpty() ) {
                selectSpill();
            }

        }while( ! simplifyEmpty() && ! workListEmpty() && ! freezeEmpty() && ! spillWorkEmpty() );

    }

    private boolean workListEmpty() {
        for( Node n : graphNodes.values() ) {
            for( Move m : n.moves ) {
                if( m.status == MoveStatus.worklist ) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean simplifyEmpty() {
        for( Node n : graphNodes.values() ) {
            if( n.status == NodeStatus.simplifyWorklist ) {
                return true;
            }
        }
        return false;
    }

    private boolean freezeEmpty() {
        for( Node n : graphNodes.values() ) {
            if( n.status == NodeStatus.freezeWorklist ) {
                return true;
            }
        }
        return false;
    }

    private boolean spillWorkEmpty() {
        for( Node n : graphNodes.values() ) {
            if( n.status == NodeStatus.spillWorklist ) {
                return true;
            }
        }
        return false;
    }

    private void makeWorklist( ) {
        for( Node n : graphNodes.values() ) {
            if( n.status == NodeStatus.initial ) {
                if( n.degree() >= K ) {
                    n.status = NodeStatus.spillWorklist;
                } else if( n.moveRelated() ) {
                    n.status = NodeStatus.freezeWorklist;
                } else {
                    n.status = NodeStatus.simplifyWorklist;
                }
            }
        }
    }

    //private void preColorMethodParams( MethodSymbol meth ) {
        //ArrayList<VariableSymbol> params = meth.getParameters();
        //for( int i = 0; i < params.size(); i++ ) {
            //Register r = Register.MEM_STACK_FRAME;
            //if( i < 4 ) {
                //r = Register.valueOf( "a" + i );
            //}
            //preColorSymbol( params.get(i), r );
        //}
    //}

    private void build( LivenessData ld, ArrayList<Quadruple> ir ) {
        for( int i = ir.size()-1; i >= 0; i-- ) {
            Quadruple q = ir.get( i );
            HashSet<Symbol> live = ld.out.get(i);
            if( q.isCopy() ) {
                live.removeAll( ld.use.get(i) );
                HashSet<Symbol> useDefUnion = new HashSet<Symbol>( ld.def.get(i) );
                useDefUnion.addAll( ld.use.get(i) );
                Move copyMove = new Move( q, MoveStatus.worklist );
                for( Symbol s : useDefUnion ) {
                    Node n = getNode( s );
                    n.moves.add( copyMove );
                }
                instMoveStatus.put(Integer.valueOf(q.ID), copyMove );
            }
            for( Symbol def : ld.def.get(i) ) {
                Node defnode = getNode(def);
                for( Symbol l : live ) {
                    Node lnode = getNode(l);
                    addEdge(defnode,lnode);
                }
            }
        }
    }

    private void addEdge( Node u, Node v ) {
        if( u != v ) {
            addAdjSet(u,v);
            addAdjSet(u,v);
            if( u.status != NodeStatus.precolored ) {
                u.adjacent.add(v);
                u.degree++;
            }
            if( v.status != NodeStatus.precolored ) {
                v.adjacent.add(u);
                v.degree++;
            }
        }
    }

    private void addAdjSet( Node u, Node v ) {
        HashSet<Node> nset = adjSet.get(u);
        if( nset == null ) {
            nset = new HashSet<Node>();
            adjSet.put(u,nset);
        }
        nset.add(v);
    }

    private boolean adjEdgeExists( Node u, Node v ) {
        HashSet<Node> nset = adjSet.get(u);
        if( nset == null ) {
            return false;
        }
        return nset.contains(v);
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

    private void coalesce( ) {
        System.out.println("Attempting coalesce" );
        for( Move m : instMoveStatus.values() ) {
            if( m.status == MoveStatus.worklist ) {
                Node x = getNode( m.quad.getResult() );
                Node y = getNode( m.quad.getFirstArgument() );
                Node u = x;
                Node v = y;
                if( y.status == NodeStatus.precolored ) {
                    u = y;
                    v = x;
                }

                if( u == v ) {
                    m.status = MoveStatus.coalesced;
                    addWorkList( u );
                } else if( v.status == NodeStatus.precolored || adjEdgeExists(u,v) ) {
                    m.status = MoveStatus.constrained;
                    addWorkList(u);
                    addWorkList(v);
                } else {
                    boolean ok = true;
                    if( u.status == NodeStatus.precolored ) {
                        for( Node t : v.adjacent() ) {
                            if( ! OK(t,u) ) {
                                ok = false;
                                break;
                            }
                        }
                    } else {
                        HashSet<Node> uvUnion = u.adjacent();
                        uvUnion.addAll(v.adjacent());
                        ok = conservative( uvUnion );
                    }
                    if(ok) {
                        m.status = MoveStatus.coalesced;
                        combine(u,v);
                        addWorkList(u);
                    } else {
                        m.status = MoveStatus.active;
                    }
                }
            }
        }
    }

    private void freeze() {
        for( Node u : graphNodes.values() ) {
            if( u.status == NodeStatus.freezeWorklist ) {
                u.status = NodeStatus.simplifyWorklist;
                u.freezeMoves();
                break;
            }
        }
    }

    private void combine( Node u , Node v ) {
        v.status = NodeStatus.coalesced;
        u.coalesceNode(v);
    }

    public boolean simplify( Stack<Node> stack ) {
        for( Node n : graphNodes.values() ){
            if( n.status == NodeStatus.simplifyWorklist ) {
                n.status = NodeStatus.stackTmp;
                stack.push( n );
                for( Node m : n.adjacent()) {
                    m.decrementDegree();
                }
                return true;
            }
        }
        return false;
    }


    private void selectSpill() {
        Node m = getSpillNode();
        m.status = NodeStatus.simplifyWorklist;
        m.freezeMoves();
    }

    private Node getSpillNode() {
        for( Node n : graphNodes.values() ) {
            if( n.status == NodeStatus.spillWorklist ) {
                return n;
            }
        }
        return null;
    }

    public boolean assignColors( Collection<ColorAllocation> colors ) {
        boolean noSpills = true;
        while( ! stack.empty() ) {
            Node n = stack.pop();
            HashSet<ColorAllocation> acceptableColors = new HashSet<ColorAllocation>(colors);
            for( Node w : n.adjacent ) {
                if( w.status == NodeStatus.colored || w.status == NodeStatus.precolored ) {
                    acceptableColors.remove( w.color );
                }
            }
            if( acceptableColors.size() == 0 ) {
                n.status = NodeStatus.spilled;
                noSpills = false;
            } else {
                n.status = NodeStatus.colored;
                n.color = acceptableColors.iterator().next();
            }
        }
        return noSpills;
    }

    public ArrayList<Quadruple> rewriteProgram() {
        int numSpills = 0;
        ArrayList<Quadruple> newIR = new ArrayList<Quadruple>(myIR);
        for( Node n : graphNodes.values( ) ) {
            if( n.status == NodeStatus.spilled && n.color == null ) {
                n.color = new ColorAllocation(
                        Register.MEM_STACK_FRAME,
                        new MemorySymbol(
                            Register.MEM_STACK_FRAME,
                            n.symbol,
                            new Identifier(
                                "_MEMSPILL"+numSpills
                                ),
                            numSpills
                            )
                        );
            }
        }

        for( int i = myIR.size() - 1; i >= 0; i-- ){
            Quadruple inst = newIR.get(i);
            ArrayList<Quadruple> newinst = new ArrayList<Quadruple>();
            for( Symbol use : liveness.use.get(i) ) {
                Node unode = getNode(use );
                if( unode.status == NodeStatus.spilled ) {
                    newinst.add(0, new CopyQuadruple( unode.color.getSymbol(), use ) );
                }
            }
            if( i > 0 ) {
                for( Symbol def : liveness.def.get(i-1) ) {
                    Node dnode = getNode(def );
                    if( dnode.status == NodeStatus.spilled ) {
                        newinst.add(0, new CopyQuadruple( def, dnode.color.getSymbol() ) );
                    }
                }
            }

            if( newinst.size() > 0 ) {
                newIR.addAll( i, newinst );
            }

        }

        return newIR;

    }

    private void addWorkList(Node n ) {
        if( n.status != NodeStatus.precolored && !( n.moveRelated() && n.degree < K ) )
            n.status = NodeStatus.simplifyWorklist;
    }

    private boolean OK( Node t, Node r ) {
        return t.degree < K || t.status == NodeStatus.precolored || adjEdgeExists(t,r);
    }

    private boolean conservative( Collection<Node> nodes ) {
        int k = 0;
        for( Node n : nodes ) {
            if( n.degree >= K ) k++;
        }
        return ( k < K );
    }

    private enum MoveStatus {
        coalesced, constrained, frozen, worklist, active;
    }

    private class Move {
        public Quadruple quad;
        public MoveStatus status;
        public Move( Quadruple target, MoveStatus stat ) {
            quad = target;
            status = stat;
        }
        public Move( Quadruple target ) {
            quad = target;
            status = MoveStatus.active;
        }

    }

    private enum NodeStatus {
        initial, precolored, simplifyWorklist, freezeWorklist, spillWorklist, spilled, coalesced, colored, stackTmp;
    }

    private class Node implements Comparable<Node>{
        public Symbol symbol;
        public HashSet<Node> adjacent;
        public ColorAllocation color;
        public NodeStatus status;
        public ArrayList<Move> moves;
        public int degree;
        private Node coalescedNode;

        Node( Symbol s ) {
            symbol = s;
            adjacent = new HashSet<Node>();
            color = null;
            status = NodeStatus.initial;
            moves = new ArrayList<Move>();
            degree = 0;
        }

        public int compareTo(Node o) {
            return this.degree() - o.degree();
        }

        public void coalesceNode( Node v ) {
            coalescedNode = v;
            graphNodes.put( v.symbol, this );
            moves.addAll(v.moves);
            enableMoves(v);
            for( Node t : v.adjacent() ) {
                addEdge(t,v);
                t.decrementDegree();
            }
            if( degree >= K && status == NodeStatus.freezeWorklist ) {
                status = NodeStatus.spillWorklist;
            }
        }

        public int degree() {
            return degree;
        }

        public void remove( int k ){
            HashSet<Node> adj = adjacent();
            for( Node n : adj ) {
                if( n.degree == k ) {
                    HashSet<Node> adjUnion = n.adjacent();
                    adjUnion.add(n);
                    enableMoves( adjUnion );
                    if( n.moveRelated() ) {
                        n.status = NodeStatus.freezeWorklist;
                    } else {
                        n.status = NodeStatus.simplifyWorklist;
                    }
                }
                n.degree--;
            }
        }

        public void decrementDegree() {
            if( degree == K ) {
                HashSet<Node> adjUnion = adjacent();
                adjUnion.add(this);
                enableMoves( adjUnion );
                if( moveRelated() ) {
                    status = NodeStatus.freezeWorklist;
                } else {
                    status = NodeStatus.simplifyWorklist;
                }
            }
        }
        private void enableMoves( Node n ) {
            for( Move m : n.moves() ) {
                if( m.status == MoveStatus.active ) {
                    m.status = MoveStatus.worklist;
                }
            }
        }

        private void enableMoves( HashSet<Node> nodes ) {
            for( Node n : nodes ) {
                enableMoves(n);
            }
        }

        public HashSet<Node> adjacent() {
            HashSet<Node> adj = new HashSet<Node>();
            for( Node n : adjacent ){
                if( n.status == NodeStatus.stackTmp || n.status == NodeStatus.coalesced ) {
                    continue;
                }
                adj.add(n);
            }
            return adj;
        }

        public void add() {
            for( Node n : adjacent ) {
                n.adjacent.add( this );
            }
        }

        public String toString() {
            return symbol.getName().toString();
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

        public boolean moveRelated() {
            return moves().size() > 0;
        }

        public HashSet<Move> moves() {
            HashSet<Move> mv = new HashSet<Move>();
            for( Move m : moves ) {
                if( m.status == MoveStatus.active || m.status == MoveStatus.worklist ){
                    mv.add(m);
                }
            }
            return mv;
        }

        public void freezeMoves() {
            for( Move m : moves() ) {
                Node y = getNode( m.quad.getFirstArgument());
                Node v = y;
                if( y == this ) {
                    v = getNode( m.quad.getResult() );
                }
                m.status = MoveStatus.frozen;
                if( v.status == NodeStatus.freezeWorklist && v.moves().size() == 0 ) {
                    v.status = NodeStatus.simplifyWorklist;
                }
            }
        }
    }
}
