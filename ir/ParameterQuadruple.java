package ir;
import visitor.symbol.*;

public class ParameterQuadruple extends Quadruple
{
    public ParameterQuadruple(Symbol arg1)
    {
        argument1 = arg1;
    }

    public String toString ()
    {
        return "param " + argument1.getName();
    }

    public boolean isDef() {
        return false;
    }


    public int quadType() {
        return Quadruple.PARAMETERQUADRUPLE;
    }
}
