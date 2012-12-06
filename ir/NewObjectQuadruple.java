package ir;

import visitor.symbol.*;

public class NewObjectQuadruple extends Quadruple
{
    public NewObjectQuadruple (Symbol arg1, Symbol r)
    {
        argument1 =	arg1;
        result = r;
    }

    public String toString ()
    {
        return result.getName() + " := new " + argument1.getType() + "()";
    }
}
