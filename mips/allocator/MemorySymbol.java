package mips.allocator;
import visitor.symbol.*;
import syntaxtree.*;

public class MemorySymbol extends Symbol {
    VariableSymbol v;
    public MemorySymbol( VariableSymbol v ) {
        this.v = v;
        name = new Identifier( "mem[" + v.getName().toString() + "]" );
        type = v.getType();
    }

    public VariableSymbol getVarSymbol() {
        return v;
    }

    public String toString() {
        return type.toString() + " " + name;
    }

}

