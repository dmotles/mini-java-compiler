class HelloWorld {
    public static void main ( String [] args ) {
        System.out.println( new ProgDriver().run() );
    }
}


class ProgDriver {
     int NL; int SP;      int EXCL;      int DQUOTE;     int HASH;      int DOLLAR;      int PCT;      int AMP;
     int LPAR;       int RPAR;      int ASTER;     int PLUS;      int COMMA;      int MINUS;      int PERIOD;
     int ZERO;       int ONE;      int TWO;     int THREE;      int FOUR;      int FIVE;      int SIX;
    int SEVEN;
     int EIGHT;       int NINE;      int COLON;     int SEMI;      int LESSTHAN;      int EQUALS;      int GREATERTHAN;
     int AT;       int A;      int B;     int C;      int D;      int E;      int F;
     int H;       int I;      int J;     int K;      int L;      int M;      int N;
     int P;       int Q;      int R;     int S;      int T;      int U;      int V; int W;
     int X;       int Y;      int Z;     int LBRACKET;      int LEFTSLASH;      int RBRACKET;      int CARROT;
     int TICK;       int a;      int b;     int c;     int d;     int e;     int f;
     int h;     int i;     int j;    int k;     int l;     int m;     int n; int o;
     int p;     int q;     int r;    int s;     int t;     int u;     int v;
     int x;     int y;     int z;    int LBRACE;     int VERTBAR;     int RBRACE;     int TILDE;

    public Object run() {
        int [] inter;
        StringBuilder helloworld;
     NL = 10; SP = 32;      EXCL = 33;      DQUOTE = 34;     HASH = 35;      DOLLAR = 36;      PCT = 37;      AMP = 38;
     LPAR = 40;       RPAR = 41;      ASTER = 42;     PLUS = 43;      COMMA = 44;      MINUS = 45;      PERIOD = 46;
     ZERO = 48;       ONE = 49;      TWO = 50;     THREE = 51;      FOUR = 52;      FIVE = 53;      SIX = 54;
    SEVEN = 55;
     EIGHT = 56;       NINE = 57;      COLON = 58;     SEMI = 59;      LESSTHAN = 60;      EQUALS = 61;      GREATERTHAN = 62;
     AT = 64;       A = 65;      B = 66;     C = 67;      D = 68;      E = 69;      F = 70;
     H = 72;       I = 73;      J = 74;     K = 75;      L = 76;      M = 77;      N = 78;
     P = 80;       Q = 81;      R = 82;     S = 83;      T = 84;      U = 85;      V = 86; W = 87;
     X = 88;       Y = 89;      Z = 90;     LBRACKET = 91;      LEFTSLASH = 92;      RBRACKET = 93;      CARROT = 94;
     TICK = 96;       a = 97;      b = 98;     c = 99;     d = 100;     e = 101;     f = 102;
     h = 104;     i = 105;     j = 106;    k = 107;     l = 108;     m = 109;     n = 110; o = 111;
     p = 112;     q = 113;     r = 114;    s = 115;     t = 116;     u = 117;     v = 118;
     x = 120;     y = 121;     z = 122;    LBRACE = 123;     VERTBAR = 124;     RBRACE = 125;     TILDE = 126;
        inter = new int[12];

        inter[0] = H;
        inter[1] = e;
        inter[2] = l;
        inter[3] = l;
        inter[4] = o;
        inter[5] = SP;
        inter[6] = W;
        inter[7] = o;
        inter[8] = r;
        inter[9] = l;
        inter[10] = d;
        inter[11] = EXCL;
        helloworld = this.makeString( inter );
        return helloworld;
    }

    public StringBuilder makeString( int [] ascii ) {
        StringBuilder temp;
        int iter;
        temp = new StringBuilder();
        iter = 0;
        while( iter < ascii.length ) {
            temp = temp.appendCodePoint( ascii[iter] );
            iter = iter + 1;
        }

        return temp;
    }
}
