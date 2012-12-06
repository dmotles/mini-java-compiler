package visitor.symbol;

import syntaxtree.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

public class SymbolTable {
    private static final boolean DEBUG = false;

    Binding root = new Binding();
    Identifier rootName = new Identifier("Root");

    Binding current = root;
    Identifier currentName = rootName;

    private class Binding {
        private HashMap<Identifier, Symbol> table;
        private HashMap<Identifier, Binding> children;
        private Binding parent;
        private Identifier parentName;

        public Binding() {
            table = new HashMap<Identifier, Symbol>();
            children = new HashMap<Identifier, Binding>();
        }

        public boolean isDefined(Identifier i) {
            return (table.get(i) != null);
        }

        public Type getType(Identifier i) {
            return table.get(i).getType();
        }

        public void addSymbol(Symbol s) {
            table.put(s.getName(), s);
        }

        Symbol getSymbol(Identifier i) {
            return table.get(i);
        }

        public boolean isClassInScope(Identifier i) {
            return (table.get(i) instanceof ClassSymbol);
        }

        public boolean isMethodInScope(Identifier i) {
            return (table.get(i) instanceof MethodSymbol);
        }

        public boolean isVariableInScope(Identifier i) {
            return (table.get(i) instanceof VariableSymbol);
        }

        public void addChild(Identifier i, Binding b) {
            children.put(i, b);
            b.parentName = currentName;
            b.parent = this;
        }

        public Binding getChild(Identifier i) {
            return children.get(i);
        }

        public Binding getParent() {
            return parent;
        }

        public Identifier getParentName() {
            return parentName;
        }

        public void dump() {
            for (Iterator i=table.keySet().iterator(); i.hasNext() ; ) {
                Identifier s = (Identifier)i.next();
                System.out.println(table.get(s));
            }
            for(Iterator i=children.keySet().iterator(); i.hasNext(); ) {
                Identifier s = (Identifier)i.next();
                System.out.println("Scope: " + s + " parent: " + parentName);
                children.get(s).dump();
            }
        }
    }

    public void addSymbol(Symbol s) {
        current.addSymbol(s);
    }

    public boolean isDefined(Identifier i) {
        Binding c = current;
        while(c != null) {
            if(c.isDefined(i))
                return true;
            c = c.getParent();
        }
        return false;
    }

    public Type getType(Identifier i) {
        Binding c = current;
        while(c != null) {
            if(c.isDefined(i))
                return c.getType(i);
            c = c.getParent();
        }
        return null;
    }

    public Symbol getSymbol(Identifier i) {
        Binding c = current;
        while(c != null) {
            if(c.isDefined(i))
                return c.getSymbol(i);
            c = c.getParent();
        }
        return null;
    }

    public ClassSymbol getClassByName(Identifier i) {

        Symbol s = root.getSymbol(i);

        if(DEBUG) {
            System.out.println("Get class by name " + i + " " + s);
        }

        if (s instanceof ClassSymbol) {
            return (ClassSymbol)s;
        }
        return null;
    }

    public MethodSymbol getMethodByName(Identifier klass, Identifier m) {
        Symbol s = root.getSymbol(klass);

        if(DEBUG) {
            System.out.println("Get method by name " + klass + " " + m);
        }

        if (s instanceof ClassSymbol) {
            if(DEBUG) {
                System.out.println("Found class " + klass);
            }
            Binding b = root.getChild(klass);

            Symbol method = b.getSymbol(m);

            if (method instanceof MethodSymbol) {
                return (MethodSymbol)method;
            }
        }
        return null;
    }

    public Identifier getScopeName() {
        return currentName;
    }

    /**

      @returns	<code>true</code> if we entered an existing scope<br/>
      <code>false</code> if a new scope was created
      */
    public boolean enterScope(Identifier name) {
        Binding temp = current.getChild(name);
        if(temp == null) {
            if(DEBUG) {
                System.out.println("In scope " + currentName + " and entering new scope " + name);
            }
            Binding newBinding = new Binding();
            current.addChild(name, newBinding);
            current = newBinding;
            currentName = name;
            return false;
        }
        else {
            if(DEBUG) {
                System.out.println("Entering existing scope " + name);
            }
            current = temp;
            currentName = name;
            return true;
        }
    }


    public void leaveScope() {
        if(DEBUG) {
            System.out.print("Leaving scope " + currentName);
        }
        currentName = current.getParentName();
        current = current.getParent();
        if(DEBUG) {
            System.out.println(" and now in scope " + currentName);
        }
    }

    public void dump() {
        if(root!=null) {
            System.out.println("Scope: " + rootName);
            root.dump();
        }
    }
}
