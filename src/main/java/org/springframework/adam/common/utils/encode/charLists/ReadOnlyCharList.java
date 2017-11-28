package org.springframework.adam.common.utils.encode.charLists;

import java.util.AbstractList;
import java.util.RandomAccess;

public class ReadOnlyCharList extends AbstractList<Character> implements RandomAccess{
	
	protected final char[] chars;
	
	public ReadOnlyCharList(char[] chars){
		this.chars = chars;
	}
	
	/**
	 * <p>Same as {@link #get(int)}, but avoids auto-boxing.</p>
	 */
	public char getChar(int index){
		return chars[index];
	}
	
	/**
	 * <p>Use {@link #getChar(int)} if a char primitive is desired, to avoid auto-boxing.</p>
	 */
	@Override
	public Character get(int index){
		return chars[index];
	}

	@Override
	public int size(){
		return chars.length;
	}

}
