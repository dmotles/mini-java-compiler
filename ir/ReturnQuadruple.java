package ir;

import visitor.symbol.*;

public class ReturnQuadruple extends Quadruple
{
    public ReturnQuadruple(Symbol arg1)
    {
        argument1 = arg1;
    }

    public String toString ()
    {
        return "return " + argument1.getName();
    }
    public int quadType() {
        return Quadruple.RETURNQUADRUPLE;
    }
}
