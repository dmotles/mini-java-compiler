class DuplicateDeclaration {
    public static void main( String [] args ) {
        System.out.println( new OtherClass().go() );
    }
}

class OtherClass {
    public int go() {
        int a;
        int c;
        int a;

        a = 2;
        return a;
    }

    public OtherClass go() {
        return this;
    }
}


class NotOther {
    // a dup method, but in another class
    public int go( boolean tf ){
        System.out.println( tf );
        return 10;
    }
}


class OtherClass {
    public int [] newArray( int size ) {
        return new int[size];
    }
}
