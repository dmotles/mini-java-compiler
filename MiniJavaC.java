import java_cup.runtime.Symbol;
import java.io.*;
import java.util.*;
import visitor.*;
import visitor.symbol.*;
import syntaxtree.Program;

public class MiniJavaC {
	public static void main(String[] args) {
		if(args.length != 1) {
			System.err.println("ERROR: Invalid number of command line arguments.");
			System.err.println("Usage: java MiniJavaC file.mini");
			System.exit(1);
		}
		Symbol parse_tree = null;

		SymbolTable table = new SymbolTable();
		
		NameVisitor nameVisitor = new NameVisitor(table);
		TypeDepthFirstVisitor typeVisitor = new TypeDepthFirstVisitor(table);
		IRVisitor irVisitor = new IRVisitor(table);

		try {
			MiniJavaParser parser_obj = new MiniJavaParser(new MiniJavaLexer(new FileInputStream(args[0])));
			parse_tree = parser_obj.parse();
			Program p = (Program)parse_tree.value;
			System.out.println("****** Name Checking ************");
			nameVisitor.visit(p);
			System.out.println("****** Type Checking ************");
			typeVisitor.visit(p);
			
			if(nameVisitor.hadError() || typeVisitor.hadError()) {
				System.out.println("Errors detected. Compilation aborted.");
				System.exit(1);
			}
			System.out.println("****** IR Generation ************");
			irVisitor.visit(p);
			
			table.dump();
			irVisitor.dumpIR();
		} catch (IOException e) {
			System.err.println("ERROR: Unable to open file: " + args[0]);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}
