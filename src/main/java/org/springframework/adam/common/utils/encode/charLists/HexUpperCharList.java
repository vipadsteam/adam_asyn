package org.springframework.adam.common.utils.encode.charLists;

public class HexUpperCharList extends ReadOnlyCharList{
	
	public HexUpperCharList(){
		super(build());
	}
	
	public static char[] build(){
		char[] chars = new char[0x10];
		for(int i=0; i < chars.length; i++){
			chars[i] = Character.toUpperCase(Character.forDigit(i, chars.length));
		}
		return chars;
	}
	
}
