package com.sun.corba.se.pept.transport;

import java.nio.ByteBuffer;

public interface ByteBufferPool {
  ByteBuffer getByteBuffer(int paramInt);
  
  void releaseByteBuffer(ByteBuffer paramByteBuffer);
  
  int activeCount();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\pept\transport\ByteBufferPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */