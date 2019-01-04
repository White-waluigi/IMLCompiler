package imlcompiler.ScopeChecker;

public class ScopeCheckerErrorException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public ScopeCheckerErrorException(String s) {
		super(s);	
		if(s==null||s=="null"||s=="") {
			throw new RuntimeException();
		}
		
	}
	
}
