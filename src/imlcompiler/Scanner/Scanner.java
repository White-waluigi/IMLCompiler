package imlcompiler.Scanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.sun.org.apache.xalan.internal.xsltc.compiler.CompilerException;

public class Scanner {
	public String currentWord;
	private int cline;
	private int cpos;
	private TokenList tokenList;
	TokenMap tm=new TokenMap("tokenClass.cmp");
	private String filename;
	
	int next_=0;
//	char[] illegalChars= {'@'};
	public Scanner(String filename) {
		this.filename=filename;
		tokenList=new TokenList();
	}

	private void clearWord() {
		currentWord="";
	}
	private void addToken(Token token) {
		if(token!=null) {
			tokenList.add(token);
		}
		clearWord();
	}
	private boolean addToken(String lexeme) {
		//System.out.println(next_+" adding:\t"+lexeme);
		String group=tm.GetGroupOfElement(lexeme);
		String attribute=tm.GetAttributeOfElement(lexeme);
		if(group==null) {
			return false;
		}
		addToken( new Token(Token.Terminal.valueOf(group),Token.getAttributeEnum(attribute),currentWord) );
		return true;
	}
	private boolean contains(char c, char[] array) {
		for (char x : array) {
			if (x == c) {
				return true;
			}
		}
		return false;
	}
	//delimiters and whitespaces
	boolean isState0Char(char a){
		return Character.isWhitespace(a)|| tm.isDelimiter(a) || ((char)-1)==a;
	}
	public int State0(char a) {

		addToken(currentWord);
		clearWord();

		if(Character.isDigit(a)) {
			return 2;
		}
		if(Character.isLetter(a)) {
			return 1;
		}
		if(!isState0Char(a)) {
			return 3;
		}	
		return 0;
	}


	//letters
	public int State1(char a) {
		if(  !(Character.isLetterOrDigit(a)) ) {
			if(!addToken(currentWord)) {
				addToken(new Token(Token.Terminal.IDENT, new Token.IdentAttribute(currentWord), currentWord));
			}
			if(isState0Char(a))
				return 0;
			else
				return 3;
		}
		

		return 1;
		
		
	}
	//numbers
	public int State2(char a) {

		if(!(Character.isLetterOrDigit(a))) {
			
			addToken(new Token(Token.Terminal.LITERAL, new Token.IntAttribute(Integer.parseInt(currentWord)), currentWord));
			
			if(isState0Char(a))
				return 0;
			else
				return 3;
		}
		return 2;
	}
	//symbols
	public int State3(char a) {
		if("//".equals(currentWord+a)) {
			return 4;	
		}
		if(isState0Char(a)||Character.isLetterOrDigit(a)) {
			if(!addToken(currentWord)) 
				throw new ScannerErrorException("Unrecognized Token: "+currentWord);
			
			
			if(isState0Char(a))
				return 0;
			else if(Character.isDigit(a))
				return 2;
			else
				return 1;
					
		}

		return 3;
	}
	//comments
	public int State4(char a) {
		if('\n'==a) {
			clearWord();
			return 0;
		}
		return 4;
	}
	
	//Error
	public int StateE(char a) {
		throw new ScannerErrorException("Compiler has entered Error state near token:\t"+currentWord+"\t"+cline+":"+cpos);
	}
	public TokenList run() throws IOException {
		
//		Iterator<Character> c = code.chars().mapToObj(cc -> (char) cc).collect(Collectors.toList()).iterator();
		int next=0;
		cline=0;
		cpos=0;
		clearWord();
		
		FileReader fileReader = new FileReader(filename);

		BufferedReader bufferedReader = new BufferedReader(fileReader);

		int c = 0;
		while (c != -1){
			c = bufferedReader.read();
			char a = (char)c;

			next=enterAutomaton(a,next);
			next_=next;
			currentWord += a;
			
			if('\n'==a) {
				cline++;
				cpos=0;
			}else {
				cpos++;
			}
		}
		
		bufferedReader.close();
		tokenList.add(new Token(Token.Terminal.SENTINEL));
		return tokenList;
	}
	int enterAutomaton(char a , int next) {
		switch(next) {
		case 0:
			next=State0(a);
			break;
		case 1:
			next=State1(a);
			break;
		case 2:
			next=State2(a);
			break;
		case 3:
			next=State3(a);
			break;
		case 4:
			next=State4(a);
			break;
		default:
			next=StateE(a);
			break;
		}
		
		return next;
	}
}
