import java_cup.runtime.Symbol;
import java.io.*;
import syntaxtree.*;
import visitor.*;
import semantic.*;
import java.util.ArrayList;

public class MiniJavaSymbolTableGenerator {
    private static boolean DEBUG_PARSE;
    private static final PrettyPrintVisitor PRETTY_PRINTER = new PrettyPrintVisitor();
    private static final BuildSymbolTableVisitor SYMBOL_TABLE_BUILDER = new BuildSymbolTableVisitor();

    public static void main( String [] args ) {
        MiniJavaLexer lex;
        MiniJavaParser parse;
        ArrayList<File> files = new ArrayList<File>();
        DEBUG_PARSE = false;

        for( int i = 0; i < args.length; i++ ) {
            if( args[i].equals( "-d" ) ) DEBUG_PARSE = true;
            else if( args[i].equals( "-h" ) || args[i].equals( "--help" ) || args[i].equals( "-help" ) ) {
                print_usage(); System.exit(1);
            } else {
                File nf = new File( args[i] );
                if( files.contains( nf ) ) {
                    System.err.println("Fatal Error: you specified " + args[i] + " more than once on the command-line." );
                    System.exit(2);
                }
                if( nf.exists() ) {
                    if( nf.isFile() ) {
                        if( nf.canRead() )
                            files.add( new File(args[i]) );
                        else {
                            System.err.println( "Fatal Error: Unable to open " + args[i] + " for reading! Check permissions? " );
                            System.exit( 5 );
                        }
                    } else {
                        System.err.println( "Fatal Error: " + args[i] + "does not appear to be a file." );
                        System.exit(4);
                    }
                } else {
                    System.err.println( "Fatal Error: path " + args[i] + " does not appear to exist. ENOENT!" );
                    System.exit( 3 );
                }
            }
        }

        if( files.size() > 0 ) {
            for( File f : files ) {
                try {
                    if( symbolTable( new FileInputStream(f) ) ) {
                        if( DEBUG_PARSE ) System.err.println( "***  " + f.getPath() + "Parsed Successfully! ***" );
                    } else {
                        System.err.println( "Fatal Error: " + f.getPath() + " failed to compile! Aborting!" );
                        System.exit(6);
                    }
                } catch( FileNotFoundException fnfe ) {
                    System.err.println( "Fatal Error: path " + f.getPath() + " does not appear to exist. ENOENT!" );
                    System.exit( 3 );
                } catch( SecurityException se ) {
                    System.err.println( "Fatal Error: Unable to open " + f.getPath() + " for reading! Check permissions? " );
                    System.exit( 5 );
                }
            }
        } else {
            if( symbolTable( System.in ) ) {
                if( DEBUG_PARSE ) System.err.println( "***  stdin Parsed Successfully! ***" );
            } else {
                System.err.println( "Fatal Error: stdin was unable to be parsed successfully." );
                System.exit(6);
            }
        }
    }

    private static void print_usage() {
        String usage = "MiniJavaSymbolTableGenerator (c) 2012 Daniel Motles under the Apache 2.0 license\n";
        usage += "Usage: java -classpath \".:/path/to/java-cup-11a.jar\" MiniJavaSymbolTableGenerator [-dh] filepath\n";
        usage += "\n  -d\n      Developer mode. Prints parser debug output.";
        usage += "\n  -h\n-help\n--help\n      Prints this usage text.";
        usage += "Portions of program generated by JavaCUP http://www.cs.princeton.edu/~appel/modern/java/CUP/ covered under a GPL-compatible license\n";
        usage += "Portions of prorgram generated by JFLex http://jflex.de/ covered under the GPL\n";
        System.err.print( usage );
    }

    private static boolean symbolTable( InputStream is ) {
        boolean ret = true;
        Symbol parserGoalSymbol;
        Program p;
        try {
            MiniJavaLexer lexer = new MiniJavaLexer( is );
            MiniJavaParser parser = new MiniJavaParser( lexer );
            parserGoalSymbol = (DEBUG_PARSE) ? parser.debug_parse() : parser.parse();
            p = (Program)parserGoalSymbol.value;
            p.accept( SYMBOL_TABLE_BUILDER );
            SymbolTable lastSymTable = SYMBOL_TABLE_BUILDER.getLastSymbolTable();
            System.out.println( lastSymTable.toString() );

        } catch (IOException e) {
            System.err.println( "Fatal Error: Unable to read file stream!" );
            ret = false;
        } catch ( NameAnalysisException nae ) {
            ret = false;
            System.err.println("Unable to continue. " + nae.getMessage() );
        } catch (Exception e) {
            ret = false;
            System.err.println( "Fatal Error: unknown error!" );
            if(DEBUG_PARSE) {
                String emsg = e.getMessage();
                Throwable ecause = e.getCause();
                StackTraceElement [] stack = e.getStackTrace();
                String msg = "Exception Message: " + ( emsg == null ? "None." : emsg + "\n" );
                msg += "Exception Caused By: " + ( ecause == null ? "No Cause." : ecause.toString() ) + "\n";
                if( ecause != null ) {
                    String causemsg = ecause.getMessage();
                    msg += "Cause message: " + ( causemsg == null ? "None." : causemsg ) + "\n";
                }
                msg += "STACK DUMP:\n";
                msg += "===========\n";
                for( int i = 0; i < stack.length; i++ ) {
                    StackTraceElement frame = stack[i];
                    String classname = frame.getClassName();
                    String filename = frame.getFileName();
                    int linenum = frame.getLineNumber();
                    String method = frame.getMethodName();
                    boolean isnative = frame.isNativeMethod();
                    msg += "  #" + i;
                    msg += " " + classname + ".";
                    msg += method + "()";
                    if( filename != null ) msg += " in " + filename;
                    if( linenum > 0 ) msg += ":" + linenum;
                    if( isnative ) msg += " JAVA NATIVE METHOD";
                    msg += "\n";
                }
                System.err.println( msg );
            }
        }
        try { is.close(); }
        catch( IOException ioe) {
            if( DEBUG_PARSE ) {
                System.err.println( "Attempted to close stream, failed:" + ioe.getMessage() );
            }
        }
        return ret;
    }
}
