package mips.code;
enum InstructionType { R,I,J };
public abstract class Instruction {
    public abstract InstructionType type();
    public abstract String name();
    public abstract String toString();
}
