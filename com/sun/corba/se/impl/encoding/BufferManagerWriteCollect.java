package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;

public class BufferManagerWriteCollect extends BufferManagerWrite {
  private BufferQueue queue = new BufferQueue();
  
  private boolean sentFragment = false;
  
  private boolean debug = false;
  
  BufferManagerWriteCollect(ORB paramORB) {
    super(paramORB);
    if (paramORB != null)
      this.debug = paramORB.transportDebugFlag; 
  }
  
  public boolean sentFragment() { return this.sentFragment; }
  
  public int getBufferSize() { return this.orb.getORBData().getGIOPFragmentSize(); }
  
  public void overflow(ByteBufferWithInfo paramByteBufferWithInfo) {
    MessageBase.setFlag(paramByteBufferWithInfo.byteBuffer, 2);
    this.queue.enqueue(paramByteBufferWithInfo);
    ByteBufferWithInfo byteBufferWithInfo = new ByteBufferWithInfo(this.orb, this);
    byteBufferWithInfo.fragmented = true;
    ((CDROutputObject)this.outputObject).setByteBufferWithInfo(byteBufferWithInfo);
    FragmentMessage fragmentMessage = ((CDROutputObject)this.outputObject).getMessageHeader().createFragmentMessage();
    fragmentMessage.write((CDROutputObject)this.outputObject);
  }
  
  public void sendMessage() {
    this.queue.enqueue(((CDROutputObject)this.outputObject).getByteBufferWithInfo());
    Iterator iterator = iterator();
    connection = ((OutputObject)this.outputObject).getMessageMediator().getConnection();
    connection.writeLock();
    try {
      ByteBufferPool byteBufferPool = this.orb.getByteBufferPool();
      while (iterator.hasNext()) {
        ByteBufferWithInfo byteBufferWithInfo = (ByteBufferWithInfo)iterator.next();
        ((CDROutputObject)this.outputObject).setByteBufferWithInfo(byteBufferWithInfo);
        connection.sendWithoutLock((CDROutputObject)this.outputObject);
        this.sentFragment = true;
        if (this.debug) {
          int i = System.identityHashCode(byteBufferWithInfo.byteBuffer);
          StringBuffer stringBuffer = new StringBuffer(80);
          stringBuffer.append("sendMessage() - releasing ByteBuffer id (");
          stringBuffer.append(i).append(") to ByteBufferPool.");
          String str = stringBuffer.toString();
          dprint(str);
        } 
        byteBufferPool.releaseByteBuffer(byteBufferWithInfo.byteBuffer);
        byteBufferWithInfo.byteBuffer = null;
        byteBufferWithInfo = null;
      } 
      this.sentFullMessage = true;
    } finally {
      connection.writeUnlock();
    } 
  }
  
  public void close() {
    Iterator iterator = iterator();
    ByteBufferPool byteBufferPool = this.orb.getByteBufferPool();
    while (iterator.hasNext()) {
      ByteBufferWithInfo byteBufferWithInfo = (ByteBufferWithInfo)iterator.next();
      if (byteBufferWithInfo != null && byteBufferWithInfo.byteBuffer != null) {
        if (this.debug) {
          int i = System.identityHashCode(byteBufferWithInfo.byteBuffer);
          StringBuffer stringBuffer = new StringBuffer(80);
          stringBuffer.append("close() - releasing ByteBuffer id (");
          stringBuffer.append(i).append(") to ByteBufferPool.");
          String str = stringBuffer.toString();
          dprint(str);
        } 
        byteBufferPool.releaseByteBuffer(byteBufferWithInfo.byteBuffer);
        byteBufferWithInfo.byteBuffer = null;
        byteBufferWithInfo = null;
      } 
    } 
  }
  
  private void dprint(String paramString) { ORBUtility.dprint("BufferManagerWriteCollect", paramString); }
  
  private Iterator iterator() { return new BufferManagerWriteCollectIterator(null); }
  
  private class BufferManagerWriteCollectIterator implements Iterator {
    private BufferManagerWriteCollectIterator() {}
    
    public boolean hasNext() { return (BufferManagerWriteCollect.this.queue.size() != 0); }
    
    public Object next() { return BufferManagerWriteCollect.this.queue.dequeue(); }
    
    public void remove() { throw new UnsupportedOperationException(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\BufferManagerWriteCollect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */