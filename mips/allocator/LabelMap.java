package mips.allocator;
import mips.code.*;
import java.util.*;
import visitor.symbol.*;
import syntaxtree.*;
public class LabelMap {
    HashMap<String, Label> map;
    public LabelMap() {
        map = new HashMap<String,Label>();
        map.put( "System.out.println", new Label("_system_out_println") );
        map.put( "System.exit", new Label("_system_exit") );
        map.put( "_New_Object", new Label("_new_object") );
        map.put( "_New_Array", new Label("_new_array") );
    }

    public Label add( String label ) {
        return map.put( label, new Label(label) );
    }

    public Label get( String label ) {
        return map.get( label );
    }
}
