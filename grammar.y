%{
package parser;

import java.util.Hashtable;

import event.*;
import lexer.*;
import symbolTable.*;
import reversePolish.*;
%}

%token UINTEGER
%token STRING
%token FUN
%token ID
%token CONSTANT
%token RETURN
%token IF
%token THEN
%token ELSE
%token DO
%token UNTIL
%token COMPARATOR
%token UI
%token ENDOFFILE
%token ASSIGN
%token PRINT

%%

input : program ENDOFFILE
;

program : sentence
	| program sentence
;

sentence : open_sentence
	| closed_sentence
	| error ';' {event.eventError("Error en sentencia",lex.getLineNumber(),"parser");}
;

open_sentence : if_sentence sentence   { event.eventNewRule("Sentencia IF",lex.getLineNumber());
					 rp.endIf(); }
	| if_sentence closed_sentence else_sentence open_sentence  { event.eventNewRule("Sentencia IF",lex.getLineNumber()); 
							rp.endElse(); }
	| do_sentence closed_sentence until_sentence  { event.eventNewRule("Sentencia DO",lex.getLineNumber()); }
	| IF condition closed_sentence ELSE open_sentence {event.eventError("Sentencia IF incompleta, falta THEN?",lex.getLineNumber(),"parser");}
	| IF condition sentence {event.eventError("Sentencia IF incompleta, falta THEN?",lex.getLineNumber(),"parser");}
;

	
closed_sentence : if_sentence closed_sentence else_sentence closed_sentence  { event.eventNewRule("Sentencia IF",lex.getLineNumber()); 
								rp.endElse(); }
	| IF condition closed_sentence ELSE closed_sentence {event.eventError("Sentencia IF incompleta, falta THEN?",lex.getLineNumber(),"parser");}
	| declaration
	| execute
	| '{' program '}'  { event.eventNewRule("Nuevo Bloque",lex.getLineNumber()); }
	| '{' error {event.eventError("Error delimitadores de bloques",lex.getLineNumber(),"parser");}
;

if_sentence : IF condition THEN { rp.startIf();}
;

else_sentence : ELSE { rp.startElse();}
;

do_sentence : DO {rp.startDo();}
;

until_sentence : UNTIL condition ';' {rp.endDo();}
;

/*declarations*/

declaration : type declaration_names ';' {	
					event.eventNewRule("Declaración",lex.getLineNumber()); }
;

declaration_names : ID 
			{ 	IdentifierEntry entry = ((IdentifierEntry)$1.obj);
				if (!entry.check()){
					entry.setValueType(type);
 					entry.declare();
				} else event.eventError("Variable " + entry.getLexeme() + " ya declarada" , lex.getLineNumber(), 										"Generador de código intermedio" ) ;
			}
	| declaration_names ',' ID
			 { 	((IdentifierEntry)$3.obj).setValueType(type);
				((IdentifierEntry)$3.obj).declare();}
;

type : UINTEGER {type = TableEntry.CONST_UINT ; }
	| '^' FUN { type = TableEntry.FUNCTION_POINTER;}
;

/*execution*/

execute : function  { event.eventNewRule("Funcion",lex.getLineNumber()); }
	| assignment  { event.eventNewRule("Asignación",lex.getLineNumber()); }
	| PRINT STRING ';' { 
			rp.addItem((TableEntry)$2.obj);
			rp.addItem((TableEntry)$1.obj);
			event.eventNewRule("Print",lex.getLineNumber()); }
	| PRINT error ';' {event.eventError("Error en sentencia Print",lex.getLineNumber(),"parser");}
;

function : function_header '{' program function_end
	| FUN ID '{' program RETURN '(' expression ')' '}' {event.eventError("Funcion no tiene tipo de retorno",lex.getLineNumber(),"parser");}
	| function_header '{' program '}' {event.eventError("Funcion sin retorno",lex.getLineNumber(),"parser");}
	| type FUN '{' program RETURN '(' expression ')' '}' {event.eventError("Funcion sin nombre",lex.getLineNumber(),"parser");}	
	| function_header error ';' {event.eventError("Funcion no delimitada",lex.getLineNumber(),"parser");}
;

function_header: type FUN ID
				{	
					IdentifierEntry entry = (IdentifierEntry)$3.obj ;
					entry.setValueType(TableEntry.FUNCTION_NAME);
					entry.declare();
					rp.startFunction(entry);
					
				}
;

function_end: function_return '(' expression ')' '}'  {rp.endFunction();}
;

function_return: RETURN { rp.addItem(ReversePolish.rtn);}
;

/*assignment*/

assignment : result ASSIGN expression ';'  {	if(((TableEntry)$1.obj).getValueType()==TableEntry.FUNCTION_NAME)
							event.eventError("Asignación de función",lex.getLineNumber(),"parser");
						rp.addItem((TableEntry)$2.obj);	 
						rp.endPtrAssigment(); //Patch
 					}
	| ID '=' expression ';' {event.eventError("Asignación mal enunciada",lex.getLineNumber(),"parser");}
;


result : ID {   if(((TableEntry)$1.obj).getValueType()==TableEntry.FUNCTION_POINTER){rp.setPtrAssigment();} //Patch
		rp.addItem((TableEntry)$1.obj);}
;

expression : expression '+' term {rp.addItem((TableEntry)$2.obj);
				   }
	| expression '-' term {rp.addItem((TableEntry)$2.obj);}
	| term
;

term : term '*' factor {rp.addItem((TableEntry)$2.obj);}
	| term '/' factor {rp.addItem((TableEntry)$2.obj); }
	| factor 
;

factor : ID  {rp.addItem((TableEntry)(clone($1)).obj);}
	| CONSTANT UI  {rp.addItem((TableEntry)(clone($1)).obj);}
	| CONSTANT error {event.eventError("Mala enunciación de constante",lex.getLineNumber(),"parser");}
	| '(' expression ')'
	| '^' ID  {rp.addItem((TableEntry)$1.obj);}
	| '(' ')' {event.eventError("Paréntesis vacíos",lex.getLineNumber(),"parser");}
	| '(' error {event.eventError("Paréntesis impares",lex.getLineNumber(),"parser");}
	| '^' error {event.eventError("Error en asignación",lex.getLineNumber(),"parser");}
;

/*control*/

condition : '(' expression COMPARATOR expression ')' {rp.addItem((TableEntry)$3.obj);}
	| '(' expression COMPARATOR ')' {event.eventError("Error en condicción",lex.getLineNumber(),"parser");}
	| '(' expression ')' {event.eventError("Error en condicción",lex.getLineNumber(),"parser");}
	| '(' COMPARATOR ')' {event.eventError("Error en condicción",lex.getLineNumber(),"parser");}
	| '(' COMPARATOR expression  ')' {event.eventError("Error en condicción",lex.getLineNumber(),"parser");}
;

%%


Lexer lex ;
Event event ;
ReversePolish rp ;
SymbolTable st ;
String type; //Used in declarations


private ParserVal clone(ParserVal other){
	if (other == null){ return null;}
	ParserVal newParserVal = new ParserVal();
	newParserVal.obj = other.obj;
	newParserVal.ival = other.ival;
	// System.out.println("NOT NULL" + newParserVal.obj.toString() ); 
	return newParserVal;
}


public void loadLexer(Lexer lex){
	this.lex = lex ;
}

public void loadRP(ReversePolish rp){
	this.rp = rp ;
}

public void loadSymbolTable(SymbolTable st){
	this.st= st ;
}

public void load(Lexer lex,SymbolTable st,ReversePolish rp, Event event){
	this.st= st ;
	this.rp = rp ;
	this.lex = lex ;
	this.event = event ;
}

void yyerror(String s){
     System.out.println(s);
}

int yylex(){
	if (lex.endOfFile()) return 0 ;
	TableEntry tok = lex.getToken();

	if(tok== null)
		return (Short) Conversor.get("ENDOFFILE");

	yylval = new ParserVal(tok);

	Short s = (Short) Conversor.get(tok.getType());
	event.eventNewToken(tok);
	return s.intValue();
} 

static Hashtable<String, Short> Conversor;
static {
	Conversor = new Hashtable<String, Short>();
	
	Conversor.put(TableEntry.CONST_STRING, STRING);
	Conversor.put(TableEntry.CONST_UINT, CONSTANT);
	Conversor.put(TableEntry.IDENTIFIER, ID);
	Conversor.put("if", IF);
	Conversor.put("then",THEN);
	Conversor.put( "else", ELSE);
	Conversor.put( "do", DO);
	Conversor.put( "until", UNTIL);
	Conversor.put( "uint", UINTEGER);
	Conversor.put( "ui" , UI);
	Conversor.put( "fun", FUN);
	Conversor.put( "return", RETURN);
	Conversor.put( "do", DO);
	Conversor.put( "print", PRINT);
	Conversor.put( "<=", COMPARATOR);
	Conversor.put( "==", COMPARATOR);
	Conversor.put( ">=", COMPARATOR);
	Conversor.put( "!=", COMPARATOR);
	Conversor.put( "<", COMPARATOR);
	Conversor.put( ">", COMPARATOR);
	Conversor.put( "<>", COMPARATOR);
	Conversor.put( ":=", ASSIGN) ;
	Conversor.put( "ENDOFFILE",ENDOFFILE);
	Conversor.put( ";", new Short((short) ';'));
	Conversor.put( ",", new Short((short) ','));
	Conversor.put( "=", new Short((short) '='));
	Conversor.put( "{", new Short((short)'{'));
	Conversor.put( "}", new Short((short)'}'));
	Conversor.put( "(", new Short((short)'('));
	Conversor.put( ")", new Short((short)')'));
	Conversor.put( "+", new Short((short)'+'));
	Conversor.put( "-", new Short((short)'-'));
	Conversor.put( "*", new Short((short)'*'));
	Conversor.put( "/", new Short((short)'/'));	
	Conversor.put( "^", new Short((short)'^'));



}

