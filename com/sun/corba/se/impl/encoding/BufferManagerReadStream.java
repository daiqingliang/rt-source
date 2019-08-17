package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.RequestCanceledException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.orb.ORB;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.ListIterator;

public class BufferManagerReadStream implements BufferManagerRead, MarkAndResetHandler {
  private boolean receivedCancel = false;
  
  private int cancelReqId = 0;
  
  private boolean endOfStream = true;
  
  private BufferQueue fragmentQueue = new BufferQueue();
  
  private long FRAGMENT_TIMEOUT = 60000L;
  
  private ORB orb;
  
  private ORBUtilSystemException wrapper;
  
  private boolean debug = false;
  
  private boolean markEngaged = false;
  
  private LinkedList fragmentStack = null;
  
  private RestorableInputStream inputStream = null;
  
  private Object streamMemento = null;
  
  BufferManagerReadStream(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.encoding");
    this.debug = paramORB.transportDebugFlag;
  }
  
  public void cancelProcessing(int paramInt) {
    synchronized (this.fragmentQueue) {
      this.receivedCancel = true;
      this.cancelReqId = paramInt;
      this.fragmentQueue.notify();
    } 
  }
  
  public void processFragment(ByteBuffer paramByteBuffer, FragmentMessage paramFragmentMessage) {
    ByteBufferWithInfo byteBufferWithInfo = new ByteBufferWithInfo(this.orb, paramByteBuffer, paramFragmentMessage.getHeaderLength());
    synchronized (this.fragmentQueue) {
      if (this.debug) {
        int i = System.identityHashCode(paramByteBuffer);
        StringBuffer stringBuffer = new StringBuffer(80);
        stringBuffer.append("processFragment() - queueing ByteBuffer id (");
        stringBuffer.append(i).append(") to fragment queue.");
        String str = stringBuffer.toString();
        dprint(str);
      } 
      this.fragmentQueue.enqueue(byteBufferWithInfo);
      this.endOfStream = !paramFragmentMessage.moreFragmentsToFollow();
      this.fragmentQueue.notify();
    } 
  }
  
  public ByteBufferWithInfo underflow(ByteBufferWithInfo paramByteBufferWithInfo) {
    ByteBufferWithInfo byteBufferWithInfo = null;
    synchronized (this.fragmentQueue) {
      if (this.receivedCancel)
        throw new RequestCanceledException(this.cancelReqId); 
      while (this.fragmentQueue.size() == 0) {
        if (this.endOfStream)
          throw this.wrapper.endOfStream(); 
        boolean bool = false;
        try {
          this.fragmentQueue.wait(this.FRAGMENT_TIMEOUT);
        } catch (InterruptedException interruptedException) {
          bool = true;
        } 
        if (!bool && this.fragmentQueue.size() == 0)
          throw this.wrapper.bufferReadManagerTimeout(); 
        if (this.receivedCancel)
          throw new RequestCanceledException(this.cancelReqId); 
      } 
      byteBufferWithInfo = this.fragmentQueue.dequeue();
      byteBufferWithInfo.fragmented = true;
      if (this.debug) {
        int i = System.identityHashCode(byteBufferWithInfo.byteBuffer);
        StringBuffer stringBuffer = new StringBuffer(80);
        stringBuffer.append("underflow() - dequeued ByteBuffer id (");
        stringBuffer.append(i).append(") from fragment queue.");
        String str = stringBuffer.toString();
        dprint(str);
      } 
      if (!this.markEngaged && paramByteBufferWithInfo != null && paramByteBufferWithInfo.byteBuffer != null) {
        ByteBufferPool byteBufferPool = getByteBufferPool();
        if (this.debug) {
          int i = System.identityHashCode(paramByteBufferWithInfo.byteBuffer);
          StringBuffer stringBuffer = new StringBuffer(80);
          stringBuffer.append("underflow() - releasing ByteBuffer id (");
          stringBuffer.append(i).append(") to ByteBufferPool.");
          String str = stringBuffer.toString();
          dprint(str);
        } 
        byteBufferPool.releaseByteBuffer(paramByteBufferWithInfo.byteBuffer);
        paramByteBufferWithInfo.byteBuffer = null;
        paramByteBufferWithInfo = null;
      } 
    } 
    return byteBufferWithInfo;
  }
  
  public void init(Message paramMessage) {
    if (paramMessage != null)
      this.endOfStream = !paramMessage.moreFragmentsToFollow(); 
  }
  
  public void close(ByteBufferWithInfo paramByteBufferWithInfo) {
    int i = 0;
    if (this.fragmentQueue != null) {
      synchronized (this.fragmentQueue) {
        if (paramByteBufferWithInfo != null)
          i = System.identityHashCode(paramByteBufferWithInfo.byteBuffer); 
        ByteBufferWithInfo byteBufferWithInfo = null;
        ByteBufferPool byteBufferPool = getByteBufferPool();
        while (this.fragmentQueue.size() != 0) {
          byteBufferWithInfo = this.fragmentQueue.dequeue();
          if (byteBufferWithInfo != null && byteBufferWithInfo.byteBuffer != null) {
            int j = System.identityHashCode(byteBufferWithInfo.byteBuffer);
            if (i != j && this.debug) {
              StringBuffer stringBuffer = new StringBuffer(80);
              stringBuffer.append("close() - fragmentQueue is ").append("releasing ByteBuffer id (").append(j).append(") to ").append("ByteBufferPool.");
              String str = stringBuffer.toString();
              dprint(str);
            } 
            byteBufferPool.releaseByteBuffer(byteBufferWithInfo.byteBuffer);
          } 
        } 
      } 
      this.fragmentQueue = null;
    } 
    if (this.fragmentStack != null && this.fragmentStack.size() != 0) {
      if (paramByteBufferWithInfo != null)
        i = System.identityHashCode(paramByteBufferWithInfo.byteBuffer); 
      ByteBufferWithInfo byteBufferWithInfo = null;
      ByteBufferPool byteBufferPool = getByteBufferPool();
      ListIterator listIterator = this.fragmentStack.listIterator();
      while (listIterator.hasNext()) {
        byteBufferWithInfo = (ByteBufferWithInfo)listIterator.next();
        if (byteBufferWithInfo != null && byteBufferWithInfo.byteBuffer != null) {
          int j = System.identityHashCode(byteBufferWithInfo.byteBuffer);
          if (i != j) {
            if (this.debug) {
              StringBuffer stringBuffer = new StringBuffer(80);
              stringBuffer.append("close() - fragmentStack - releasing ").append("ByteBuffer id (" + j + ") to ").append("ByteBufferPool.");
              String str = stringBuffer.toString();
              dprint(str);
            } 
            byteBufferPool.releaseByteBuffer(byteBufferWithInfo.byteBuffer);
          } 
        } 
      } 
      this.fragmentStack = null;
    } 
  }
  
  protected ByteBufferPool getByteBufferPool() { return this.orb.getByteBufferPool(); }
  
  private void dprint(String paramString) { ORBUtility.dprint("BufferManagerReadStream", paramString); }
  
  public void mark(RestorableInputStream paramRestorableInputStream) {
    this.inputStream = paramRestorableInputStream;
    this.markEngaged = true;
    this.streamMemento = paramRestorableInputStream.createStreamMemento();
    if (this.fragmentStack != null)
      this.fragmentStack.clear(); 
  }
  
  public void fragmentationOccured(ByteBufferWithInfo paramByteBufferWithInfo) {
    if (!this.markEngaged)
      return; 
    if (this.fragmentStack == null)
      this.fragmentStack = new LinkedList(); 
    this.fragmentStack.addFirst(new ByteBufferWithInfo(paramByteBufferWithInfo));
  }
  
  public void reset() {
    if (!this.markEngaged)
      return; 
    this.markEngaged = false;
    if (this.fragmentStack != null && this.fragmentStack.size() != 0) {
      ListIterator listIterator = this.fragmentStack.listIterator();
      synchronized (this.fragmentQueue) {
        while (listIterator.hasNext())
          this.fragmentQueue.push((ByteBufferWithInfo)listIterator.next()); 
      } 
      this.fragmentStack.clear();
    } 
    this.inputStream.restoreInternalState(this.streamMemento);
  }
  
  public MarkAndResetHandler getMarkAndResetHandler() { return this; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\BufferManagerReadStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */