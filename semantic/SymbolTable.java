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
class SymbolTree<T> {
    private static final int TOSTRING_INDENT_SPACES = 30;
    private static final String TOSTR_FMT = "%-" + TOSTRING_INDENT_SPACES
        + "s";

    SymbolTreeNode<T> root;
    SymbolTreeNode<T> curNode;

    public SymbolTree() {
        curNode = new SymbolTreeNode<T>();
        root = curNode;
    }
    public boolean push( String scope ) {
        boolean ret = false;
        SymbolTreeNode<T> next = curNode.descend( scope );
        if( next != null ) {
            ret = true;
            curNode = next;
        }
        return ret;
    }
    public boolean pop() {
        boolean ret = false;
        SymbolTreeNode<T> next = curNode.ascend();
        if( next != null ) {
            ret = true;
            curNode = next;
        }
        return ret;
    }
    public boolean addChildScope( String name ) {
        SymbolTreeNode<T> n = curNode.addChildScope( name );
        if( n != null ) {
            curNode = n;
            return true;
        }
        return false;
    }
    public T lookup( String id ) {
        return curNode.lookup( id );
    }

    public String toString() {
        return root.toString();
    }

    public boolean addSymbol( String id, T s ) {
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
    private class SymbolTreeNode<U> {
        HashMap< String, SymbolTreeNode<U> > children;
        HashMap< String, U > vars;
        SymbolTreeNode<U> parent;

        public SymbolTreeNode() {
            parent = null;
            children = new HashMap<String,SymbolTreeNode<U>>();
            vars = new HashMap< String, U >();
        }
        public SymbolTreeNode( SymbolTreeNode<U> p ) {
            this();
            parent = p;
        }
        public boolean addSymbol( String id, U s ) {
            boolean ret = false;
            String intern = id.intern();
            if( vars.get(intern) == null ) {
                ret = true;
                vars.put( id.intern(), s );
            }
            return ret;
        }
        public SymbolTreeNode<U> descend( String child ) {
            return children.get( child.intern() );
        }
        public SymbolTreeNode<U> ascend() {
            return parent;
        }
        public SymbolTreeNode<U> addChildScope( String name ) {
            String id = name.intern();
            SymbolTreeNode<U> existing = children.get( id );
            if( existing == null ) {
                SymbolTreeNode<U> n = new SymbolTreeNode<U>( this );
                children.put( id, n );
                return n;
            }
            return null; // child scope exists already.
        }
        public U lookup( String id ) {
            U v = vars.get( id.intern() );
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
            for( U v : vars.values() )
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
    private static final boolean DEBUG = true;
    private HashMap< String, ClassSymbol > classes;
    private HashMap< String, HashMap<String,MethodSymbol> > methods;
    private SymbolTree<VariableSymbol> vars;
    private String lastAddedClass;
    public SymbolTable() {
        classes = new HashMap<String, ClassSymbol>();
        methods = new HashMap<String, HashMap<String,MethodSymbol>>();
        vars = new SymbolTree<VariableSymbol>();
        lastAddedClass = null;
    }
    public VariableSymbol lookupVariable( String name ) {
        return vars.lookup( name );
    }

    public MethodSymbol lookupMethod( String classname, String method ) {
        HashMap<String, MethodSymbol> decl_set = methods.get( method.intern() );
        if( decl_set != null )
            return decl_set.get( classname.intern() );
        return null;
    }

    public ClassSymbol lookupClass( String name ) {
        return classes.get( name.intern() );
    }

    public void enterScope( String name ) {
        if( ! vars.push( name ) && DEBUG ) {
            System.err.println( "No scope exists at this level with name " + name );
        }
    }

    public void leaveScope() {
        if( ! vars.pop() && DEBUG ) {
            System.err.println( "You popped too many scopes off - you are already"
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
        MethodSymbol m = new MethodSymbol( id, line, col, ret );
        id = id.intern();
        HashMap< String, MethodSymbol > decl_map = methods.get( id );
        if( decl_map == null ) {
            decl_map = new HashMap< String, MethodSymbol >();
            decl_map.put( lastAddedClass, m );
            methods.put( id, decl_map );
        } else if( ! decl_map.containsKey( lastAddedClass ) ) {
            decl_map.put( lastAddedClass, m );
        } else {
            return null;
        }
        if( ! vars.addChildScope( id ) ) {
            throw new Error( "Fatal: could not add new child scope (already exists?) ");
        }
        return m;
    }

    public ClassSymbol addClass( String id, int line, int col ) {
        String intern = id.intern();
        if( ! classes.containsKey( intern ) ) {
            ClassSymbol c = new ClassSymbol( id, line, col );
            classes.put( intern, c );
            lastAddedClass = intern;
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
            lastAddedClass = intern;
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
        for( HashMap<String,MethodSymbol>decmap : methods.values() ) {
            for( MethodSymbol m : decmap.values() )
                sb.append( String.format("%s\n", m.toString() ) );
        }
        sb.append( "Variables:\n" );
        sb.append( vars.toString() );
        return sb.toString();
    }
}
