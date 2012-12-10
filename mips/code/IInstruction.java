package mips.code;
import mips.allocator.*;
import visitor.symbol.*;
public abstract class IInstruction extends Instruction {
    Register RS;
    Register RT;
    Symbol immediate;
    public InstructionType type() {
        return InstructionType.I;
    }
    public void setRS( Register r ) {
        RS = r;
    }
    public void setRT( Register r ) {
        RT = r;
    }
    public void setImmediate( Symbol s ) {
        immediate = s;
    }
    public Register getRS( ) {
        return RS;
    }
    public Register getRT( ) {
        return RT;
    }
    public Symbol getImmediate() {
        return immediate;
    }
    public String toString() {
        return name() + " $" + RS.toString() + ", $" + RT.toString() + ", " + immediate.getName();
    }
}
