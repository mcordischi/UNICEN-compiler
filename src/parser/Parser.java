//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 2 "grammar.y"
package parser;

import java.util.Hashtable;

import event.*;
import lexer.*;
import symbolTable.*;
import reversePolish.*;
//#line 26 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short UINTEGER=257;
public final static short STRING=258;
public final static short FUN=259;
public final static short ID=260;
public final static short CONSTANT=261;
public final static short RETURN=262;
public final static short IF=263;
public final static short THEN=264;
public final static short ELSE=265;
public final static short DO=266;
public final static short UNTIL=267;
public final static short COMPARATOR=268;
public final static short UI=269;
public final static short ENDOFFILE=270;
public final static short ASSIGN=271;
public final static short PRINT=272;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    2,    2,    2,    3,    3,    3,    3,
    3,    4,    4,    4,    4,    4,    4,    5,    6,    7,
    8,   10,   13,   13,   12,   12,   11,   11,   11,   11,
   14,   14,   14,   14,   14,   16,   17,   19,   15,   15,
   20,   18,   18,   18,   21,   21,   21,   22,   22,   22,
   22,   22,   22,   22,   22,    9,    9,    9,    9,    9,
};
final static short yylen[] = {                            2,
    2,    1,    2,    1,    1,    2,    2,    4,    3,    5,
    3,    4,    5,    1,    1,    3,    2,    3,    1,    1,
    3,    3,    1,    3,    1,    2,    1,    1,    3,    3,
    4,    9,    4,    9,    3,    3,    5,    1,    4,    4,
    1,    3,    3,    1,    3,    3,    1,    1,    2,    2,
    3,    2,    2,    2,    2,    5,    4,    3,    3,    4,
};
final static short yydefred[] = {                         0,
    0,   25,    0,    0,    0,   20,    0,    0,    0,    0,
    0,    2,    4,    5,    0,    0,   14,   15,    0,   27,
   28,    0,    0,    6,    0,    0,    0,    0,    0,    0,
    0,    0,   26,    1,    3,    7,    0,    0,    0,    0,
    0,   23,    0,    0,    0,    0,    0,   48,    0,    0,
    0,    0,    0,   47,    0,    0,   18,   11,    0,   30,
   29,   16,   19,    0,    0,    0,    9,    0,   36,    0,
   22,    0,   35,    0,    0,    0,   50,   49,   55,   52,
   54,   53,    0,   40,    0,    0,    0,    0,   59,    0,
    0,   58,    0,    8,   12,    0,    0,    0,    0,   24,
   38,   33,   31,    0,   39,    0,   51,    0,    0,   45,
   46,   60,   57,    0,   10,   13,    0,   21,    0,    0,
    0,   56,    0,    0,    0,    0,    0,    0,    0,   37,
   32,   34,
};
final static short yydgoto[] = {                         10,
   11,   12,   13,   14,   15,   64,   16,   67,   28,   17,
   18,   19,   43,   20,   21,   22,  103,   52,  104,   23,
   53,   54,
};
final static short yysindex[] = {                        28,
  -14,    0, -200,   19,   30,    0, -205,   36, -177,    0,
  -10,    0,    0,    0,   28,   87,    0,    0, -220,    0,
    0, -109, -180,    0,  -13,  -21,  -40,   -5,   34,   58,
  -14,  -59,    0,    0,    0,    0, -150,   30, -146,   87,
 -108,    0,  -11,   67,   28,  -21,   28,    0, -227, -144,
  -23,   -2,    5,    0,  -18,  -32,    0,    0, -142,    0,
    0,    0,    0,   52,   62,   30,    0, -150,    0,   28,
    0, -129,    0,  -48,   29,    9,    0,    0,    0,    0,
    0,    0,   38,    0,  -21,  -21,  -21,  -21,    0,   51,
  -16,    0,   52,    0,    0, -132,   75,   87,   17,    0,
    0,    0,    0,   95,    0,   96,    0,    5,    5,    0,
    0,    0,    0,   56,    0,    0,   87,    0,   97,  -21,
  -21,    0,  -21,   57,   63,   64,   16,   18,   23,    0,
    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0, -127,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  -93,    0,    0,    0,    0,    0,  -67,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  -39,    0,    0,    0,    0,    0,  -67,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  -38,  -33,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,
};
final static short yygindex[] = {                         0,
   20,  275,  -30,   21,   22,   74,    0,    0,  -22,    0,
    0,    0,    0,    0,    0,    0,    0,    4,    0,    0,
  -17,   41,
};
final static int YYTABLESIZE=374;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         51,
   17,   44,   42,   44,   42,   44,   42,   43,   92,   43,
   85,   43,   86,   45,   70,   65,   51,   82,   51,   44,
   42,   51,   89,   51,  113,   43,    5,   32,   77,   17,
   56,   17,   72,   94,    9,   37,   39,   40,   41,   42,
   85,   78,   86,   97,   24,    9,   87,   71,   59,   75,
   29,   88,   30,   50,   83,    5,   84,    5,   90,   25,
   68,   40,  115,    8,   74,   62,   76,  108,  109,   27,
   50,   85,   50,   86,    8,   50,  102,   50,  107,   26,
   85,   33,   86,    9,   95,   96,   40,  105,    9,   99,
   46,  112,   60,   85,  114,   86,  122,  127,   85,   85,
   86,   86,    9,  128,  129,   85,   85,   86,   86,   47,
    9,   79,    8,  116,   63,   80,   61,    8,   95,   40,
   66,    9,   93,  124,  125,   73,  126,  110,  111,    9,
  100,    8,  117,  118,  120,  121,  123,  116,   40,    8,
  130,   98,  131,   41,    0,    9,   44,  132,    0,    0,
    8,   69,    0,    0,    0,    9,    0,    0,    8,    0,
    0,    0,   17,   17,    0,   17,   17,    0,   17,   17,
    0,   17,   17,   17,    8,    0,   17,    0,   17,    0,
    9,    0,    0,    0,    8,    0,    0,    0,    5,    5,
    0,    5,    5,    0,    5,    5,    1,    2,    5,    3,
    4,    0,    5,    5,    5,    0,    6,    1,    2,    8,
    3,    4,    7,  101,    5,    0,    0,    6,    0,   48,
   49,    0,    0,    7,    0,    0,    0,   55,   44,   42,
    0,    0,   81,    0,   43,   91,   48,   49,   48,   49,
    0,   48,   49,   48,   49,    1,    2,    0,    3,    4,
    1,    2,    5,    3,    4,    6,    0,    5,   57,   34,
    6,    7,    0,    0,    1,    2,    7,    3,    4,    0,
  106,    5,    1,    2,    6,    3,    4,    0,  119,    5,
    7,    0,    6,    1,    2,   35,    3,    4,    7,   36,
    5,   31,    2,    6,    3,    4,    0,    0,    5,    7,
    0,    6,   58,    0,    0,    0,   35,    7,    2,    0,
    3,    4,    0,    0,    5,    0,    0,    6,    2,    0,
    3,    4,    0,    7,   38,   57,    0,    0,    0,    0,
    0,    0,    0,    7,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    2,    0,    3,    4,    0,   35,   38,
   35,    0,    0,    0,    0,    0,    0,    0,    7,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   35,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
   94,   41,   41,   43,   43,   45,   45,   41,   41,   43,
   43,   45,   45,  123,  123,   38,   40,   41,   40,   59,
   59,   40,   41,   40,   41,   59,   94,    8,  256,  123,
   27,  125,   44,   64,   94,   15,   16,   16,  259,  260,
   43,  269,   45,   66,   59,   94,   42,   59,   28,   46,
  256,   47,  258,   94,   51,  123,   59,  125,   55,  260,
   40,   40,   93,  123,   45,  125,   47,   85,   86,   40,
   94,   43,   94,   45,  123,   94,  125,   94,   41,   61,
   43,  259,   45,   94,   64,   65,   65,   59,   94,   70,
  271,   41,   59,   43,   91,   45,   41,   41,   43,   43,
   45,   45,   94,   41,   41,   43,   43,   45,   45,  123,
   94,  256,  123,   93,  265,  260,   59,  123,   98,   98,
  267,   94,  265,  120,  121,   59,  123,   87,   88,   94,
  260,  123,  265,   59,   40,   40,   40,  117,  117,  123,
  125,   68,  125,  271,   -1,   94,  256,  125,   -1,   -1,
  123,  260,   -1,   -1,   -1,   94,   -1,   -1,  123,   -1,
   -1,   -1,  256,  257,   -1,  259,  260,   -1,  262,  263,
   -1,  265,  266,  267,  123,   -1,  270,   -1,  272,   -1,
   94,   -1,   -1,   -1,  123,   -1,   -1,   -1,  256,  257,
   -1,  259,  260,   -1,  262,  263,  256,  257,  266,  259,
  260,   -1,  270,  263,  272,   -1,  266,  256,  257,  123,
  259,  260,  272,  262,  263,   -1,   -1,  266,   -1,  260,
  261,   -1,   -1,  272,   -1,   -1,   -1,  268,  268,  268,
   -1,   -1,  256,   -1,  268,  268,  260,  261,  260,  261,
   -1,  260,  261,  260,  261,  256,  257,   -1,  259,  260,
  256,  257,  263,  259,  260,  266,   -1,  263,  264,  270,
  266,  272,   -1,   -1,  256,  257,  272,  259,  260,   -1,
  262,  263,  256,  257,  266,  259,  260,   -1,  262,  263,
  272,   -1,  266,  256,  257,   11,  259,  260,  272,   15,
  263,  256,  257,  266,  259,  260,   -1,   -1,  263,  272,
   -1,  266,   28,   -1,   -1,   -1,   32,  272,  257,   -1,
  259,  260,   -1,   -1,  263,   -1,   -1,  266,  257,   -1,
  259,  260,   -1,  272,  263,  264,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  272,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  257,   -1,  259,  260,   -1,   74,  263,
   76,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  272,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   99,
};
}
final static short YYFINAL=10;
final static short YYMAXTOKEN=272;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'",null,"'/'",null,null,null,null,null,null,null,null,null,null,null,"';'",
null,"'='",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"'^'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,"UINTEGER","STRING","FUN","ID",
"CONSTANT","RETURN","IF","THEN","ELSE","DO","UNTIL","COMPARATOR","UI",
"ENDOFFILE","ASSIGN","PRINT",
};
final static String yyrule[] = {
"$accept : input",
"input : program ENDOFFILE",
"program : sentence",
"program : program sentence",
"sentence : open_sentence",
"sentence : closed_sentence",
"sentence : error ';'",
"open_sentence : if_sentence sentence",
"open_sentence : if_sentence closed_sentence else_sentence open_sentence",
"open_sentence : do_sentence closed_sentence until_sentence",
"open_sentence : IF condition closed_sentence ELSE open_sentence",
"open_sentence : IF condition sentence",
"closed_sentence : if_sentence closed_sentence else_sentence closed_sentence",
"closed_sentence : IF condition closed_sentence ELSE closed_sentence",
"closed_sentence : declaration",
"closed_sentence : execute",
"closed_sentence : '{' program '}'",
"closed_sentence : '{' error",
"if_sentence : IF condition THEN",
"else_sentence : ELSE",
"do_sentence : DO",
"until_sentence : UNTIL condition ';'",
"declaration : type declaration_names ';'",
"declaration_names : ID",
"declaration_names : declaration_names ',' ID",
"type : UINTEGER",
"type : '^' FUN",
"execute : function",
"execute : assignment",
"execute : PRINT STRING ';'",
"execute : PRINT error ';'",
"function : function_header '{' program function_end",
"function : FUN ID '{' program RETURN '(' expression ')' '}'",
"function : function_header '{' program '}'",
"function : type FUN '{' program RETURN '(' expression ')' '}'",
"function : function_header error ';'",
"function_header : type FUN ID",
"function_end : function_return '(' expression ')' '}'",
"function_return : RETURN",
"assignment : result ASSIGN expression ';'",
"assignment : ID '=' expression ';'",
"result : ID",
"expression : expression '+' term",
"expression : expression '-' term",
"expression : term",
"term : term '*' factor",
"term : term '/' factor",
"term : factor",
"factor : ID",
"factor : CONSTANT UI",
"factor : CONSTANT error",
"factor : '(' expression ')'",
"factor : '^' ID",
"factor : '(' ')'",
"factor : '(' error",
"factor : '^' error",
"condition : '(' expression COMPARATOR expression ')'",
"condition : '(' expression COMPARATOR ')'",
"condition : '(' expression ')'",
"condition : '(' COMPARATOR ')'",
"condition : '(' COMPARATOR expression ')'",
};

//#line 176 "grammar.y"


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

//#line 463 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 6:
//#line 40 "grammar.y"
{event.eventError("Error en sentencia",lex.getLineNumber(),"parser");}
break;
case 7:
//#line 43 "grammar.y"
{ event.eventNewRule("Sentencia IF",lex.getLineNumber());
					 rp.endIf(); }
break;
case 8:
//#line 45 "grammar.y"
{ event.eventNewRule("Sentencia IF",lex.getLineNumber()); 
							rp.endElse(); }
break;
case 9:
//#line 47 "grammar.y"
{ event.eventNewRule("Sentencia DO",lex.getLineNumber()); }
break;
case 10:
//#line 48 "grammar.y"
{event.eventError("Sentencia IF incompleta, falta THEN?",lex.getLineNumber(),"parser");}
break;
case 11:
//#line 49 "grammar.y"
{event.eventError("Sentencia IF incompleta, falta THEN?",lex.getLineNumber(),"parser");}
break;
case 12:
//#line 53 "grammar.y"
{ event.eventNewRule("Sentencia IF",lex.getLineNumber()); 
								rp.endElse(); }
break;
case 13:
//#line 55 "grammar.y"
{event.eventError("Sentencia IF incompleta, falta THEN?",lex.getLineNumber(),"parser");}
break;
case 16:
//#line 58 "grammar.y"
{ event.eventNewRule("Nuevo Bloque",lex.getLineNumber()); }
break;
case 17:
//#line 59 "grammar.y"
{event.eventError("Error delimitadores de bloques",lex.getLineNumber(),"parser");}
break;
case 18:
//#line 62 "grammar.y"
{ rp.startIf();}
break;
case 19:
//#line 65 "grammar.y"
{ rp.startElse();}
break;
case 20:
//#line 68 "grammar.y"
{rp.startDo();}
break;
case 21:
//#line 71 "grammar.y"
{rp.endDo();}
break;
case 22:
//#line 76 "grammar.y"
{	
					event.eventNewRule("Declaración",lex.getLineNumber()); }
break;
case 23:
//#line 81 "grammar.y"
{ 	IdentifierEntry entry = ((IdentifierEntry)val_peek(0).obj);
				if (!entry.check()){
					entry.setValueType(type);
 					entry.declare();
				} else event.eventError("Variable " + entry.getLexeme() + " ya declarada" , lex.getLineNumber(), 										"Generador de código intermedio" ) ;
			}
break;
case 24:
//#line 88 "grammar.y"
{ 	((IdentifierEntry)val_peek(0).obj).setValueType(type);
				((IdentifierEntry)val_peek(0).obj).declare();}
break;
case 25:
//#line 92 "grammar.y"
{type = TableEntry.CONST_UINT ; }
break;
case 26:
//#line 93 "grammar.y"
{ type = TableEntry.FUNCTION_POINTER;}
break;
case 27:
//#line 98 "grammar.y"
{ event.eventNewRule("Funcion",lex.getLineNumber()); }
break;
case 28:
//#line 99 "grammar.y"
{ event.eventNewRule("Asignación",lex.getLineNumber()); }
break;
case 29:
//#line 100 "grammar.y"
{ 
			rp.addItem((TableEntry)val_peek(1).obj);
			rp.addItem((TableEntry)val_peek(2).obj);
			event.eventNewRule("Print",lex.getLineNumber()); }
break;
case 30:
//#line 104 "grammar.y"
{event.eventError("Error en sentencia Print",lex.getLineNumber(),"parser");}
break;
case 32:
//#line 108 "grammar.y"
{event.eventError("Funcion no tiene tipo de retorno",lex.getLineNumber(),"parser");}
break;
case 33:
//#line 109 "grammar.y"
{event.eventError("Funcion sin retorno",lex.getLineNumber(),"parser");}
break;
case 34:
//#line 110 "grammar.y"
{event.eventError("Funcion sin nombre",lex.getLineNumber(),"parser");}
break;
case 35:
//#line 111 "grammar.y"
{event.eventError("Funcion no delimitada",lex.getLineNumber(),"parser");}
break;
case 36:
//#line 115 "grammar.y"
{	
					IdentifierEntry entry = (IdentifierEntry)val_peek(0).obj ;
					entry.setValueType(TableEntry.FUNCTION_NAME);
					entry.declare();
					rp.startFunction(entry);
					
				}
break;
case 37:
//#line 124 "grammar.y"
{rp.endFunction();}
break;
case 38:
//#line 127 "grammar.y"
{ rp.addItem(ReversePolish.rtn);}
break;
case 39:
//#line 132 "grammar.y"
{	if(((TableEntry)val_peek(3).obj).getValueType()==TableEntry.FUNCTION_NAME)
							event.eventError("Asignación de función",lex.getLineNumber(),"parser");
						rp.addItem((TableEntry)val_peek(2).obj);	 
						rp.endPtrAssigment(); /*Patch*/
 					}
break;
case 40:
//#line 137 "grammar.y"
{event.eventError("Asignación mal enunciada",lex.getLineNumber(),"parser");}
break;
case 41:
//#line 141 "grammar.y"
{   if(((TableEntry)val_peek(0).obj).getValueType()==TableEntry.FUNCTION_POINTER){rp.setPtrAssigment();} /*Patch*/
		rp.addItem((TableEntry)val_peek(0).obj);}
break;
case 42:
//#line 145 "grammar.y"
{rp.addItem((TableEntry)val_peek(1).obj);
				   }
break;
case 43:
//#line 147 "grammar.y"
{rp.addItem((TableEntry)val_peek(1).obj);}
break;
case 45:
//#line 151 "grammar.y"
{rp.addItem((TableEntry)val_peek(1).obj);}
break;
case 46:
//#line 152 "grammar.y"
{rp.addItem((TableEntry)val_peek(1).obj); }
break;
case 48:
//#line 156 "grammar.y"
{rp.addItem((TableEntry)(clone(val_peek(0))).obj);}
break;
case 49:
//#line 157 "grammar.y"
{rp.addItem((TableEntry)(clone(val_peek(1))).obj);}
break;
case 50:
//#line 158 "grammar.y"
{event.eventError("Mala enunciación de constante",lex.getLineNumber(),"parser");}
break;
case 52:
//#line 160 "grammar.y"
{rp.addItem((TableEntry)val_peek(1).obj);}
break;
case 53:
//#line 161 "grammar.y"
{event.eventError("Paréntesis vacíos",lex.getLineNumber(),"parser");}
break;
case 54:
//#line 162 "grammar.y"
{event.eventError("Paréntesis impares",lex.getLineNumber(),"parser");}
break;
case 55:
//#line 163 "grammar.y"
{event.eventError("Error en asignación",lex.getLineNumber(),"parser");}
break;
case 56:
//#line 168 "grammar.y"
{rp.addItem((TableEntry)val_peek(2).obj);}
break;
case 57:
//#line 169 "grammar.y"
{event.eventError("Error en condicción",lex.getLineNumber(),"parser");}
break;
case 58:
//#line 170 "grammar.y"
{event.eventError("Error en condicción",lex.getLineNumber(),"parser");}
break;
case 59:
//#line 171 "grammar.y"
{event.eventError("Error en condicción",lex.getLineNumber(),"parser");}
break;
case 60:
//#line 172 "grammar.y"
{event.eventError("Error en condicción",lex.getLineNumber(),"parser");}
break;
//#line 833 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
