package mips.code;
public class Label extends Instruction {
    String label;

    public Label( String l ) {
        label = l;
    }

    public InstructionType type() {
        return InstructionType.LABEL;
    }

    public String name() {
        return label;
    }

    public String toString() {
        return label + ":";
    }

    public int hashCode () {
        return label.hashCode();
    }

    public boolean equals( Object o ) {
        if( o instanceof Label ) {
            return label.equals( ((Label)o).label );
        }
        return false;
    }
}
