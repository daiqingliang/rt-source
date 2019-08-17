package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.SystemException;

public class BufferManagerWriteStream extends BufferManagerWrite {
  private int fragmentCount = 0;
  
  BufferManagerWriteStream(ORB paramORB) { super(paramORB); }
  
  public boolean sentFragment() { return (this.fragmentCount > 0); }
  
  public int getBufferSize() { return this.orb.getORBData().getGIOPFragmentSize(); }
  
  public void overflow(ByteBufferWithInfo paramByteBufferWithInfo) {
    MessageBase.setFlag(paramByteBufferWithInfo.byteBuffer, 2);
    try {
      sendFragment(false);
    } catch (SystemException systemException) {
      this.orb.getPIHandler().invokeClientPIEndingPoint(2, systemException);
      throw systemException;
    } 
    paramByteBufferWithInfo.position(0);
    paramByteBufferWithInfo.buflen = paramByteBufferWithInfo.byteBuffer.limit();
    paramByteBufferWithInfo.fragmented = true;
    FragmentMessage fragmentMessage = ((CDROutputObject)this.outputObject).getMessageHeader().createFragmentMessage();
    fragmentMessage.write((CDROutputObject)this.outputObject);
  }
  
  private void sendFragment(boolean paramBoolean) {
    connection = ((OutputObject)this.outputObject).getMessageMediator().getConnection();
    connection.writeLock();
    try {
      connection.sendWithoutLock((OutputObject)this.outputObject);
      this.fragmentCount++;
    } finally {
      connection.writeUnlock();
    } 
  }
  
  public void sendMessage() {
    sendFragment(true);
    this.sentFullMessage = true;
  }
  
  public void close() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\BufferManagerWriteStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */