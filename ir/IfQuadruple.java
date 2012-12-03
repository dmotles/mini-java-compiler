package ir;

import visitor.symbol.*;

public class IfQuadruple extends Quadruple
{	
	public IfQuadruple(Symbol arg1, Symbol r)
	{
		argument1 =	arg1;
		result = r;
	}
	
	public String toString ()
	{
		return "iftrue " + argument1.getName() + " goto " + result;
	}
}