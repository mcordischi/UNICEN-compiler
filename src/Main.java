
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Vector;

import parser.Parser;

import event.UserInterface;
import lexer.*;
import reversePolish.ReversePolish;
import symbolTable.SymbolTable;
import codeGenerator.*;


public class Main {

	
	private static String readFile(String path) throws IOException {
		  FileInputStream stream = new FileInputStream(new File(path));
		  try {
		    FileChannel fc = stream.getChannel();
		    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		    /* Instead of using default, pass in a decoder. */
		    return Charset.defaultCharset().decode(bb).toString();
		  }
		  finally {
		    stream.close();
		  }
		}
	
	
	static UserInterface ui;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
	
		//String path = "/home/marto/working/compiler/jar/punterosFunciones.l";
		String path = args[0];
		
		SymbolTable st = new SymbolTable();
		ui = new UserInterface();
		
		Lexer lex = new Lexer(st,ui);
		lex.loadFile(readFile(path)); 
		
		Parser par = new Parser();
		
		ReversePolish rp = new ReversePolish(st,ui);
		
		//Run the Parser and Lexer
		par.load(lex,st,rp,ui);
		par.run();
		
//		System.out.println(rp.toString());

		Vector<String> program =  new Vector<String>();
		
		MemoryManager mm = new MemoryManager(program,ui);
		CodeGenerator cg = new CodeGenerator(rp,mm,ui,program);
		
		Vector<String> result;
		if(!(ui.error)){
			//Translate to Assembler
			cg.run();
			result = makeProgram(mm.getDeclarations(),program);
			saveFile(result);
		}else {
			result = new Vector<String>();
			result.add("Ha ocurrido un error en la ejecución, generación de código cancelada");
		}		

		//Print ReversePolish and SymbolTable
		System.out.println(rp.toString());
		System.out.println(st.toString());
		
		for(String str: result)
			System.out.println(str);
	 
	}
	
	
	
	
	static private Vector<String> makeProgram(Vector<String> declarations , Vector<String> program){
		Vector<String> result = new Vector<String>();
		result.add(".386");
		result.add(".model flat, stdcall");
		result.add("option casemap :none");
		result.add("include \\masm32\\include\\windows.inc");
		result.add("include \\masm32\\include\\kernel32.inc");
		result.add("include \\masm32\\include\\user32.inc");
		result.add("includelib \\masm32\\lib\\kernel32.lib");
		result.add("includelib \\masm32\\lib\\user32.lib");
		result.add(".data");
		result.addAll(declarations);
		result.add(".code");
		result.add("start:");
		result.add("JMP START");
		result.add("DIVZERO:");
		result.add("invoke MessageBox, NULL, addr \"Error\", addr \"Division por Cero\", MB_OK");
		result.add("JMP END");
		result.add("START:");
		result.addAll(program);
		result.add("END:");
		result.add("end start");
		return result;
	}
	
	
	
	
	static private void saveFile(Vector<String> output){
		try {
			FileWriter file = new FileWriter("out.asm");
			BufferedWriter out = new BufferedWriter(file);
			for (String str: output)
				out.write(str + "\n");
			out.close();
			file.close();
		} catch (IOException e) {
			ui.eventError("Error de escritura", 0, "Output");
			e.printStackTrace();
		}
	}

}
