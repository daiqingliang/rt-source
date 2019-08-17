package com.sun.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;

final class FileData implements Data {
  private final DataFile file;
  
  private final long pointer;
  
  private final int length;
  
  FileData(DataFile paramDataFile, ByteBuffer paramByteBuffer) { this(paramDataFile, paramDataFile.writeTo(paramByteBuffer.array(), 0, paramByteBuffer.limit()), paramByteBuffer.limit()); }
  
  FileData(DataFile paramDataFile, long paramLong, int paramInt) {
    this.file = paramDataFile;
    this.pointer = paramLong;
    this.length = paramInt;
  }
  
  public byte[] read() {
    byte[] arrayOfByte = new byte[this.length];
    this.file.read(this.pointer, arrayOfByte, 0, this.length);
    return arrayOfByte;
  }
  
  public long writeTo(DataFile paramDataFile) { throw new IllegalStateException(); }
  
  public int size() { return this.length; }
  
  public Data createNext(DataHead paramDataHead, ByteBuffer paramByteBuffer) { return new FileData(this.file, paramByteBuffer); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\FileData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */