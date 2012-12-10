package krum.automaton;

/**
 * Data structure for returning token info from a <tt>TokenAutomaton</tt>.
 *
 * @author Kevin Krumwiede (kjkrum@gmail.com)
 * @see TokenAutomaton#find(TokenResult, boolean)
 */
public class TokenResult {
	/**
	 * Indicates that no match is possible.
	 */
	public static final Object NO_MATCH = (Object) "NO_MATCH";
	
	/**
	 * Indicates that a match extends to the end of the character sequence and
	 * could be longer.
	 */
	public static final Object UNDERFLOW = (Object) "UNDERFLOW";
	
	/**
	 * The character sequence containing the token.
	 */
	public CharSequence seq;
	
	/**
	 * Offset of the first character of the token.
	 */
	public int off;
	
	/**
	 * Length of the token.
	 */
	public int len;
		
	/**
	 * Object associated with the token, or one of the constants
	 * <tt>NO_MATCH</tt> or <tt>UNDERFLOW</tt>.
	 */
	public Object info;
}
