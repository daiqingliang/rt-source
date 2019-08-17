package com.sun.jndi.ldap;

import java.io.UnsupportedEncodingException;

public final class BerEncoder extends Ber {
  private int curSeqIndex;
  
  private int[] seqOffset;
  
  private static final int INITIAL_SEQUENCES = 16;
  
  private static final int DEFAULT_BUFSIZE = 1024;
  
  private static final int BUF_GROWTH_FACTOR = 8;
  
  public BerEncoder() { this(1024); }
  
  public BerEncoder(int paramInt) {
    this.buf = new byte[paramInt];
    this.bufsize = paramInt;
    this.offset = 0;
    this.seqOffset = new int[16];
    this.curSeqIndex = 0;
  }
  
  public void reset() {
    while (this.offset > 0)
      this.buf[--this.offset] = 0; 
    while (this.curSeqIndex > 0)
      this.seqOffset[--this.curSeqIndex] = 0; 
  }
  
  public int getDataLen() { return this.offset; }
  
  public byte[] getBuf() {
    if (this.curSeqIndex != 0)
      throw new IllegalStateException("BER encode error: Unbalanced SEQUENCEs."); 
    return this.buf;
  }
  
  public byte[] getTrimmedBuf() {
    int i = getDataLen();
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(getBuf(), 0, arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  public void beginSeq(int paramInt) {
    if (this.curSeqIndex >= this.seqOffset.length) {
      int[] arrayOfInt = new int[this.seqOffset.length * 2];
      for (byte b = 0; b < this.seqOffset.length; b++)
        arrayOfInt[b] = this.seqOffset[b]; 
      this.seqOffset = arrayOfInt;
    } 
    encodeByte(paramInt);
    this.seqOffset[this.curSeqIndex] = this.offset;
    ensureFreeBytes(3);
    this.offset += 3;
    this.curSeqIndex++;
  }
  
  public void endSeq() {
    this.curSeqIndex--;
    if (this.curSeqIndex < 0)
      throw new IllegalStateException("BER encode error: Unbalanced SEQUENCEs."); 
    int i = this.seqOffset[this.curSeqIndex] + 3;
    int j = this.offset - i;
    if (j <= 127) {
      shiftSeqData(i, j, -2);
      this.buf[this.seqOffset[this.curSeqIndex]] = (byte)j;
    } else if (j <= 255) {
      shiftSeqData(i, j, -1);
      this.buf[this.seqOffset[this.curSeqIndex]] = -127;
      this.buf[this.seqOffset[this.curSeqIndex] + 1] = (byte)j;
    } else if (j <= 65535) {
      this.buf[this.seqOffset[this.curSeqIndex]] = -126;
      this.buf[this.seqOffset[this.curSeqIndex] + 1] = (byte)(j >> 8);
      this.buf[this.seqOffset[this.curSeqIndex] + 2] = (byte)j;
    } else if (j <= 16777215) {
      shiftSeqData(i, j, 1);
      this.buf[this.seqOffset[this.curSeqIndex]] = -125;
      this.buf[this.seqOffset[this.curSeqIndex] + 1] = (byte)(j >> 16);
      this.buf[this.seqOffset[this.curSeqIndex] + 2] = (byte)(j >> 8);
      this.buf[this.seqOffset[this.curSeqIndex] + 3] = (byte)j;
    } else {
      throw new Ber.EncodeException("SEQUENCE too long");
    } 
  }
  
  private void shiftSeqData(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt3 > 0)
      ensureFreeBytes(paramInt3); 
    System.arraycopy(this.buf, paramInt1, this.buf, paramInt1 + paramInt3, paramInt2);
    this.offset += paramInt3;
  }
  
  public void encodeByte(int paramInt) {
    ensureFreeBytes(1);
    this.buf[this.offset++] = (byte)paramInt;
  }
  
  public void encodeInt(int paramInt) { encodeInt(paramInt, 2); }
  
  public void encodeInt(int paramInt1, int paramInt2) {
    int i = -8388608;
    byte b = 4;
    while (((paramInt1 & i) == 0 || (paramInt1 & i) == i) && b > 1) {
      b--;
      paramInt1 <<= 8;
    } 
    encodeInt(paramInt1, paramInt2, b);
  }
  
  private void encodeInt(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt3 > 4)
      throw new IllegalArgumentException("BER encode error: INTEGER too long."); 
    ensureFreeBytes(2 + paramInt3);
    this.buf[this.offset++] = (byte)paramInt2;
    this.buf[this.offset++] = (byte)paramInt3;
    int i = -16777216;
    while (paramInt3-- > 0) {
      this.buf[this.offset++] = (byte)((paramInt1 & i) >> 24);
      paramInt1 <<= 8;
    } 
  }
  
  public void encodeBoolean(boolean paramBoolean) { encodeBoolean(paramBoolean, 1); }
  
  public void encodeBoolean(boolean paramBoolean, int paramInt) {
    ensureFreeBytes(3);
    this.buf[this.offset++] = (byte)paramInt;
    this.buf[this.offset++] = 1;
    this.buf[this.offset++] = paramBoolean ? -1 : 0;
  }
  
  public void encodeString(String paramString, boolean paramBoolean) throws Ber.EncodeException { encodeString(paramString, 4, paramBoolean); }
  
  public void encodeString(String paramString, int paramInt, boolean paramBoolean) throws Ber.EncodeException {
    int i;
    encodeByte(paramInt);
    byte b = 0;
    byte[] arrayOfByte = null;
    if (paramString == null) {
      i = 0;
    } else if (paramBoolean) {
      try {
        arrayOfByte = paramString.getBytes("UTF8");
        i = arrayOfByte.length;
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        throw new Ber.EncodeException("UTF8 not available on platform");
      } 
    } else {
      try {
        arrayOfByte = paramString.getBytes("8859_1");
        i = arrayOfByte.length;
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        throw new Ber.EncodeException("8859_1 not available on platform");
      } 
    } 
    encodeLength(i);
    ensureFreeBytes(i);
    while (b < i)
      this.buf[this.offset++] = arrayOfByte[b++]; 
  }
  
  public void encodeOctetString(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3) throws Ber.EncodeException {
    encodeByte(paramInt1);
    encodeLength(paramInt3);
    if (paramInt3 > 0) {
      ensureFreeBytes(paramInt3);
      System.arraycopy(paramArrayOfByte, paramInt2, this.buf, this.offset, paramInt3);
      this.offset += paramInt3;
    } 
  }
  
  public void encodeOctetString(byte[] paramArrayOfByte, int paramInt) throws Ber.EncodeException { encodeOctetString(paramArrayOfByte, paramInt, 0, paramArrayOfByte.length); }
  
  private void encodeLength(int paramInt) {
    ensureFreeBytes(4);
    if (paramInt < 128) {
      this.buf[this.offset++] = (byte)paramInt;
    } else if (paramInt <= 255) {
      this.buf[this.offset++] = -127;
      this.buf[this.offset++] = (byte)paramInt;
    } else if (paramInt <= 65535) {
      this.buf[this.offset++] = -126;
      this.buf[this.offset++] = (byte)(paramInt >> 8);
      this.buf[this.offset++] = (byte)(paramInt & 0xFF);
    } else if (paramInt <= 16777215) {
      this.buf[this.offset++] = -125;
      this.buf[this.offset++] = (byte)(paramInt >> 16);
      this.buf[this.offset++] = (byte)(paramInt >> 8);
      this.buf[this.offset++] = (byte)(paramInt & 0xFF);
    } else {
      throw new Ber.EncodeException("string too long");
    } 
  }
  
  public void encodeStringArray(String[] paramArrayOfString, boolean paramBoolean) throws Ber.EncodeException {
    if (paramArrayOfString == null)
      return; 
    for (byte b = 0; b < paramArrayOfString.length; b++)
      encodeString(paramArrayOfString[b], paramBoolean); 
  }
  
  private void ensureFreeBytes(int paramInt) {
    if (this.bufsize - this.offset < paramInt) {
      int i = this.bufsize * 8;
      if (i - this.offset < paramInt)
        i += paramInt; 
      byte[] arrayOfByte = new byte[i];
      System.arraycopy(this.buf, 0, arrayOfByte, 0, this.offset);
      this.buf = arrayOfByte;
      this.bufsize = i;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\BerEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */