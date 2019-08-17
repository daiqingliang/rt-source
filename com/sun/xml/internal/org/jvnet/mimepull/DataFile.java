package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;

final class DataFile {
  private WeakDataFile weak;
  
  private long writePointer = 0L;
  
  DataFile(File paramFile) { this.weak = new WeakDataFile(this, paramFile); }
  
  void close() { this.weak.close(); }
  
  void read(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2) { this.weak.read(paramLong, paramArrayOfByte, paramInt1, paramInt2); }
  
  void renameTo(File paramFile) { this.weak.renameTo(paramFile); }
  
  long writeTo(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    long l = this.writePointer;
    this.writePointer = this.weak.writeTo(this.writePointer, paramArrayOfByte, paramInt1, paramInt2);
    return l;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\DataFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */