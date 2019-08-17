package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.orb.ORB;
import java.nio.ByteBuffer;
import org.omg.CORBA.ORB;

public class ByteBufferWithInfo {
  private ORB orb;
  
  private boolean debug;
  
  private int index;
  
  public ByteBuffer byteBuffer;
  
  public int buflen;
  
  public int needed;
  
  public boolean fragmented;
  
  public ByteBufferWithInfo(ORB paramORB, ByteBuffer paramByteBuffer, int paramInt) {
    this.orb = (ORB)paramORB;
    this.debug = this.orb.transportDebugFlag;
    this.byteBuffer = paramByteBuffer;
    if (paramByteBuffer != null)
      this.buflen = paramByteBuffer.limit(); 
    position(paramInt);
    this.needed = 0;
    this.fragmented = false;
  }
  
  public ByteBufferWithInfo(ORB paramORB, ByteBuffer paramByteBuffer) { this(paramORB, paramByteBuffer, 0); }
  
  public ByteBufferWithInfo(ORB paramORB, BufferManagerWrite paramBufferManagerWrite) { this(paramORB, paramBufferManagerWrite, true); }
  
  public ByteBufferWithInfo(ORB paramORB, BufferManagerWrite paramBufferManagerWrite, boolean paramBoolean) {
    this.orb = (ORB)paramORB;
    this.debug = this.orb.transportDebugFlag;
    int i = paramBufferManagerWrite.getBufferSize();
    if (paramBoolean) {
      ByteBufferPool byteBufferPool = this.orb.getByteBufferPool();
      this.byteBuffer = byteBufferPool.getByteBuffer(i);
      if (this.debug) {
        int j = System.identityHashCode(this.byteBuffer);
        StringBuffer stringBuffer = new StringBuffer(80);
        stringBuffer.append("constructor (ORB, BufferManagerWrite) - got ").append("ByteBuffer id (").append(j).append(") from ByteBufferPool.");
        String str = stringBuffer.toString();
        dprint(str);
      } 
    } else {
      this.byteBuffer = ByteBuffer.allocate(i);
    } 
    position(0);
    this.buflen = i;
    this.byteBuffer.limit(this.buflen);
    this.needed = 0;
    this.fragmented = false;
  }
  
  public ByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo) {
    this.orb = paramByteBufferWithInfo.orb;
    this.debug = paramByteBufferWithInfo.debug;
    this.byteBuffer = paramByteBufferWithInfo.byteBuffer;
    this.buflen = paramByteBufferWithInfo.buflen;
    this.byteBuffer.limit(this.buflen);
    position(paramByteBufferWithInfo.position());
    this.needed = paramByteBufferWithInfo.needed;
    this.fragmented = paramByteBufferWithInfo.fragmented;
  }
  
  public int getSize() { return position(); }
  
  public int getLength() { return this.buflen; }
  
  public int position() { return this.index; }
  
  public void position(int paramInt) {
    this.byteBuffer.position(paramInt);
    this.index = paramInt;
  }
  
  public void setLength(int paramInt) {
    this.buflen = paramInt;
    this.byteBuffer.limit(this.buflen);
  }
  
  public void growBuffer(ORB paramORB) {
    int i;
    for (i = this.byteBuffer.limit() * 2; position() + this.needed >= i; i *= 2);
    ByteBufferPool byteBufferPool = paramORB.getByteBufferPool();
    ByteBuffer byteBuffer1 = byteBufferPool.getByteBuffer(i);
    if (this.debug) {
      int j = System.identityHashCode(byteBuffer1);
      StringBuffer stringBuffer = new StringBuffer(80);
      stringBuffer.append("growBuffer() - got ByteBuffer id (");
      stringBuffer.append(j).append(") from ByteBufferPool.");
      String str = stringBuffer.toString();
      dprint(str);
    } 
    this.byteBuffer.position(0);
    byteBuffer1.put(this.byteBuffer);
    if (this.debug) {
      int j = System.identityHashCode(this.byteBuffer);
      StringBuffer stringBuffer = new StringBuffer(80);
      stringBuffer.append("growBuffer() - releasing ByteBuffer id (");
      stringBuffer.append(j).append(") to ByteBufferPool.");
      String str = stringBuffer.toString();
      dprint(str);
    } 
    byteBufferPool.releaseByteBuffer(this.byteBuffer);
    this.byteBuffer = byteBuffer1;
    this.buflen = i;
    this.byteBuffer.limit(this.buflen);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("ByteBufferWithInfo:");
    stringBuffer.append(" buflen = " + this.buflen);
    stringBuffer.append(" byteBuffer.limit = " + this.byteBuffer.limit());
    stringBuffer.append(" index = " + this.index);
    stringBuffer.append(" position = " + position());
    stringBuffer.append(" needed = " + this.needed);
    stringBuffer.append(" byteBuffer = " + ((this.byteBuffer == null) ? "null" : "not null"));
    stringBuffer.append(" fragmented = " + this.fragmented);
    return stringBuffer.toString();
  }
  
  protected void dprint(String paramString) { ORBUtility.dprint("ByteBufferWithInfo", paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\ByteBufferWithInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */