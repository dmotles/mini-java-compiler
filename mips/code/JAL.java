package mips.code;
import mips.allocator.*;
public class JAL extends JInstruction {
    public String name() {
        return "jal";
    }
    public String toString() {
        return name() + " " + immediate.getName();
    }
}
