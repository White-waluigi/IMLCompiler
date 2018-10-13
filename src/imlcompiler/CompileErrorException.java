package imlcompiler;

public class CompileErrorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public CompileErrorException(String s) {
		super(s);	
		if(s==null||s=="null"||s=="") {
			throw new RuntimeException();
		}
		
	}
	
}
