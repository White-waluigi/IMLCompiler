package imlcompiler;

import java.io.File;

public class CompilerTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File folder = new File("examplePrograms");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile() &&listOfFiles[i].getName().endsWith(".iml") && !listOfFiles[i].getName().startsWith("INV")) {
			System.out.println("fibbonacci\t" +listOfFiles[i].getName());
		    Compiler.parse("examplePrograms/"+listOfFiles[i].getName(),false);
		  }
		}
		
		System.out.println("*** Testing Successfull ***");
	}

}
