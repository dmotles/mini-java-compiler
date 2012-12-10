package mips.code;
import mips.allocator.*;
public class MOVE extends IInstruction {
    public String name() {
        return "move";
    }
    public void setImmediate( Symbol s ) {
    }
    public Symbol getImmediate() {
        return null;
    }
}
