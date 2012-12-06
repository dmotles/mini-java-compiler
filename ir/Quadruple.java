package ir;

import visitor.symbol.*;
import java.util.*;

public abstract class Quadruple implements CodeEncodable
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

    public boolean isCopy() {
        return false;
    }

    public boolean isDef() {
        return true;
    }

    public abstract String toString ();
}
