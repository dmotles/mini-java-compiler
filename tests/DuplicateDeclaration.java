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
}
