package ir;
import visitor.symbol.*;

public class ArrayAssignmentQuadruple extends Quadruple
{	
	public ArrayAssignmentQuadruple (Symbol arg1, Symbol arg2, Symbol r)
	{
		argument1 =	arg1;
		argument2 =	arg2;
		result = r;
	}

	public String toString ()
	{
		return result.getName() + "[ " + argument2.getName() + " ] := " + argument2.getName();
	}
}