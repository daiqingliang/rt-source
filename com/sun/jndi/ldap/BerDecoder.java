package com.sun.jndi.ldap;

import java.io.UnsupportedEncodingException;

public final class BerDecoder extends Ber {
  private int origOffset;
  
  public BerDecoder(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    this.buf = paramArrayOfByte;
    this.bufsize = paramInt2;
    this.origOffset = paramInt1;
    reset();
  }
  
  public void reset() { this.offset = this.origOffset; }
  
  public int getParsePosition() { return this.offset; }
  
  public int parseLength() {
    int i = parseByte();
    if ((i & 0x80) == 128) {
      i &= 0x7F;
      if (i == 0)
        throw new Ber.DecodeException("Indefinite length not supported"); 
      if (i > 4)
        throw new Ber.DecodeException("encoding too long"); 
      if (this.bufsize - this.offset < i)
        throw new Ber.DecodeException("Insufficient data"); 
      byte b = 0;
      for (byte b1 = 0; b1 < i; b1++)
        b = (b << 8) + (this.buf[this.offset++] & 0xFF); 
      if (b < 0)
        throw new Ber.DecodeException("Invalid length bytes"); 
      return b;
    } 
    return i;
  }
  
  public int parseSeq(int[] paramArrayOfInt) throws Ber.DecodeException {
    int i = parseByte();
    int j = parseLength();
    if (paramArrayOfInt != null)
      paramArrayOfInt[0] = j; 
    return i;
  }
  
  void seek(int paramInt) throws Ber.DecodeException {
    if (this.offset + paramInt > this.bufsize || this.offset + paramInt < 0)
      throw new Ber.DecodeException("array index out of bounds"); 
    this.offset += paramInt;
  }
  
  public int parseByte() {
    if (this.bufsize - this.offset < 1)
      throw new Ber.DecodeException("Insufficient data"); 
    return this.buf[this.offset++] & 0xFF;
  }
  
  public int peekByte() {
    if (this.bufsize - this.offset < 1)
      throw new Ber.DecodeException("Insufficient data"); 
    return this.buf[this.offset] & 0xFF;
  }
  
  public boolean parseBoolean() throws Ber.DecodeException { return !(parseIntWithTag(1) == 0); }
  
  public int parseEnumeration() { return parseIntWithTag(10); }
  
  public int parseInt() { return parseIntWithTag(2); }
  
  private int parseIntWithTag(int paramInt) throws Ber.DecodeException {
    if (parseByte() != paramInt)
      throw new Ber.DecodeException("Encountered ASN.1 tag " + Integer.toString(this.buf[this.offset - 1] & 0xFF) + " (expected tag " + Integer.toString(paramInt) + ")"); 
    int i = parseLength();
    if (i > 4)
      throw new Ber.DecodeException("INTEGER too long"); 
    if (i > this.bufsize - this.offset)
      throw new Ber.DecodeException("Insufficient data"); 
    byte b = this.buf[this.offset++];
    byte b1 = 0;
    b1 = b & 0x7F;
    for (byte b2 = 1; b2 < i; b2++) {
      b1 <<= 8;
      b1 |= this.buf[this.offset++] & 0xFF;
    } 
    if ((b & 0x80) == 128)
      b1 = -b1; 
    return b1;
  }
  
  public String parseString(boolean paramBoolean) throws Ber.DecodeException { return parseStringWithTag(4, paramBoolean, null); }
  
  public String parseStringWithTag(int paramInt, boolean paramBoolean, int[] paramArrayOfInt) throws Ber.DecodeException {
    String str;
    int j = this.offset;
    int i;
    if ((i = parseByte()) != paramInt)
      throw new Ber.DecodeException("Encountered ASN.1 tag " + Integer.toString((byte)i) + " (expected tag " + paramInt + ")"); 
    int k = parseLength();
    if (k > this.bufsize - this.offset)
      throw new Ber.DecodeException("Insufficient data"); 
    if (k == 0) {
      str = "";
    } else {
      byte[] arrayOfByte = new byte[k];
      System.arraycopy(this.buf, this.offset, arrayOfByte, 0, k);
      if (paramBoolean) {
        try {
          str = new String(arrayOfByte, "UTF8");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
          throw new Ber.DecodeException("UTF8 not available on platform");
        } 
      } else {
        try {
          str = new String(arrayOfByte, "8859_1");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
          throw new Ber.DecodeException("8859_1 not available on platform");
        } 
      } 
      this.offset += k;
    } 
    if (paramArrayOfInt != null)
      paramArrayOfInt[0] = this.offset - j; 
    return str;
  }
  
  public byte[] parseOctetString(int paramInt, int[] paramArrayOfInt) throws Ber.DecodeException {
    int i = this.offset;
    int j;
    if ((j = parseByte()) != paramInt)
      throw new Ber.DecodeException("Encountered ASN.1 tag " + Integer.toString(j) + " (expected tag " + Integer.toString(paramInt) + ")"); 
    int k = parseLength();
    if (k > this.bufsize - this.offset)
      throw new Ber.DecodeException("Insufficient data"); 
    byte[] arrayOfByte = new byte[k];
    if (k > 0) {
      System.arraycopy(this.buf, this.offset, arrayOfByte, 0, k);
      this.offset += k;
    } 
    if (paramArrayOfInt != null)
      paramArrayOfInt[0] = this.offset - i; 
    return arrayOfByte;
  }
  
  public int bytesLeft() { return this.bufsize - this.offset; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\BerDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */