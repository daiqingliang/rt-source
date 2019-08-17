package com.sun.imageio.plugins.common;

import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.stream.ImageOutputStream;

public class LZWCompressor {
  int codeSize;
  
  int clearCode;
  
  int endOfInfo;
  
  int numBits;
  
  int limit;
  
  short prefix;
  
  BitFile bf;
  
  LZWStringTable lzss;
  
  boolean tiffFudge;
  
  public LZWCompressor(ImageOutputStream paramImageOutputStream, int paramInt, boolean paramBoolean) throws IOException {
    this.bf = new BitFile(paramImageOutputStream, !paramBoolean);
    this.codeSize = paramInt;
    this.tiffFudge = paramBoolean;
    this.clearCode = 1 << paramInt;
    this.endOfInfo = this.clearCode + 1;
    this.numBits = paramInt + 1;
    this.limit = (1 << this.numBits) - 1;
    if (this.tiffFudge)
      this.limit--; 
    this.prefix = -1;
    this.lzss = new LZWStringTable();
    this.lzss.clearTable(paramInt);
    this.bf.writeBits(this.clearCode, this.numBits);
  }
  
  public void compress(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    int j = paramInt1 + paramInt2;
    for (int i = paramInt1; i < j; i++) {
      byte b = paramArrayOfByte[i];
      short s;
      if ((s = this.lzss.findCharString(this.prefix, b)) != -1) {
        this.prefix = s;
      } else {
        this.bf.writeBits(this.prefix, this.numBits);
        if (this.lzss.addCharString(this.prefix, b) > this.limit) {
          if (this.numBits == 12) {
            this.bf.writeBits(this.clearCode, this.numBits);
            this.lzss.clearTable(this.codeSize);
            this.numBits = this.codeSize + 1;
          } else {
            this.numBits++;
          } 
          this.limit = (1 << this.numBits) - 1;
          if (this.tiffFudge)
            this.limit--; 
        } 
        this.prefix = (short)((short)b & 0xFF);
      } 
    } 
  }
  
  public void flush() throws IOException {
    if (this.prefix != -1)
      this.bf.writeBits(this.prefix, this.numBits); 
    this.bf.writeBits(this.endOfInfo, this.numBits);
    this.bf.flush();
  }
  
  public void dump(PrintStream paramPrintStream) { this.lzss.dump(paramPrintStream); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\common\LZWCompressor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */