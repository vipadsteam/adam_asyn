package org.springframework.adam.common.utils.encode;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public interface ICharToByteDecoder extends ICoder<CharBuffer, ByteBuffer>{
	// Below not currently supported by javac: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6294779
	// public ICharToByteDecoder clone() throws CloneNotSupportedException;
}
