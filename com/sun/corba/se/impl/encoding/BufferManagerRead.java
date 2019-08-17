package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import java.nio.ByteBuffer;

public interface BufferManagerRead {
  void processFragment(ByteBuffer paramByteBuffer, FragmentMessage paramFragmentMessage);
  
  ByteBufferWithInfo underflow(ByteBufferWithInfo paramByteBufferWithInfo);
  
  void init(Message paramMessage);
  
  MarkAndResetHandler getMarkAndResetHandler();
  
  void cancelProcessing(int paramInt);
  
  void close(ByteBufferWithInfo paramByteBufferWithInfo);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\BufferManagerRead.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */