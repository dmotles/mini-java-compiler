package ir;

import visitor.symbol.*;

public abstract class Quadruple	
{
	protected Symbol argument1;
	protected Symbol argument2;
	protected Symbol result;

	public Symbol getFirstArgument() 
	{
		return argument1;
	}
	
	public Symbol getSecondArgument() 
	{
		return argument2;
	}

	public Symbol getResult()
	{
		return result;
	}

	public abstract String toString ();
}
