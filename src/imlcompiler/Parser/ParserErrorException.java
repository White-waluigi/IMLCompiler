package imlcompiler.Parser;
//todo change to Scanner
public class ParserErrorException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public ParserErrorException(String s) {
		super(s);	
		if(s==null||s=="null"||s=="") {
			throw new RuntimeException();
		}
		
	}
	
}
