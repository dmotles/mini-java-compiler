package ir;

import visitor.symbol.*;

public class CallQuadruple extends Quadruple
{
    boolean isStatic = false;

    public CallQuadruple (Symbol arg1, Symbol r)
    {
        argument1 =	arg1;
        result = r;
    }

    public CallQuadruple (Symbol arg1, Symbol r, boolean b)
    {
        argument1 =	arg1;
        result = r;
        isStatic = b;
    }

    public String toString ()
    {
        int parameters = ((MethodSymbol)argument1).getParameters().size();
        if(!isStatic) {
            //Add in the parameter for this
            parameters ++;
        }

        return ((result == null) ? "" : result.getName() + " := " )
            + "call "
            + argument1.getName()
            + ", "
            + parameters;
    }
    public int quadType() {
        return Quadruple.CALLQUADRUPLE;
    }
}
