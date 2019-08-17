package com.sun.xml.internal.bind.v2.runtime.output;

import java.io.IOException;

public final class Encoded {
  public byte[] buf;
  
  public int len;
  
  private static final byte[][] entities = new byte[128][];
  
  private static final byte[][] attributeEntities = new byte[128][];
  
  public Encoded() {}
  
  public Encoded(String paramString) { set(paramString); }
  
  public void ensureSize(int paramInt) {
    if (this.buf == null || this.buf.length < paramInt)
      this.buf = new byte[paramInt]; 
  }
  
  public final void set(String paramString) {
    int i = paramString.length();
    ensureSize(i * 3 + 1);
    byte b1 = 0;
    for (byte b2 = 0; b2 < i; b2++) {
      char c = paramString.charAt(b2);
      if (c > '') {
        if (c > '߿') {
          if ('?' <= c && c <= '?') {
            char c1 = ((c & 0x3FF) << '\n' | paramString.charAt(++b2) & 0x3FF) + 65536;
            this.buf[b1++] = (byte)(0xF0 | c1 >> '\022');
            this.buf[b1++] = (byte)(0x80 | c1 >> '\f' & 0x3F);
            this.buf[b1++] = (byte)(0x80 | c1 >> '\006' & 0x3F);
            this.buf[b1++] = (byte)('' + (c1 & 0x3F));
          } else {
            this.buf[b1++] = (byte)('à' + (c >> '\f'));
            this.buf[b1++] = (byte)('' + (c >> '\006' & 0x3F));
            this.buf[b1++] = (byte)('' + (c & 0x3F));
          } 
        } else {
          this.buf[b1++] = (byte)('À' + (c >> '\006'));
          this.buf[b1++] = (byte)('' + (c & 0x3F));
        } 
      } else {
        this.buf[b1++] = (byte)c;
      } 
    } 
    this.len = b1;
  }
  
  public final void setEscape(String paramString, boolean paramBoolean) {
    int i = paramString.length();
    ensureSize(i * 6 + 1);
    int j = 0;
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      int k = j;
      if (c > '') {
        if (c > '߿') {
          if ('?' <= c && c <= '?') {
            char c1 = ((c & 0x3FF) << '\n' | paramString.charAt(++b) & 0x3FF) + 65536;
            this.buf[j++] = (byte)(0xF0 | c1 >> '\022');
            this.buf[j++] = (byte)(0x80 | c1 >> '\f' & 0x3F);
            this.buf[j++] = (byte)(0x80 | c1 >> '\006' & 0x3F);
            this.buf[j++] = (byte)('' + (c1 & 0x3F));
          } else {
            this.buf[k++] = (byte)('à' + (c >> '\f'));
            this.buf[k++] = (byte)('' + (c >> '\006' & 0x3F));
            this.buf[k++] = (byte)('' + (c & 0x3F));
          } 
        } else {
          this.buf[k++] = (byte)('À' + (c >> '\006'));
          this.buf[k++] = (byte)('' + (c & 0x3F));
        } 
      } else {
        byte[] arrayOfByte;
        if ((arrayOfByte = attributeEntities[c]) != null) {
          if (paramBoolean || entities[c] != null) {
            k = writeEntity(arrayOfByte, k);
          } else {
            this.buf[k++] = (byte)c;
          } 
        } else {
          this.buf[k++] = (byte)c;
        } 
        j = k;
      } 
    } 
    this.len = j;
  }
  
  private int writeEntity(byte[] paramArrayOfByte, int paramInt) {
    System.arraycopy(paramArrayOfByte, 0, this.buf, paramInt, paramArrayOfByte.length);
    return paramInt + paramArrayOfByte.length;
  }
  
  public final void write(UTF8XmlOutput paramUTF8XmlOutput) throws IOException { paramUTF8XmlOutput.write(this.buf, 0, this.len); }
  
  public void append(char paramChar) { this.buf[this.len++] = (byte)paramChar; }
  
  public void compact() {
    byte[] arrayOfByte = new byte[this.len];
    System.arraycopy(this.buf, 0, arrayOfByte, 0, this.len);
    this.buf = arrayOfByte;
  }
  
  private static void add(char paramChar, String paramString, boolean paramBoolean) {
    byte[] arrayOfByte = UTF8XmlOutput.toBytes(paramString);
    attributeEntities[paramChar] = arrayOfByte;
    if (!paramBoolean)
      entities[paramChar] = arrayOfByte; 
  }
  
  static  {
    add('&', "&amp;", false);
    add('<', "&lt;", false);
    add('>', "&gt;", false);
    add('"', "&quot;", true);
    add('\t', "&#x9;", true);
    add('\r', "&#xD;", false);
    add('\n', "&#xA;", true);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\Encoded.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */