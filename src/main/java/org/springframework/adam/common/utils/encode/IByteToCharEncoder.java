package org.springframework.adam.common.utils.encode;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public interface IByteToCharEncoder extends ICoder<ByteBuffer, CharBuffer>{
	// Below not currently supported by javac: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6294779
	// public IByteToCharEncoder clone() throws CloneNotSupportedException;
}
