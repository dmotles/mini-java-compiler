package ir;
import visitor.symbol.*;

public class AssignmentQuadruple extends Quadruple
{	
	public static final int ADD = 0;
	public static final int SUB = 1;
	public static final int MUL = 2;
	public static final int LT = 3;
	public static final int AND = 4;

	private int operator;
	private String[] operators = {"+", "-", "*", "<", "&&"} ;

	public AssignmentQuadruple (int op, Symbol arg1, Symbol arg2, Symbol r)
	{
		operator = op;
		argument1 =	arg1;
		argument2 =	arg2;
		result = r;
	}
	
	private int getOperator() {
		return operator;
	}

	public String toString ()
	{
		return result.getName() + " := " + argument1.getName() + " " + operators[operator] + " " + argument2.getName();
	}
}