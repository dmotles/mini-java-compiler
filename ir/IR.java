package ir;
import java.util.*;
import syntaxtree.*;

public class IR {
    HashMap<Identifier,HashMap<Identifier,ArrayList<Quadruple>>> IRmap;
    Identifier mainclass;
    ArrayList<Quadruple> main;


    public IR() {
        IRmap = new HashMap<Identifier,HashMap<Identifier,ArrayList<Quadruple>>>();
        mainclass = null;
        main = null;
    }

    public void addClass( Identifier c ) {
        IRmap.put( c, new HashMap<Identifier,ArrayList<Quadruple>>());
    }

    public void addMethod( Identifier c, Identifier m, ArrayList<Quadruple> ir ) {
        IRmap.get(c).put(m,ir);
    }

    public void addMainClass( Identifier c ) {
        if( mainclass == null ) {
            addClass(c);
            mainclass = c;
        }
    }

    public void addMainMethod( Identifier m, ArrayList<Quadruple> ir ) {
        if( main == null ) {
            IRmap.get(mainclass).put( m, ir );
            main = ir;
        }
    }

    public HashMap<Identifier,ArrayList<Quadruple>> getClass( Identifier c ) {
        return IRmap.get(c);
    }

    public ArrayList<Quadruple> getMethod( Identifier c, Identifier m ) {
        return IRmap.get(c).get(m);
    }

    public Set<Identifier> classes() {
        return IRmap.keySet();
    }

    public void dump() {
        System.out.println("main:");
        for(int i=0; i<main.size();i++) {
            System.out.println("\t" + i + ": " + main.get(i));
        }

        for( Identifier c : IRmap.keySet() ) {
            if( ! c.equals(mainclass) ) {
                HashMap<Identifier,ArrayList<Quadruple>> methodMap = IRmap.get(c);
                for( Identifier m : methodMap.keySet() ) {
                    ArrayList<Quadruple> ir = methodMap.get(m);
                    System.out.println( c.s + "_" + m.s + ":");
                    for(int i=0; i<ir.size();i++) {
                        System.out.println("\t" + i + ": " + ir.get(i));
                    }
                }
            }
        }
    }

}
