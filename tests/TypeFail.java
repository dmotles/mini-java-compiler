class TypeFail {
    public static void main( String [] aerg ){
        System.out.println( new Derp().doStuff() );
    }
}


class Derp {

    public boolean doStuff() {
        int pow;
        pow = this.pow( 2, 5 );

        if( pow < new Derp() ) {
            System.out.println( 99 );
        } else {
            System.out.println( this.doStuff( 10 ) );
        }

        while( 1 && true ) {
            pow = this.pow( pow, pow );
        }
        return  1 < 2;
    }

    public int pow( int base, int pow ) {
        int result;
        result = 0;
        if( pow < 1 )
            result = 1;
        else
            result = base * this.pow( base, pow + this.negativeOne() );
        return result;
    }

    public int negativeOne(){
        return 2147483647 + 2147483647;
    }

}
