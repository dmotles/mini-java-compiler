package ir;

import visitor.symbol.*;

public class NewArrayQuadruple extends Quadruple
{
    public NewArrayQuadruple (Symbol arg1, Symbol r)
    {
        argument1 =	arg1;
        result = r;
    }

    public String toString ()
    {
        return result.getName() + " := new int [" + argument1.getName() + "]";
    }
}
