package mips.allocator;
import java.util.*;
import visitor.symbol.*;
import syntaxtree.*;

public class ClassFootprintMap {
    private HashMap<String,Integer> sizemap;
    private HashMap<String,HashMap<String,Integer>> offsetmap;
    private SymbolTable symboltable;
    public ClassFootprintMap( SymbolTable st ) {
        symboltable = st;
        sizemap = new HashMap<String, Integer>();
        offsetmap = new HashMap<String,HashMap<String,Integer>>();
    }

    /**
     * Calculates the amount of space we need to allocated for the class and where the
     * instance variables are going to live inside the allocated space.
     */
    public void addMapping( String className ) {
        ClassSymbol cs = symboltable.getClassByName( new Identifier( className ) );
        if( cs != null ) {
            ArrayList<VariableSymbol> vars = cs.getVariables();
            int structsize = vars.size() * 4; // class is 4 times the number of variables we need to store
            sizemap.put( className, Integer.valueOf(structsize) );
            HashMap<String,Integer> offsets = new HashMap<String,Integer>();
            for( int i = 0; i < vars.size(); i++ ) {
                offsets.put( vars.get(i).getName().toString(), Integer.valueOf(i*4) );
            }
            offesetmap.put( className, offsets );
        }
    }

    /**
     * Returns the amount of space we need to allocate for the class.
     */
    public Integer getSize( String className ) {
        return sizemap.get( className );
    }

    /**
     * Returns the offset of where a variable is inside the class struct.
     */
    public Integer getOffset( String className, String varName ) {
        return offsetmap.get( className ).get( varName );

    }
}
