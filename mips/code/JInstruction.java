package mips.code;
import mips.register;
import visitor.symbol;
public abstract class JInstruction extends Instruction {
    Symbol immediate;
    public InstructionType type() {
        return InstructionType.J;
    }
    public void setImmediate( Symbol s ) {
        immediate = s;
    }
    public Symbol getImmediate() {
        return immediate;
    }
}

