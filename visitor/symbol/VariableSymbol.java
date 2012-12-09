package visitor.symbol;

import syntaxtree.*;

public class VariableSymbol extends Symbol
{
    private static final boolean VARHASH_DEBUG = true;
    public VariableSymbol(Type t, Identifier n)
    {
        type = t;
        name = n;
    }

    public String toString() {
        String hashcode = "";
        if( VARHASH_DEBUG ) hashcode = " HASH: " + Integer.toString( this.hashCode() );
        return type.toString() + " " + name + hashcode;
    }

    public boolean equals( Object o ) {
        if( o instanceof VariableSymbol ) {
            Type t = ((VariableSymbol)o).getType();
            Identifier i = ((VariableSymbol)o).getName();
            return type.equals(t) && name.equals(i);
        }
        return false;
    }

    public int hashCode() {
        String cat = type.toString() + " " + name.s;
        return cat.hashCode();
    }

}
