package ir;

import visitor.symbol.*;

public class UnaryAssignmentQuadruple extends Quadruple
{	
	public static final int NOT = 0;
	
	private int operator;
	private String[] operators = {"!"} ;

	public UnaryAssignmentQuadruple (int op, Symbol arg1, Symbol r)
	{
		operator = op;
		argument1 =	arg1;
		result = r;
	}

	public String toString ()
	{
		return result.getName() + " := " + operators[operator] + " " + argument1.getName();
	}

	private int getOperator() {
		return operator;
	}
}