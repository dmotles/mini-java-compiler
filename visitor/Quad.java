package visitor;
import syntaxtree.*;

public class Quad {
    public String result, operan, var1, var2;

    public Quad(String res, String op, String v1, String v2)
    {
        result = res;
        operan = op;
        var1 = v1;
        var2 = v2;
    }

    public String toString() {
        return "quad( " + result + ", " + operan + " , " + var1 + ", " + var2 + ")";
    }

}
