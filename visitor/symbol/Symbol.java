package visitor.symbol;

import syntaxtree.*;

public abstract class Symbol
{
    protected Type type;
    protected Identifier name;

    public Type getType()
    {
        return type;
    }

    public Identifier getName() {
        return name;
    }

    public abstract String toString();
}
