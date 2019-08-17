package com.sun.xml.internal.ws.org.objectweb.asm;

public class ByteVector {
  byte[] data = new byte[64];
  
  int length;
  
  public ByteVector() {}
  
  public ByteVector(int paramInt) {}
  
  public ByteVector putByte(int paramInt) {
    int i = this.length;
    if (i + 1 > this.data.length)
      enlarge(1); 
    this.data[i++] = (byte)paramInt;
    this.length = i;
    return this;
  }
  
  ByteVector put11(int paramInt1, int paramInt2) {
    int i = this.length;
    if (i + 2 > this.data.length)
      enlarge(2); 
    byte[] arrayOfByte = this.data;
    arrayOfByte[i++] = (byte)paramInt1;
    arrayOfByte[i++] = (byte)paramInt2;
    this.length = i;
    return this;
  }
  
  public ByteVector putShort(int paramInt) {
    int i = this.length;
    if (i + 2 > this.data.length)
      enlarge(2); 
    byte[] arrayOfByte = this.data;
    arrayOfByte[i++] = (byte)(paramInt >>> 8);
    arrayOfByte[i++] = (byte)paramInt;
    this.length = i;
    return this;
  }
  
  ByteVector put12(int paramInt1, int paramInt2) {
    int i = this.length;
    if (i + 3 > this.data.length)
      enlarge(3); 
    byte[] arrayOfByte = this.data;
    arrayOfByte[i++] = (byte)paramInt1;
    arrayOfByte[i++] = (byte)(paramInt2 >>> 8);
    arrayOfByte[i++] = (byte)paramInt2;
    this.length = i;
    return this;
  }
  
  public ByteVector putInt(int paramInt) {
    int i = this.length;
    if (i + 4 > this.data.length)
      enlarge(4); 
    byte[] arrayOfByte = this.data;
    arrayOfByte[i++] = (byte)(paramInt >>> 24);
    arrayOfByte[i++] = (byte)(paramInt >>> 16);
    arrayOfByte[i++] = (byte)(paramInt >>> 8);
    arrayOfByte[i++] = (byte)paramInt;
    this.length = i;
    return this;
  }
  
  public ByteVector putLong(long paramLong) {
    int i = this.length;
    if (i + 8 > this.data.length)
      enlarge(8); 
    byte[] arrayOfByte = this.data;
    int j = (int)(paramLong >>> 32);
    arrayOfByte[i++] = (byte)(j >>> 24);
    arrayOfByte[i++] = (byte)(j >>> 16);
    arrayOfByte[i++] = (byte)(j >>> 8);
    arrayOfByte[i++] = (byte)j;
    j = (int)paramLong;
    arrayOfByte[i++] = (byte)(j >>> 24);
    arrayOfByte[i++] = (byte)(j >>> 16);
    arrayOfByte[i++] = (byte)(j >>> 8);
    arrayOfByte[i++] = (byte)j;
    this.length = i;
    return this;
  }
  
  public ByteVector putUTF8(String paramString) {
    int i = paramString.length();
    if (this.length + 2 + i > this.data.length)
      enlarge(2 + i); 
    int j = this.length;
    byte[] arrayOfByte = this.data;
    arrayOfByte[j++] = (byte)(i >>> 8);
    arrayOfByte[j++] = (byte)i;
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c >= '\001' && c <= '') {
        arrayOfByte[j++] = (byte)c;
      } else {
        int k = b;
        byte b1;
        for (b1 = b; b1 < i; b1++) {
          c = paramString.charAt(b1);
          if (c >= '\001' && c <= '') {
            k++;
          } else if (c > '߿') {
            k += 3;
          } else {
            k += 2;
          } 
        } 
        arrayOfByte[this.length] = (byte)(k >>> 8);
        arrayOfByte[this.length + 1] = (byte)k;
        if (this.length + 2 + k > arrayOfByte.length) {
          this.length = j;
          enlarge(2 + k);
          arrayOfByte = this.data;
        } 
        for (b1 = b; b1 < i; b1++) {
          c = paramString.charAt(b1);
          if (c >= '\001' && c <= '') {
            arrayOfByte[j++] = (byte)c;
          } else if (c > '߿') {
            arrayOfByte[j++] = (byte)(0xE0 | c >> '\f' & 0xF);
            arrayOfByte[j++] = (byte)(0x80 | c >> '\006' & 0x3F);
            arrayOfByte[j++] = (byte)(0x80 | c & 0x3F);
          } else {
            arrayOfByte[j++] = (byte)(0xC0 | c >> '\006' & 0x1F);
            arrayOfByte[j++] = (byte)(0x80 | c & 0x3F);
          } 
        } 
        break;
      } 
    } 
    this.length = j;
    return this;
  }
  
  public ByteVector putByteArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (this.length + paramInt2 > this.data.length)
      enlarge(paramInt2); 
    if (paramArrayOfByte != null)
      System.arraycopy(paramArrayOfByte, paramInt1, this.data, this.length, paramInt2); 
    this.length += paramInt2;
    return this;
  }
  
  private void enlarge(int paramInt) {
    int i = 2 * this.data.length;
    int j = this.length + paramInt;
    byte[] arrayOfByte = new byte[(i > j) ? i : j];
    System.arraycopy(this.data, 0, arrayOfByte, 0, this.length);
    this.data = arrayOfByte;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\ByteVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */