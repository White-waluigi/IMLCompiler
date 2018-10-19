package imlcompiler.Scanner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TokenMap {
	ArrayList<Character> delimiters=new ArrayList<>();
	public class Element {
		String group;
		String attribute;
		String lexeme;
		

		
		public Element(String group, String lexeme, String attribute) {
			this.group = group;
			this.lexeme = lexeme;
			this.attribute=attribute;
			if (lexeme==null|| group==null){
				throw new ScannerErrorException("Token Template has no group or Lexeme:\n"+lexeme+"\t"+group);
			}
		}
		
	}
	public boolean isDelimiter(char a) {
		if(delimiters.contains(a)) {
			return true;
		}
		return false;
	}
	ArrayList<Element> elements=new ArrayList<>();
	private String currentAttribute;
	
	public String GetGroupOfElement(String lexeme) {
		for(Element e: elements) {
			if(e.lexeme.equals(lexeme)) {
				return e.group;
			}
		}
		return null;
	}
	public String GetAttributeOfElement(String lexeme) {
		for(Element e: elements) {
			if(e.lexeme.equals(lexeme)) {
				return e.attribute;
			}
		}
		return null;
	}
	public TokenMap(String a) {
		
		String currentGroup=null;
		try (BufferedReader br = new BufferedReader(new FileReader(a))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	line=line.replaceAll("\\s+","");
		    	
		    	if(line.startsWith("#")) {
		    		currentGroup=line.substring(1);	
		    		currentAttribute=null;

		    	}else if(line.startsWith("@")) {
		    		currentAttribute=line.substring(1);
		    	}else {
		    		if(line.startsWith("§")) {
		    			line=line.substring(1);
		    			this.delimiters.add(line.charAt(0));
		    			
		    		}
		    		elements.add(new Element(currentGroup, line, currentAttribute));
		    	}
		    	
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
