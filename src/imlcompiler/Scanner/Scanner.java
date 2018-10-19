package imlcompiler.Scanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Scanner {
	public String currentWord;
	private int cline;
	private int cpos;
	private TokenList tokenList;
	TokenMap tm=new TokenMap("tokenClass.cmp");
	private String filename;
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
	//symbols and whitespaces
	public int State0(char a) {
		if("//".equals(currentWord+a)) {
			return 3;	
		}
		addToken(currentWord);
		clearWord();
		
		if(Character.isDigit(a)) {
			return 2;
		}
		if(Character.isLetter(a)) {
			return 1;
		}
		return 0;
	}


	//letters
	public int State1(char a) {
		if(  !(Character.isLetterOrDigit(a)) ) {
			if(!addToken(currentWord)) {
				addToken(new Token(Token.Terminal.IDENT, new Token.IdentAttribute(currentWord), currentWord));
			}
			
			return 0;
		}
		

		return 1;
		
		
	}
	//numbers
	public int State2(char a) {

		if(!(Character.isLetterOrDigit(a))) {
			
			addToken(new Token(Token.Terminal.LITERAL, new Token.IntAttribute(Integer.parseInt(currentWord)), currentWord));
			return 0;
		}
		return 2;
	}
	//comments
	public int State3(char a) {
		if('\n'==a) {
			clearWord();
			return 0;
		}
		return 3;
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
		default:
			next=StateE(a);
			break;
		}
		
		return next;
	}
}
