make: Compiler.class scanner/Scanner.class parser/CC4Parser.class ast/Ast.class semantic/Semantic.class irt/Irt.class codegen/Codegen.class opt/Algebraic.class opt/ConstantFolding.class lib/Debug.class lib/ErrorHandler.class
	
Compiler.class : Compiler.java 
	javac Compiler.java 

scanner/Scanner.class : scanner/Scanner.java 
	javac scanner/Scanner.java 

parser/CC4Parser.class : parser/CC4Parser.java 
	javac parser/CC4Parser.java 

ast/Ast.class : ast/Ast.java 
	javac ast/Ast.java 

semantic/Semantic.class : semantic/Semantic.java 
	javac semantic/Semantic.java 

irt/Irt.class : irt/Irt.java 
	javac irt/Irt.java 

codegen/Codegen.class : codegen/Codegen.java 
	javac codegen/Codegen.java 

opt/Algebraic.class : opt/Algebraic.java 
	javac opt/Algebraic.java 

opt/ConstantFolding.class : opt/ConstantFolding.java 
	javac opt/ConstantFolding.java 

lib/Debug.class : lib/Debug.java 
	javac lib/Debug.java 

lib/ErrorHandler.class: lib/ErrorHandler.java
	javac lib/ErrorHandler.java

clean:
	rm Compiler.class 
	rm scanner/Scanner.class 
	rm parser/CC4Parser.class 
	rm ast/Ast.class 
	rm semantic/Semantic.class 
	rm irt/Irt.class 
	rm codegen/Codegen.class 
	rm opt/Algebraic.class 
	rm opt/ConstantFolding.class 
	rm lib/Debug.class 
	rm lib/ErrorHandler.class
