package com.sun.corba.se.impl.transport;

import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.orb.ORB;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ByteBufferPoolImpl implements ByteBufferPool {
  private ORB itsOrb;
  
  private int itsByteBufferSize;
  
  private ArrayList itsPool;
  
  private int itsObjectCounter = 0;
  
  private boolean debug;
  
  public ByteBufferPoolImpl(ORB paramORB) {
    this.itsByteBufferSize = paramORB.getORBData().getGIOPFragmentSize();
    this.itsPool = new ArrayList();
    this.itsOrb = paramORB;
    this.debug = paramORB.transportDebugFlag;
  }
  
  public ByteBuffer getByteBuffer(int paramInt) {
    ByteBuffer byteBuffer = null;
    if (paramInt <= this.itsByteBufferSize && !this.itsOrb.getORBData().disableDirectByteBufferUse()) {
      int i;
      synchronized (this.itsPool) {
        i = this.itsPool.size();
        if (i > 0) {
          byteBuffer = (ByteBuffer)this.itsPool.remove(i - 1);
          byteBuffer.clear();
        } 
      } 
      if (i <= 0)
        byteBuffer = ByteBuffer.allocateDirect(this.itsByteBufferSize); 
      this.itsObjectCounter++;
    } else {
      byteBuffer = ByteBuffer.allocate(paramInt);
    } 
    return byteBuffer;
  }
  
  public void releaseByteBuffer(ByteBuffer paramByteBuffer) {
    if (paramByteBuffer.isDirect()) {
      synchronized (this.itsPool) {
        boolean bool = false;
        int i = 0;
        if (this.debug)
          for (byte b = 0; b < this.itsPool.size() && !bool; b++) {
            ByteBuffer byteBuffer = (ByteBuffer)this.itsPool.get(b);
            if (paramByteBuffer == byteBuffer) {
              bool = true;
              i = System.identityHashCode(paramByteBuffer);
            } 
          }  
        if (!bool || !this.debug) {
          this.itsPool.add(paramByteBuffer);
        } else {
          String str = Thread.currentThread().getName();
          Throwable throwable = new Throwable(str + ": Duplicate ByteBuffer reference (" + i + ")");
          throwable.printStackTrace(System.out);
        } 
      } 
      this.itsObjectCounter--;
    } else {
      paramByteBuffer = null;
    } 
  }
  
  public int activeCount() { return this.itsObjectCounter; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\ByteBufferPoolImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */