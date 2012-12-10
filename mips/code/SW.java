package mips.code;
import mips.allocator.*;
public class SW extends IInstruction {
    public String name() {
        return "sw";
    }
    public String name() {
        return name() + " $" + RT + " " + immediate + "($" + RS + ")";
    }
}
