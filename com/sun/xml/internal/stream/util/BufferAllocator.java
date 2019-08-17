package com.sun.xml.internal.stream.util;

public class BufferAllocator {
  public static int SMALL_SIZE_LIMIT = 128;
  
  public static int MEDIUM_SIZE_LIMIT = 2048;
  
  public static int LARGE_SIZE_LIMIT = 8192;
  
  char[] smallCharBuffer;
  
  char[] mediumCharBuffer;
  
  char[] largeCharBuffer;
  
  byte[] smallByteBuffer;
  
  byte[] mediumByteBuffer;
  
  byte[] largeByteBuffer;
  
  public char[] getCharBuffer(int paramInt) {
    if (paramInt <= SMALL_SIZE_LIMIT) {
      char[] arrayOfChar = this.smallCharBuffer;
      this.smallCharBuffer = null;
      return arrayOfChar;
    } 
    if (paramInt <= MEDIUM_SIZE_LIMIT) {
      char[] arrayOfChar = this.mediumCharBuffer;
      this.mediumCharBuffer = null;
      return arrayOfChar;
    } 
    if (paramInt <= LARGE_SIZE_LIMIT) {
      char[] arrayOfChar = this.largeCharBuffer;
      this.largeCharBuffer = null;
      return arrayOfChar;
    } 
    return null;
  }
  
  public void returnCharBuffer(char[] paramArrayOfChar) {
    if (paramArrayOfChar == null)
      return; 
    if (paramArrayOfChar.length <= SMALL_SIZE_LIMIT) {
      this.smallCharBuffer = paramArrayOfChar;
    } else if (paramArrayOfChar.length <= MEDIUM_SIZE_LIMIT) {
      this.mediumCharBuffer = paramArrayOfChar;
    } else if (paramArrayOfChar.length <= LARGE_SIZE_LIMIT) {
      this.largeCharBuffer = paramArrayOfChar;
    } 
  }
  
  public byte[] getByteBuffer(int paramInt) {
    if (paramInt <= SMALL_SIZE_LIMIT) {
      byte[] arrayOfByte = this.smallByteBuffer;
      this.smallByteBuffer = null;
      return arrayOfByte;
    } 
    if (paramInt <= MEDIUM_SIZE_LIMIT) {
      byte[] arrayOfByte = this.mediumByteBuffer;
      this.mediumByteBuffer = null;
      return arrayOfByte;
    } 
    if (paramInt <= LARGE_SIZE_LIMIT) {
      byte[] arrayOfByte = this.largeByteBuffer;
      this.largeByteBuffer = null;
      return arrayOfByte;
    } 
    return null;
  }
  
  public void returnByteBuffer(byte[] paramArrayOfByte) {
    if (paramArrayOfByte == null)
      return; 
    if (paramArrayOfByte.length <= SMALL_SIZE_LIMIT) {
      this.smallByteBuffer = paramArrayOfByte;
    } else if (paramArrayOfByte.length <= MEDIUM_SIZE_LIMIT) {
      this.mediumByteBuffer = paramArrayOfByte;
    } else if (paramArrayOfByte.length <= LARGE_SIZE_LIMIT) {
      this.largeByteBuffer = paramArrayOfByte;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\strea\\util\BufferAllocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */