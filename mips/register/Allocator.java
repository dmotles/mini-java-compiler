package mips.register;

public class Allocator {
    private static final Register [] registers = Register.values();
    private static final BitSet reserved;
    private static final BitSet callerSaved;
    private static final BitSet calleeSaved;
    private static final BitSet arguments;
    private static final BitSet returnValues;
    private static final BitSet GeneralPurpose;
    private BitSet table;

    public Allocator() {
        table = new BitSet( registers.length );
        table.set(0);   //$zero
        table.set(1);   //$at
        table.set(26);  //$k0
        table.set(27);  //$k1
        table.set(28);  //$gp
        table.set(29);  //$sp
        table.set(31);  //$ra
    }


    public Register register( int index ) {
        return registers[index];

    }
}
