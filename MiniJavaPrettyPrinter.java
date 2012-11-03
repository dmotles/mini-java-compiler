import java_cup.runtime.Symbol;
import java.io.*;
import syntaxtree.*;
import visitor.*;

public class MiniJavaPrettyPrinter {
    public static final int PARAMETER_ERROR = 10;
    public static void main( String [] args ) {
        int exitcode = 0;
        if( args.length > 0 ) {
            for( int i = 0; i < args.length; i++ ) {
                Symbol resultSymbol = null;
                String filename = args[i];
                try {
                    MiniJavaLexer ml = new MiniJavaLexer( new FileInputStream( filename ) );
                    MiniJavaParser mp = new MiniJavaParser( ml );
                    resultSymbol = mp.parse();

                    Program p = (Program)resultSymbol.value;
                    PrettyPrintVisitor ppv = new PrettyPrintVisitor();
                    p.accept( ppv );

                } catch (IOException e) {
                    System.err.println("ERROR: " + e.getMessage() );
                    exitcode = PARAMETER_ERROR;
                } catch (Exception e) {
                    System.err.println( "ERROR IN: " + filename );
                    exitcode = 255;
                }
            }
        } else {
            System.err.println( "Usage: java MiniJavaPrettyPrinter [input file]" );
            exitcode = 1;
        }
        if( exitcode != 0 ) {
            System.exit( exitcode );
        }
    }
}
