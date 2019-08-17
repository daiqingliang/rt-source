package com.sun.imageio.plugins.common;

import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;

public class BitFile {
  ImageOutputStream output;
  
  byte[] buffer;
  
  int index;
  
  int bitsLeft;
  
  boolean blocks = false;
  
  public BitFile(ImageOutputStream paramImageOutputStream, boolean paramBoolean) {
    this.output = paramImageOutputStream;
    this.blocks = paramBoolean;
    this.buffer = new byte[256];
    this.index = 0;
    this.bitsLeft = 8;
  }
  
  public void flush() throws IOException {
    int i = this.index + ((this.bitsLeft == 8) ? 0 : 1);
    if (i > 0) {
      if (this.blocks)
        this.output.write(i); 
      this.output.write(this.buffer, 0, i);
      this.buffer[0] = 0;
      this.index = 0;
      this.bitsLeft = 8;
    } 
  }
  
  public void writeBits(int paramInt1, int paramInt2) throws IOException {
    int i = 0;
    char c = 'ÿ';
    do {
      if ((this.index == 254 && this.bitsLeft == 0) || this.index > 254) {
        if (this.blocks)
          this.output.write(c); 
        this.output.write(this.buffer, 0, c);
        this.buffer[0] = 0;
        this.index = 0;
        this.bitsLeft = 8;
      } 
      if (paramInt2 <= this.bitsLeft) {
        if (this.blocks) {
          this.buffer[this.index] = (byte)(this.buffer[this.index] | (paramInt1 & (1 << paramInt2) - 1) << 8 - this.bitsLeft);
          i += paramInt2;
          this.bitsLeft -= paramInt2;
          paramInt2 = 0;
        } else {
          this.buffer[this.index] = (byte)(this.buffer[this.index] | (paramInt1 & (1 << paramInt2) - 1) << this.bitsLeft - paramInt2);
          i += paramInt2;
          this.bitsLeft -= paramInt2;
          paramInt2 = 0;
        } 
      } else if (this.blocks) {
        this.buffer[this.index] = (byte)(this.buffer[this.index] | (paramInt1 & (1 << this.bitsLeft) - 1) << 8 - this.bitsLeft);
        i += this.bitsLeft;
        paramInt1 >>= this.bitsLeft;
        paramInt2 -= this.bitsLeft;
        this.buffer[++this.index] = 0;
        this.bitsLeft = 8;
      } else {
        int j = paramInt1 >>> paramInt2 - this.bitsLeft & (1 << this.bitsLeft) - 1;
        this.buffer[this.index] = (byte)(this.buffer[this.index] | j);
        paramInt2 -= this.bitsLeft;
        i += this.bitsLeft;
        this.buffer[++this.index] = 0;
        this.bitsLeft = 8;
      } 
    } while (paramInt2 != 0);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\common\BitFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */