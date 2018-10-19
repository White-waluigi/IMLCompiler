package imlcompiler.Scanner;
//todo change to Scanner
public class ScannerErrorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ScannerErrorException(String s) {
		super(s);	
		if(s==null||s=="null"||s=="") {
			throw new RuntimeException();
		}
		
	}
	
}
