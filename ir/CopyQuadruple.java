package ir;

import visitor.symbol.*;

public class CopyQuadruple extends Quadruple
{
    public CopyQuadruple (Symbol arg1, Symbol r)
    {
        argument1 =	arg1;
        result = r;
    }

    public boolean isCopy() {
        return true;
    }

    public String toString ()
    {
        return result.getName() + " := " + argument1.getName();
    }
}
