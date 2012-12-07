package mips.code;
import mips.register;
public abstract class RInstruction extends Instruction {
    Register RS;
    Register RD;
    Register RT;
    public InstructionType type() {
        return InstructionType.R;
    }
    public void setRS( Register r ) {
        RS = r;
    }
    public void setRD( Register r ) {
        RD = r;
    }
    public void setRT( Register r ) {
        RT = r;
    }
    public Register getRS( ) {
        return RS;
    }
    public Register getRD( ) {
        return RD;
    }
    public Register getRT( ) {
        return RT;
    }
}

