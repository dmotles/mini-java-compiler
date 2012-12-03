package ir;

import visitor.symbol.*;

public class GotoQuadruple extends Quadruple
{	

	public GotoQuadruple(Symbol r)
	{
		result = r;
	}
	
	public String toString ()
	{
		return "goto " + result;
	}
}