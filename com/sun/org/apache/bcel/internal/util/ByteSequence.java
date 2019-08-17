package com.sun.org.apache.bcel.internal.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public final class ByteSequence extends DataInputStream {
  private ByteArrayStream byte_stream = (ByteArrayStream)this.in;
  
  public ByteSequence(byte[] paramArrayOfByte) { super(new ByteArrayStream(paramArrayOfByte)); }
  
  public final int getIndex() { return this.byte_stream.getPosition(); }
  
  final void unreadByte() { this.byte_stream.unreadByte(); }
  
  private static final class ByteArrayStream extends ByteArrayInputStream {
    ByteArrayStream(byte[] param1ArrayOfByte) { super(param1ArrayOfByte); }
    
    final int getPosition() { return this.pos; }
    
    final void unreadByte() {
      if (this.pos > 0)
        this.pos--; 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\ByteSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */