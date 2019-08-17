package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.spi.orb.ORB;
import java.nio.ByteBuffer;

public class BufferManagerReadGrow implements BufferManagerRead, MarkAndResetHandler {
  private ORB orb;
  
  private ORBUtilSystemException wrapper;
  
  private Object streamMemento;
  
  private RestorableInputStream inputStream;
  
  private boolean markEngaged = false;
  
  BufferManagerReadGrow(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.encoding");
  }
  
  public void processFragment(ByteBuffer paramByteBuffer, FragmentMessage paramFragmentMessage) {}
  
  public void init(Message paramMessage) {}
  
  public ByteBufferWithInfo underflow(ByteBufferWithInfo paramByteBufferWithInfo) { throw this.wrapper.unexpectedEof(); }
  
  public void cancelProcessing(int paramInt) {}
  
  public MarkAndResetHandler getMarkAndResetHandler() { return this; }
  
  public void mark(RestorableInputStream paramRestorableInputStream) {
    this.markEngaged = true;
    this.inputStream = paramRestorableInputStream;
    this.streamMemento = this.inputStream.createStreamMemento();
  }
  
  public void fragmentationOccured(ByteBufferWithInfo paramByteBufferWithInfo) {}
  
  public void reset() {
    if (!this.markEngaged)
      return; 
    this.markEngaged = false;
    this.inputStream.restoreInternalState(this.streamMemento);
    this.streamMemento = null;
  }
  
  public void close(ByteBufferWithInfo paramByteBufferWithInfo) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\BufferManagerReadGrow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */