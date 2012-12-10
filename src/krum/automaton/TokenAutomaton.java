package krum.automaton;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RunAutomaton;

public class TokenAutomaton extends RunAutomaton {
	private static final long serialVersionUID = 1L;

	private static final int NO_ACCEPT = -1;
	private static final int NO_STEP = -1;
	
	/**
	 * Searches for the longest run of the specified character sequence,
	 * beginning at the specified index, that is accepted by this automaton.
	 * <p>
	 * This method returns true if an accepted run is found and a longer
	 * accepted run is not possible.  The fields of <tt>result</tt> will be
	 * filled with the token details.  The value of <tt>result.info</tt> is
	 * copied from the <tt>info</tt> field of the accepting state.
	 * <p>
	 * This method returns false if no accepted run is found, or if a longer
	 * accepted run is possible and <tt>endOfInput</tt> is false.  These
	 * conditions are distinguished by the value of <tt>result.info</tt>,
	 * which will be identity-equivalent (comparable via the equality
	 * operator) to either <tt>TokenResult.NO_MATCH</tt> or
	 * <tt>TokenResult.UNDERFLOW</tt>.
	 * 
	 * @param result
	 * @return
	 * @see dk.brics.automaton.State#setInfo(Object)
	 * @see dk.brics.automaton.RunAutomaton#getInfo(int)
	 */
	public boolean find(CharSequence seq, int off, boolean endOfInput, TokenResult result) {
		int maxAccept = NO_ACCEPT;
		Object info = null;
		int s = this.getInitialState();
		int l = seq.length();
		for(int i = off; i < l; ++i) {
			s = step(s, seq.charAt(i));
			if(s == NO_STEP) {
				if(maxAccept == NO_ACCEPT) {
					result.info = TokenResult.NO_MATCH;
					return false;
				}
				else {
					result.seq = seq;
					result.off = off;
					result.len = maxAccept - result.off + 1;
					result.info = info;
					return true;
				}
			}
			else {
				if(this.isAccept(s)) {
					maxAccept = i;
					info = this.info[s];
				}
			}
		}
		// stepped to end of sequence
		if(endOfInput == false && this.hasTransitions(s)) {
			result.info = TokenResult.UNDERFLOW;
			return false;
		}
		else {
			if(maxAccept == NO_ACCEPT) {
				result.info = TokenResult.NO_MATCH;
				return false;
			}
			else {
				result.seq = seq;
				result.off = off;
				result.len = maxAccept - result.off + 1;
				result.info = info;
				return true;
			}
		}
	}
	
	
	/**
	 * Returns true if the given state has any transitions.
	 */
	protected boolean hasTransitions(int state) {
		if(!accept[state]) return true;
		int l = (state + 1) * points.length;
		for(int i = state * points.length; i < l; ++i) {
			if(transitions[i] != -1) return true;
		}
		return false;
	}
	
	
	public TokenAutomaton(Automaton a, boolean tableize) {
		super(a, tableize);
	}

	
	public TokenAutomaton(Automaton a) {
		super(a);
	}
	
	
	public static TokenAutomaton load(InputStream stream) throws IOException, ClassNotFoundException {
		ObjectInputStream s = new ObjectInputStream(stream);
		return (TokenAutomaton) s.readObject();
	}

}
