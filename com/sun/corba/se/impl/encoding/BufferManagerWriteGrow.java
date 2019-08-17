package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;

public class BufferManagerWriteGrow extends BufferManagerWrite {
  BufferManagerWriteGrow(ORB paramORB) { super(paramORB); }
  
  public boolean sentFragment() { return false; }
  
  public int getBufferSize() {
    ORBData oRBData = null;
    int i = 1024;
    if (this.orb != null) {
      oRBData = this.orb.getORBData();
      if (oRBData != null) {
        i = oRBData.getGIOPBufferSize();
        dprint("BufferManagerWriteGrow.getBufferSize: bufferSize == " + i);
      } else {
        dprint("BufferManagerWriteGrow.getBufferSize: orbData reference is NULL");
      } 
    } else {
      dprint("BufferManagerWriteGrow.getBufferSize: orb reference is NULL");
    } 
    return i;
  }
  
  public void overflow(ByteBufferWithInfo paramByteBufferWithInfo) {
    paramByteBufferWithInfo.growBuffer(this.orb);
    paramByteBufferWithInfo.fragmented = false;
  }
  
  public void sendMessage() {
    connection = ((OutputObject)this.outputObject).getMessageMediator().getConnection();
    connection.writeLock();
    try {
      connection.sendWithoutLock((OutputObject)this.outputObject);
      this.sentFullMessage = true;
    } finally {
      connection.writeUnlock();
    } 
  }
  
  public void close() {}
  
  private void dprint(String paramString) {
    if (this.orb.transportDebugFlag)
      ORBUtility.dprint(this, paramString); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\BufferManagerWriteGrow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */