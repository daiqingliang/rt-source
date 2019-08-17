package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

final class WriterToUTF8Buffered extends Writer implements WriterChain {
  private static final int BYTES_MAX = 16384;
  
  private static final int CHARS_MAX = 5461;
  
  private final OutputStream m_os;
  
  private final byte[] m_outputBytes;
  
  private final char[] m_inputChars;
  
  private int count;
  
  public WriterToUTF8Buffered(OutputStream paramOutputStream) throws UnsupportedEncodingException {
    this.m_os = paramOutputStream;
    this.m_outputBytes = new byte[16387];
    this.m_inputChars = new char[5463];
    this.count = 0;
  }
  
  public void write(int paramInt) throws IOException {
    if (this.count >= 16384)
      flushBuffer(); 
    if (paramInt < 128) {
      this.m_outputBytes[this.count++] = (byte)paramInt;
    } else if (paramInt < 2048) {
      this.m_outputBytes[this.count++] = (byte)(192 + (paramInt >> 6));
      this.m_outputBytes[this.count++] = (byte)(128 + (paramInt & 0x3F));
    } else if (paramInt < 65536) {
      this.m_outputBytes[this.count++] = (byte)(224 + (paramInt >> 12));
      this.m_outputBytes[this.count++] = (byte)(128 + (paramInt >> 6 & 0x3F));
      this.m_outputBytes[this.count++] = (byte)(128 + (paramInt & 0x3F));
    } else {
      this.m_outputBytes[this.count++] = (byte)(240 + (paramInt >> 18));
      this.m_outputBytes[this.count++] = (byte)(128 + (paramInt >> 12 & 0x3F));
      this.m_outputBytes[this.count++] = (byte)(128 + (paramInt >> 6 & 0x3F));
      this.m_outputBytes[this.count++] = (byte)(128 + (paramInt & 0x3F));
    } 
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    int i = 3 * paramInt2;
    if (i >= 16384 - this.count) {
      flushBuffer();
      if (i > 16384) {
        int i1;
        int n = paramInt2 / 5461;
        if (paramInt2 % 5461 > 0) {
          i1 = n + 1;
        } else {
          i1 = n;
        } 
        int i2 = paramInt1;
        for (byte b = 1; b <= i1; b++) {
          int i3 = i2;
          i2 = paramInt1 + (int)(paramInt2 * b / i1);
          char c1 = paramArrayOfChar[i2 - 1];
          char c2 = paramArrayOfChar[i2 - 1];
          if (c1 >= '?' && c1 <= '?')
            if (i2 < paramInt1 + paramInt2) {
              i2++;
            } else {
              i2--;
            }  
          int i4 = i2 - i3;
          write(paramArrayOfChar, i3, i4);
        } 
        return;
      } 
    } 
    int j = paramInt2 + paramInt1;
    byte[] arrayOfByte = this.m_outputBytes;
    int k = this.count;
    int m;
    char c;
    for (m = paramInt1; m < j && (c = paramArrayOfChar[m]) < ''; m++)
      arrayOfByte[k++] = (byte)c; 
    while (m < j) {
      c = paramArrayOfChar[m];
      if (c < '') {
        arrayOfByte[k++] = (byte)c;
      } else if (c < 'ࠀ') {
        arrayOfByte[k++] = (byte)('À' + (c >> '\006'));
        arrayOfByte[k++] = (byte)('' + (c & 0x3F));
      } else if (c >= '?' && c <= '?') {
        char c1 = c;
        char c2 = paramArrayOfChar[++m];
        arrayOfByte[k++] = (byte)(0xF0 | c1 + '@' >> '\b' & 0xF0);
        arrayOfByte[k++] = (byte)(0x80 | c1 + '@' >> '\002' & 0x3F);
        arrayOfByte[k++] = (byte)(0x80 | (c2 >> '\006' & 0xF) + (c1 << '\004' & 0x30));
        arrayOfByte[k++] = (byte)(0x80 | c2 & 0x3F);
      } else {
        arrayOfByte[k++] = (byte)('à' + (c >> '\f'));
        arrayOfByte[k++] = (byte)('' + (c >> '\006' & 0x3F));
        arrayOfByte[k++] = (byte)('' + (c & 0x3F));
      } 
      m++;
    } 
    this.count = k;
  }
  
  public void write(String paramString) throws IOException {
    int i = paramString.length();
    int j = 3 * i;
    if (j >= 16384 - this.count) {
      flushBuffer();
      if (j > 16384) {
        int i1;
        boolean bool = false;
        int n = i / 5461;
        if (i % 5461 > 0) {
          i1 = n + 1;
        } else {
          i1 = n;
        } 
        int i2 = 0;
        for (byte b1 = 1; b1 <= i1; b1++) {
          int i3 = i2;
          i2 = 0 + (int)(i * b1 / i1);
          paramString.getChars(i3, i2, this.m_inputChars, 0);
          int i4 = i2 - i3;
          char c1 = this.m_inputChars[i4 - 1];
          if (c1 >= '?' && c1 <= '?') {
            i2--;
            i4--;
            if (b1 == i1);
          } 
          write(this.m_inputChars, 0, i4);
        } 
        return;
      } 
    } 
    paramString.getChars(0, i, this.m_inputChars, 0);
    char[] arrayOfChar = this.m_inputChars;
    int k = i;
    byte[] arrayOfByte = this.m_outputBytes;
    int m = this.count;
    byte b;
    char c;
    for (b = 0; b < k && (c = arrayOfChar[b]) < ''; b++)
      arrayOfByte[m++] = (byte)c; 
    while (b < k) {
      c = arrayOfChar[b];
      if (c < '') {
        arrayOfByte[m++] = (byte)c;
      } else if (c < 'ࠀ') {
        arrayOfByte[m++] = (byte)('À' + (c >> '\006'));
        arrayOfByte[m++] = (byte)('' + (c & 0x3F));
      } else if (c >= '?' && c <= '?') {
        char c1 = c;
        char c2 = arrayOfChar[++b];
        arrayOfByte[m++] = (byte)(0xF0 | c1 + '@' >> '\b' & 0xF0);
        arrayOfByte[m++] = (byte)(0x80 | c1 + '@' >> '\002' & 0x3F);
        arrayOfByte[m++] = (byte)(0x80 | (c2 >> '\006' & 0xF) + (c1 << '\004' & 0x30));
        arrayOfByte[m++] = (byte)(0x80 | c2 & 0x3F);
      } else {
        arrayOfByte[m++] = (byte)('à' + (c >> '\f'));
        arrayOfByte[m++] = (byte)('' + (c >> '\006' & 0x3F));
        arrayOfByte[m++] = (byte)('' + (c & 0x3F));
      } 
      b++;
    } 
    this.count = m;
  }
  
  public void flushBuffer() throws IOException {
    if (this.count > 0) {
      this.m_os.write(this.m_outputBytes, 0, this.count);
      this.count = 0;
    } 
  }
  
  public void flush() throws IOException {
    flushBuffer();
    this.m_os.flush();
  }
  
  public void close() throws IOException {
    flushBuffer();
    this.m_os.close();
  }
  
  public OutputStream getOutputStream() { return this.m_os; }
  
  public Writer getWriter() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\WriterToUTF8Buffered.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */