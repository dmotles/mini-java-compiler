package ir;

import visitor.symbol.*;

public class ArrayLengthQuadruple extends Quadruple
{
    public ArrayLengthQuadruple (Symbol arg1, Symbol r)
    {
        argument1 =	arg1;
        result = r;
    }

    public String toString ()
    {
        return result.getName() + " := length " + argument1.getName();
    }
    public int quadType() {
        return Quadruple.ARRAYLENGTHQUADRUPLE;
    }
}
