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
			File f =  new File(input);
			if (f.exists()) {
				if (input.endsWith(".dcf")) {
					System.out.println("Archivo a compilar: " + input);
					checkParam(args, input.substring(0, input.length() - 4));
				} else System.out.println("El archivo a compilar debe ser de extension .dcf");
			} else System.out.println("no existe el archivo especificado");
		} else {
			System.out.println("Debe especificar el archivo .dcf a compilar");
			printHelp();
		}
	}

	static void checkParam(String params[], String input) throws Exception{
		String temp, outname = input + ".cpl", target = "", opt = "", debug = "";
		boolean h = false, o = false, targ = false, op = false, debu = false, namef = false;
		

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
						namef = true;
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
				System.out.println("Ingreso un parametro no reconocido o incorrecto");
				printHelp();
				return;
			}
		}

		archivo = new PrintWriter(new BufferedWriter(new FileWriter(outname)));
		archivo.append("Archivo de salida del compilador\n");

		if (o) {
			if (namef){
				System.out.println("Archivo de salida " + outname + " creado");
			} else 	{
				archivo.close();
				System.out.println("No se especifico el archivo de salida");
				return;
			}
			
		} else System.out.println("Archivo de salida " + outname + " creado");

		if (targ) {
						
			int stage = resolveStage(target);
		
			if (stage == -1) { 
				archivo.close();
				System.out.println("No se ha especificado ninguna fase como target. No se ejecuta nada."); 
				printHelp(); 
				return;
			}
			
			if (stage >= 0) {
				archivo.append(Scanner.scan());
				System.out.println("Scanner");
			}
			if (stage >= 1) {
				archivo.append(CC4Parser.parse());
				System.out.println("Parser");
			}
			if (stage >= 2) {
				archivo.append(Ast.ast());
				System.out.println("AST");
			}
			if (stage >= 3) {
				archivo.append(Semantic.semantic());
				System.out.println("Semantic");
			}
			if (stage >= 4) {
				archivo.append(Irt.irt());
				System.out.println("IRT");
			}
			if (stage == 5) {
				archivo.append(Codegen.codegen());
				System.out.println("Codegen");
			}
			
		}

		if (op){
			if (opt.equals("constant")) {
				System.out.println("Constant Folding Optimization");
				archivo.append(ConstantFolding.constantfolding());
			} else if (opt.equals("algebraic")){
				System.out.println("Algebraic Optimization");
				archivo.append(Algebraic.algebraic());
			} else {
				archivo.close();
				System.out.println("Debe especificar el tipo de optimizacion");
				printHelp();
				return;
			}
		}
		
		if (debu) {
			String[] debugPhases = debug.split(":");
			for(int i = 0; i < debugPhases.length; i++) { // Evaluar que fases han de debugguearse.
				temp = debugPhases[i];
				
				if (temp.equals("scan")) {
					archivo.append(Debug.debug("Scanner"));
				} else if (temp.equals("parse")) {
					archivo.append(Debug.debug("Parser"));
				} else if (temp.equals("ast")) {
					archivo.append(Debug.debug("Ast"));
				} else if (temp.equals("semantic")) {
					archivo.append(Debug.debug("Semantic"));
				} else if (temp.equals("irt")) {
					archivo.append(Debug.debug("IRT"));
				} else if (temp.equals("codegen")) {
					archivo.append(Debug.debug("Codegen"));
				}
			}
		}

		archivo.close();
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
			+ "	pueden \"debuggear\" varias etapas, separandolas con ':' de la forma\n"
			+ "	scan:parse:etc.\n"
			
			+ "\n<filename>	Nombre de archivo de entrada, siempre es el ultimo.\n"
			
			+ "\n-h	Muestra esta ayuda al usuario.\n");
			
	}
}

