package mips.allocator;
import visitor.symbol.*;
import syntaxtree.*;

public class MemorySymbol extends Symbol {
    Symbol v;
    int offset;
    Register r;
    public MemorySymbol( Register r, Symbol old, Identifier i, int offset ) {
        this.r = r;
        this.v = old;
        name = i;
        type = v.getType();
        this.offset = offset;
    }

    public Symbol getSymbol() {
        return v;
    }

    public int getOffset() {
        return offset;
    }

    public Register getRegister() {
        return r;
    }

    public String toString() {
        return type.toString() + " " + name;
    }

}

