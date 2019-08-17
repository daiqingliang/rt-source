package com.sun.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;

final class Chunk {
  public Chunk(Data paramData) { this.data = paramData; }
  
  public Chunk createNext(DataHead paramDataHead, ByteBuffer paramByteBuffer) { return this.next = new Chunk(this.data.createNext(paramDataHead, paramByteBuffer)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\Chunk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */