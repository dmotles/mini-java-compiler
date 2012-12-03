package visitor.symbol;

import syntaxtree.*;

public class VariableSymbol extends Symbol
{
	public VariableSymbol(Type t, Identifier n)
	{
		type = t;
		name = n;
	}

	public String toString() {
		return type.toString() + " " + name;
	}

}