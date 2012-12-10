package mips.code;
import mips.allocator.*;
public class LW extends IInstruction {
    public String name() {
        return "lw";
    }
    public String name() {
        return name() + " $" + RT + " " + immediate + "($" + RS + ")";
    }
}
