package ir;

import visitor.symbol.*;
import java.util.*;

public abstract class Quadruple
{
    protected Symbol argument1;
    protected Symbol argument2;
    protected Symbol result;
    public final static int ARRAYASSIGNMENTQUADRUPLE = 0;
    public final static int ARRAYLENGTHQUADRUPLE = 1;
    public final static int ARRAYLOOKUPQUADRUPLE = 2;
    public final static int ASSIGNMENTQUADRUPLE = 3;
    public final static int CALLQUADRUPLE = 4;
    public final static int COPYQUADRUPLE = 5;
    public final static int GOTOQUADRUPLE = 6;
    public final static int IFQUADRUPLE = 7;
    public final static int NEWARRAYQUADRUPLE = 8;
    public final static int NEWOBJECTQUADRUPLE = 9;
    public final static int PARAMETERQUADRUPLE = 10;
    public final static int RETURNQUADRUPLE = 11;
    public final static int UNARYASSIGNMENTQUADRUPLE = 12;

    public HashSet<Quadruple> succ = new HashSet<Quadruple>();
    public HashSet<Quadruple> pred = new HashSet<Quadruple>();
    public int ID = -1;

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

    public boolean isCopy() {
        return false;
    }

    public boolean isDef() {
        return true;
    }

    public abstract int quadType();

    public abstract String toString ();
}
