package mips.code;
import mips.register.*;
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
