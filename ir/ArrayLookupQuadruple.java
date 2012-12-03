package ir;
import visitor.symbol.*;

public class ArrayLookupQuadruple extends Quadruple
{	
	public ArrayLookupQuadruple (Symbol arg1, Symbol arg2, Symbol r)
	{
		argument1 =	arg1;
		argument2 =	arg2;
		result = r;
	}

	public String toString ()
	{
		return result.getName() + " := " + argument1.getName() + "[ " + argument2.getName() + " ]";
	}
}