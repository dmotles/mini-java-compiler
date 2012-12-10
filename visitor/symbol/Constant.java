package visitor.symbol;

import syntaxtree.*;

public class Constant extends Symbol {
    int constant;

    public Constant(Type t, int i)
    {
        type = t;
        constant = i;
        name = new Identifier(String.valueOf(i));
    }

    public String toString() {
        return name.s;
    }

    public void changeConstant( int i ) {
        constant = i;
        name = new Identifier( String.valueOf(i) );
    }

}
