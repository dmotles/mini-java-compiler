package mips.code;
import mips.allocator.*;
public class LI extends IInstruction {
    public String name() {
        return "li";
    }
    public void setRT( Register r ) {
    }
    public Register getRT() {
        return null;
    }
}
