package mips.code;
import mips.allocator.*;
public class NOT extends IInstruction {
    public String name() {
        return "not";
    }
    public void setImmediate( Symbol s ) {
    }
    public Symbol getImmediate() {
        return null;
    }
}
