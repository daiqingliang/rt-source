package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;

class JPEGBuffer {
  private boolean debug = false;
  
  final int BUFFER_SIZE = 4096;
  
  byte[] buf = new byte[4096];
  
  int bufAvail = 0;
  
  int bufPtr = 0;
  
  ImageInputStream iis;
  
  JPEGBuffer(ImageInputStream paramImageInputStream) { this.iis = paramImageInputStream; }
  
  void loadBuf(int paramInt) throws IOException {
    if (this.debug) {
      System.out.print("loadbuf called with ");
      System.out.print("count " + paramInt + ", ");
      System.out.println("bufAvail " + this.bufAvail + ", ");
    } 
    if (paramInt != 0) {
      if (this.bufAvail >= paramInt)
        return; 
    } else if (this.bufAvail == 4096) {
      return;
    } 
    if (this.bufAvail > 0 && this.bufAvail < 4096)
      System.arraycopy(this.buf, this.bufPtr, this.buf, 0, this.bufAvail); 
    int i = this.iis.read(this.buf, this.bufAvail, this.buf.length - this.bufAvail);
    if (this.debug)
      System.out.println("iis.read returned " + i); 
    if (i != -1)
      this.bufAvail += i; 
    this.bufPtr = 0;
    int j = Math.min(4096, paramInt);
    if (this.bufAvail < j)
      throw new IIOException("Image Format Error"); 
  }
  
  void readData(byte[] paramArrayOfByte) throws IOException {
    int i = paramArrayOfByte.length;
    if (this.bufAvail >= i) {
      System.arraycopy(this.buf, this.bufPtr, paramArrayOfByte, 0, i);
      this.bufAvail -= i;
      this.bufPtr += i;
      return;
    } 
    int j = 0;
    if (this.bufAvail > 0) {
      System.arraycopy(this.buf, this.bufPtr, paramArrayOfByte, 0, this.bufAvail);
      j = this.bufAvail;
      i -= this.bufAvail;
      this.bufAvail = 0;
      this.bufPtr = 0;
    } 
    if (this.iis.read(paramArrayOfByte, j, i) != i)
      throw new IIOException("Image format Error"); 
  }
  
  void skipData(int paramInt) throws IOException {
    if (this.bufAvail >= paramInt) {
      this.bufAvail -= paramInt;
      this.bufPtr += paramInt;
      return;
    } 
    if (this.bufAvail > 0) {
      paramInt -= this.bufAvail;
      this.bufAvail = 0;
      this.bufPtr = 0;
    } 
    if (this.iis.skipBytes(paramInt) != paramInt)
      throw new IIOException("Image format Error"); 
  }
  
  void pushBack() throws IOException {
    this.iis.seek(this.iis.getStreamPosition() - this.bufAvail);
    this.bufAvail = 0;
    this.bufPtr = 0;
  }
  
  long getStreamPosition() throws IOException { return this.iis.getStreamPosition() - this.bufAvail; }
  
  boolean scanForFF(JPEGImageReader paramJPEGImageReader) throws IOException {
    boolean bool = false;
    boolean bool1 = false;
    while (!bool1) {
      while (this.bufAvail > 0) {
        if ((this.buf[this.bufPtr++] & 0xFF) == 255) {
          this.bufAvail--;
          bool1 = true;
          break;
        } 
        this.bufAvail--;
      } 
      loadBuf(0);
      if (bool1 == true)
        while (this.bufAvail > 0 && (this.buf[this.bufPtr] & 0xFF) == 255) {
          this.bufPtr++;
          this.bufAvail--;
        }  
      if (this.bufAvail == 0) {
        bool = true;
        this.buf[0] = -39;
        this.bufAvail = 1;
        this.bufPtr = 0;
        bool1 = true;
      } 
    } 
    return bool;
  }
  
  void print(int paramInt) throws IOException {
    System.out.print("buffer has ");
    System.out.print(this.bufAvail);
    System.out.println(" bytes available");
    if (this.bufAvail < paramInt)
      paramInt = this.bufAvail; 
    int i = this.bufPtr;
    while (paramInt > 0) {
      byte b = this.buf[i++] & 0xFF;
      System.out.print(" " + Integer.toHexString(b));
      paramInt--;
    } 
    System.out.println();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */