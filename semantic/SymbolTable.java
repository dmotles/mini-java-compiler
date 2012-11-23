package semantic;
import java.util.HashMap;
import syntaxtree.Type;


/**
 * Represents the "tree" of symbols for variables (methods and classes are handled seperately).
 *
 * A lookup will always return the closest scoped variable.
 *
 * @author Daniel Motles
 * @version 0.1
 */
class VariableSymbolTree {
    private static final int TOSTRING_INDENT_SPACES = 30;
    private static final String TOSTR_FMT = "%-" + TOSTRING_INDENT_SPACES
        + "s";

    VariableSymbolTreeNode root;
    VariableSymbolTreeNode curNode;

    public VariableSymbolTree() {
        curNode = new VariableSymbolTreeNode();
        root = curNode;
    }
    public boolean push( String scope ) {
        boolean ret = false;
        VariableSymbolTreeNode next = curNode.descend( scope );
        if( next != null ) {
            ret = true;
            curNode = next;
        }
        return ret;
    }
    public boolean pop() {
        boolean ret = false;
        VariableSymbolTreeNode next = curNode.ascend();
        if( next != null ) {
            ret = true;
            curNode = next;
        }
        return ret;
    }
    public boolean addChildScope( String name ) {
        VariableSymbolTreeNode n = curNode.addChildScope( name );
        if( n != null ) {
            curNode = n;
            return true;
        }
        return false;
    }
    public VariableSymbol lookup( String id ) {
        return curNode.lookup( id );
    }

    public String toString() {
        return root.toString();
    }

    public boolean addSymbol( String id, VariableSymbol s ) {
        return curNode.addSymbol( id, s );
    }

    /**
     * Internal representation of a tree node.
     *
     * Uses HashMap to refer to children.
     *
     * @author Daniel Motles
     * @version 0.1
     */
    private class VariableSymbolTreeNode {
        HashMap< String, VariableSymbolTreeNode > children;
        HashMap< String, VariableSymbol > vars;
        VariableSymbolTreeNode parent;

        public VariableSymbolTreeNode() {
            parent = null;
            children = new HashMap<String,VariableSymbolTreeNode>();
            vars = new HashMap< String, VariableSymbol >();
        }
        public VariableSymbolTreeNode( VariableSymbolTreeNode p ) {
            this();
            parent = p;
        }
        public boolean addSymbol( String id, VariableSymbol s ) {
            boolean ret = false;
            String intern = id.intern();
            if( vars.get(intern) == null ) {
                ret = true;
                vars.put( id.intern(), s );
            }
            return ret;
        }
        public VariableSymbolTreeNode descend( String child ) {
            return children.get( child.intern() );
        }
        public VariableSymbolTreeNode ascend() {
            return parent;
        }
        public VariableSymbolTreeNode addChildScope( String name ) {
            String id = name.intern();
            VariableSymbolTreeNode existing = children.get( id );
            if( existing == null ) {
                VariableSymbolTreeNode n = new VariableSymbolTreeNode( this );
                children.put( id, n );
                return n;
            }
            return null; // child scope exists already.
        }
        public VariableSymbol lookup( String id ) {
            VariableSymbol v = vars.get( id.intern() );
            if( v != null ) {
                return v;
            } else {
                if( parent != null )
                    return parent.lookup( id );
            }
            return null;
        }


        public String toString() {
            return toString(0);
        }

        private String toString( int depth ) {
            StringBuilder sb = new StringBuilder();
            for( VariableSymbol v : vars.values() )
                sb.append( String.format("%s" + TOSTR_FMT
                            + "\n", tabs(depth), v.toString() ) );
            for( String k : children.keySet() ) {
                sb.append( String.format("%s" +  TOSTR_FMT
                            + "\n", tabs(depth), k+" ========>") );
                sb.append( children.get(k).toString(depth+1) );
            }
            return sb.toString();
        }

        private String tabs( int depth ) {
            if( depth > 0 ) {
                StringBuilder sb = new StringBuilder( 20* depth );
                for( int i = 0; i < depth; i++ )
                    sb.append( String.format( TOSTR_FMT, " " ) );
                return sb.toString();
            }
            return "";
        }
    }
}

/**
 * Represents the FULL symboltable, including methods and classes.
 *
 * @author Daniel Motles
 * @version 0.1
 */
public class SymbolTable {
    private HashMap< String, ClassSymbol > classes;
    private HashMap< String, MethodSymbol > methods;
    private VariableSymbolTree vars;
    private String lastAddedClass;
    public SymbolTable() {
        classes = new HashMap<String, ClassSymbol>();
        methods = new HashMap<String, MethodSymbol>();
        vars = new VariableSymbolTree();
        lastAddedClass = null;
    }
    public VariableSymbol lookupVariable( String name ) {
        return vars.lookup( name );
    }

    public MethodSymbol lookupMethod( String classname, String method ) {
        String lookuptag = classname.concat( method );
        return methods.get( lookuptag.intern() );
    }

    public ClassSymbol lookupClass( String name ) {
        return classes.get( name.intern() );
    }

    public void enterScope( String name ) {
        if( ! vars.push( name ) ) {
            throw new Error( "No scope exists at this level with name" + name );
        }
    }

    public void leaveScope() {
        if( ! vars.pop() ) {
            throw new Error( "You popped too many scopes off - you are already"
                    + " at the most outer scope. " );
        }
    }

    public VariableSymbol addVariable( String id, Type t, int line, int col ) {
        VariableSymbol v = new VariableSymbol( id, line, col, t );
        if( vars.addSymbol( id, v ) ) {
            return v;
        }
        return null;
    }
    public MethodSymbol addMethod( String id, Type ret, int line, int col ) {
        String methodtag = lastAddedClass + id;
        String intern = methodtag.intern();
        MethodSymbol m = new MethodSymbol( methodtag, line, col, ret );
        if( methods.get( intern ) == null ) {
            methods.put( intern, m );
            if( ! vars.addChildScope( id ) ) {
                throw new Error( "Fatal: could not add new child scope (already exists?) ");
            }
            return m;
        }
        return null;
    }

    public ClassSymbol addClass( String id, int line, int col ) {
        String intern = id.intern();
        if( ! classes.containsKey( intern ) ) {
            ClassSymbol c = new ClassSymbol( id, line, col );
            classes.put( intern, c );
            lastAddedClass = id;
            if( ! vars.addChildScope( id ) ) {
                throw new Error( "Fatal: could not add new child scope (already exists?) ");
            }
            return c;
        }
        return null;
    }

    public ClassSymbol addClass( String id, int line, int col, String e ) {
        String intern = id.intern();
        if( ! classes.containsKey( intern ) ) {
            ClassSymbol c = new ClassSymbol( id, line, col, e );
            classes.put( intern, c );
            lastAddedClass = id;
            if( ! vars.addChildScope( id ) ) {
                throw new Error( "Fatal: could not add new child scope (already exists?) ");
            }
            return c;
        }
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "Classes:\n" );
        for( ClassSymbol c : classes.values() ) {
            sb.append( String.format("%s\n", c.toString() ) );
        }
        sb.append( "Methods:\n" );
        for( MethodSymbol m : methods.values() ) {
            sb.append( String.format("%s\n", m.toString() ) );
        }
        sb.append( "Variables:\n" );
        sb.append( vars.toString() );
        return sb.toString();
    }
}
