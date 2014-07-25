import java.io.*;
import scanner.*;
import parser.*;
import ast.Ast;
import semantic.Semantic;
import irt.Irt;
import codegen.Codegen;
import opt.*;
import lib.*;


public class Compiler {
	
	public static void main(String args[]) throws Exception {
		
		String input = "";

		if ((args.length > 0) && (args[args.length - 1].indexOf('-') != 0)) {
			input = args[args.length - 1]; // Si existen parametros toma el ultimo como el nombre del archivo de entrada. SIEMPRE.
			if (input.endsWith(".dcf")) {
				System.out.println("Archivo a compilar: " + input);
				checkParam(args, input);
			} else System.out.println("El archivo a compilar debe ser de extension .dcf");
			} else {
				System.out.println("Debe especificar el archivo .dcf a compilar");
				printHelp();
		}
	}

	static void checkParam(String params[], String input) throws Exception{
		String temp, outname = "", target = "", opt = "", debug = "";
		boolean h = false, o = false, targ = false, op = false, debu = false;
		
		PrintWriter archivo = null;
		for(int contador = 0; contador < (params.length - 1); contador++) {
			temp = params[contador].toLowerCase();
			if (temp.equals("-h") && (h == false)) {
				System.out.println("GOT IN.");
				printHelp();
				h = true;
			} else if (temp.equals("-o")){
				o = true;
				if ((contador + 1) < params.length) {
					contador++;
					temp = params[contador];
					if (temp.indexOf('-') != 0) {
						outname = temp + ".cpl";
					}
				}
			} else if (temp.equals("-target")) {
				targ = true;
				if ((contador + 1) < params.length) {
					contador++;
					temp = params[contador];
					if (temp.indexOf('-') != 0) {
						target = temp;
					}
				}
			} else if (temp.equals("-opt")) {
				op = true;
				if ((contador + 1) < params.length) {
					contador++;
					temp = params[contador];
					if (temp.indexOf('-') != 0) {
						opt = temp;
					}
				}
			} else if ((temp.equals("-debug")) && ((contador + 1) < params.length)) {
				contador++;
				temp = params[contador];
				if (temp.indexOf('-') != 0) {
					debug = temp;
					debu = true;
				} else contador--;
			} else {
				System.out.println("Ingreso un parametro no reconocido");
				printHelp();
			}
		}

		if (o) {
			try{
				archivo = new PrintWriter(new BufferedWriter(new FileWriter(outname)));
				archivo.append("Archivo de salida del compilador");
				System.out.println("Archivo de salida " + outname + " creado");
				archivo.close();
			} catch (FileNotFoundException e) {
				System.out.println("No se especifico el archivo de salida");
			}
		}

		if (targ) {
						
			int stage = resolveStage(target);
		
			if (stage == -1) { System.out.println("No se ha especificado ninguna fase como target. No se ejecuta nada."); printHelp(); }
			
			if (stage >= 0) Scanner.scan();
			if (stage >= 1) CC4Parser.parse();
			if (stage >= 2) Ast.ast();
			if (stage >= 3) Semantic.semantic();
			if (stage >= 4) Irt.irt();
			if (stage == 5) Codegen.codegen();
			
		}

		if (op){
			if (opt.equals("constant")) {
				ConstantFolding.constantfolding();
			} else if (opt.equals("algebraic")){
				Algebraic.algebraic();
			} else System.out.println("Debe especificar el tipo de optimizacion");
		}
		
		System.out.println("\nFin de ejecucion.");
	}
	
	static int resolveStage(String target){
		if (target.equals("scan")) return 0;
		else if (target.equals("parse")) return 1;
		else if (target.equals("ast")) return 2;
		else if (target.equals("semantic")) return 3;
		else if (target.equals("irt")) return 4;
		else if (target.equals("codegen")) return 5;
		else return -1;
	}
	
	static void printHelp(){
		System.out.println( //"help");/*
			"\n-o <outname>	Escribir el output a <outname>.\n"

			+ "\n-target <stage>	Donde <stage> es uno de: scan, parse, ast, semantic, irt,\n"
			+ "	codegen; la compilacion debe proceder hasta la etapa indicada.\n"
					
			+ "\n	Por ejemplo, si <stage> es scan, una instancia de scan debe ser creada\n"
			+ "	imprimiendo en el archivo de salida \"stage: scanning\".\n"
					
			+ "\n	Si es parse una instancia de parser debe ser creada a partir de la\n"
			+ "	instancia de scanner imprimiendo \"stage: parsing\", ademas del mensaje\n"
			+ "	de scanner y asi sucesivamente.\n"

			+ "\n-opt	<opt_stage> es uno de: constant, algebraic; la compilacion debe hacer\n"
			+ "	solo la optimizacion que se le pida, y debe imprimir como en -target\n"
			+ "	\"optimizing: constant folding\" o \"optimizing: algebraic simplification\". \n"

			+ "\n-debug <stage>	Imprimir informacion de debugging. Debe haber un mensaje por\n"
			+ "	cada etapa listada en <stage> de la forma \"Debugging <stage>\".\n"
			+ "	<stage> tiene las mismas opciones de -target, con la diferencia que se\n"
			+ "	<stage> tiene las mismas opciones de -target, con la diferencia que se\n"
			+ "	pueden \"debuggear\" varias etapas, separandolas con ':' de la forma\n"
			+ "	scan:parse:etc.\n"
			
			+ "\n<filename>	Nombre de archivo de entrada, siempre es el ultimo.\n"
			
			+ "\n-h	Muestra esta ayuda al usuario.\n");
			
	}
}

