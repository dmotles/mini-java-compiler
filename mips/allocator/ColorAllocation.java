package mips.allocator;
import java.util.*;
import ir.*;
import visitor.symbol.*;
import syntaxtree.*;

public class ColorAllocation {
    Register r;
    Symbol sym;
    public ColorAllocation( Register r, Symbol s ) {
        this.r = r;
        this.sym = sym;
    }

    public Symbol getSymbol() {
        return sym;
    }

    public Register getRegister() {
        return r;
    }
}
