package com.sun.org.apache.xml.internal.utils;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class FastStringBuffer {
  static final int DEBUG_FORCE_INIT_BITS = 0;
  
  static final boolean DEBUG_FORCE_FIXED_CHUNKSIZE = true;
  
  public static final int SUPPRESS_LEADING_WS = 1;
  
  public static final int SUPPRESS_TRAILING_WS = 2;
  
  public static final int SUPPRESS_BOTH = 3;
  
  private static final int CARRY_WS = 4;
  
  int m_chunkBits = 15;
  
  int m_maxChunkBits = 15;
  
  int m_rebundleBits = 2;
  
  int m_chunkSize;
  
  int m_chunkMask;
  
  char[][] m_array;
  
  int m_lastChunk = 0;
  
  int m_firstFree = 0;
  
  FastStringBuffer m_innerFSB = null;
  
  static final char[] SINGLE_SPACE = { ' ' };
  
  public FastStringBuffer(int paramInt1, int paramInt2, int paramInt3) {
    paramInt2 = paramInt1;
    this.m_array = new char[16][];
    if (paramInt1 > paramInt2)
      paramInt1 = paramInt2; 
    this.m_chunkBits = paramInt1;
    this.m_maxChunkBits = paramInt2;
    this.m_rebundleBits = paramInt3;
    this.m_chunkSize = 1 << paramInt1;
    this.m_chunkMask = this.m_chunkSize - 1;
    this.m_array[0] = new char[this.m_chunkSize];
  }
  
  public FastStringBuffer(int paramInt1, int paramInt2) { this(paramInt1, paramInt2, 2); }
  
  public FastStringBuffer(int paramInt) { this(paramInt, 15, 2); }
  
  public FastStringBuffer() { this(10, 15, 2); }
  
  public final int size() { return (this.m_lastChunk << this.m_chunkBits) + this.m_firstFree; }
  
  public final int length() { return (this.m_lastChunk << this.m_chunkBits) + this.m_firstFree; }
  
  public final void reset() {
    this.m_lastChunk = 0;
    this.m_firstFree = 0;
    FastStringBuffer fastStringBuffer;
    for (fastStringBuffer = this; fastStringBuffer.m_innerFSB != null; fastStringBuffer = fastStringBuffer.m_innerFSB);
    this.m_chunkBits = fastStringBuffer.m_chunkBits;
    this.m_chunkSize = fastStringBuffer.m_chunkSize;
    this.m_chunkMask = fastStringBuffer.m_chunkMask;
    this.m_innerFSB = null;
    this.m_array = new char[16][0];
    this.m_array[0] = new char[this.m_chunkSize];
  }
  
  public final void setLength(int paramInt) {
    this.m_lastChunk = paramInt >>> this.m_chunkBits;
    if (this.m_lastChunk == 0 && this.m_innerFSB != null) {
      this.m_innerFSB.setLength(paramInt, this);
    } else {
      this.m_firstFree = paramInt & this.m_chunkMask;
      if (this.m_firstFree == 0 && this.m_lastChunk > 0) {
        this.m_lastChunk--;
        this.m_firstFree = this.m_chunkSize;
      } 
    } 
  }
  
  private final void setLength(int paramInt, FastStringBuffer paramFastStringBuffer) {
    this.m_lastChunk = paramInt >>> this.m_chunkBits;
    if (this.m_lastChunk == 0 && this.m_innerFSB != null) {
      this.m_innerFSB.setLength(paramInt, paramFastStringBuffer);
    } else {
      paramFastStringBuffer.m_chunkBits = this.m_chunkBits;
      paramFastStringBuffer.m_maxChunkBits = this.m_maxChunkBits;
      paramFastStringBuffer.m_rebundleBits = this.m_rebundleBits;
      paramFastStringBuffer.m_chunkSize = this.m_chunkSize;
      paramFastStringBuffer.m_chunkMask = this.m_chunkMask;
      paramFastStringBuffer.m_array = this.m_array;
      paramFastStringBuffer.m_innerFSB = this.m_innerFSB;
      paramFastStringBuffer.m_lastChunk = this.m_lastChunk;
      paramFastStringBuffer.m_firstFree = paramInt & this.m_chunkMask;
    } 
  }
  
  public final String toString() {
    int i = (this.m_lastChunk << this.m_chunkBits) + this.m_firstFree;
    return getString(new StringBuffer(i), 0, 0, i).toString();
  }
  
  public final void append(char paramChar) {
    char[] arrayOfChar;
    boolean bool = (this.m_lastChunk + 1 == this.m_array.length) ? 1 : 0;
    if (this.m_firstFree < this.m_chunkSize) {
      arrayOfChar = this.m_array[this.m_lastChunk];
    } else {
      int i = this.m_array.length;
      if (this.m_lastChunk + 1 == i) {
        char[][] arrayOfChar1 = new char[i + 16][];
        System.arraycopy(this.m_array, 0, arrayOfChar1, 0, i);
        this.m_array = arrayOfChar1;
      } 
      arrayOfChar = this.m_array[++this.m_lastChunk];
      if (arrayOfChar == null) {
        if (this.m_lastChunk == 1 << this.m_rebundleBits && this.m_chunkBits < this.m_maxChunkBits)
          this.m_innerFSB = new FastStringBuffer(this); 
        arrayOfChar = this.m_array[this.m_lastChunk] = new char[this.m_chunkSize];
      } 
      this.m_firstFree = 0;
    } 
    arrayOfChar[this.m_firstFree++] = paramChar;
  }
  
  public final void append(String paramString) {
    if (paramString == null)
      return; 
    int i = paramString.length();
    if (0 == i)
      return; 
    int j = 0;
    char[] arrayOfChar = this.m_array[this.m_lastChunk];
    int k = this.m_chunkSize - this.m_firstFree;
    while (i > 0) {
      if (k > i)
        k = i; 
      paramString.getChars(j, j + k, this.m_array[this.m_lastChunk], this.m_firstFree);
      i -= k;
      j += k;
      if (i > 0) {
        int m = this.m_array.length;
        if (this.m_lastChunk + 1 == m) {
          char[][] arrayOfChar1 = new char[m + 16][];
          System.arraycopy(this.m_array, 0, arrayOfChar1, 0, m);
          this.m_array = arrayOfChar1;
        } 
        arrayOfChar = this.m_array[++this.m_lastChunk];
        if (arrayOfChar == null) {
          if (this.m_lastChunk == 1 << this.m_rebundleBits && this.m_chunkBits < this.m_maxChunkBits)
            this.m_innerFSB = new FastStringBuffer(this); 
          arrayOfChar = this.m_array[this.m_lastChunk] = new char[this.m_chunkSize];
        } 
        k = this.m_chunkSize;
        this.m_firstFree = 0;
      } 
    } 
    this.m_firstFree += k;
  }
  
  public final void append(StringBuffer paramStringBuffer) {
    if (paramStringBuffer == null)
      return; 
    int i = paramStringBuffer.length();
    if (0 == i)
      return; 
    int j = 0;
    char[] arrayOfChar = this.m_array[this.m_lastChunk];
    int k = this.m_chunkSize - this.m_firstFree;
    while (i > 0) {
      if (k > i)
        k = i; 
      paramStringBuffer.getChars(j, j + k, this.m_array[this.m_lastChunk], this.m_firstFree);
      i -= k;
      j += k;
      if (i > 0) {
        int m = this.m_array.length;
        if (this.m_lastChunk + 1 == m) {
          char[][] arrayOfChar1 = new char[m + 16][];
          System.arraycopy(this.m_array, 0, arrayOfChar1, 0, m);
          this.m_array = arrayOfChar1;
        } 
        arrayOfChar = this.m_array[++this.m_lastChunk];
        if (arrayOfChar == null) {
          if (this.m_lastChunk == 1 << this.m_rebundleBits && this.m_chunkBits < this.m_maxChunkBits)
            this.m_innerFSB = new FastStringBuffer(this); 
          arrayOfChar = this.m_array[this.m_lastChunk] = new char[this.m_chunkSize];
        } 
        k = this.m_chunkSize;
        this.m_firstFree = 0;
      } 
    } 
    this.m_firstFree += k;
  }
  
  public final void append(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    int i = paramInt2;
    if (0 == i)
      return; 
    int j = paramInt1;
    char[] arrayOfChar = this.m_array[this.m_lastChunk];
    int k = this.m_chunkSize - this.m_firstFree;
    while (i > 0) {
      if (k > i)
        k = i; 
      System.arraycopy(paramArrayOfChar, j, this.m_array[this.m_lastChunk], this.m_firstFree, k);
      i -= k;
      j += k;
      if (i > 0) {
        int m = this.m_array.length;
        if (this.m_lastChunk + 1 == m) {
          char[][] arrayOfChar1 = new char[m + 16][];
          System.arraycopy(this.m_array, 0, arrayOfChar1, 0, m);
          this.m_array = arrayOfChar1;
        } 
        arrayOfChar = this.m_array[++this.m_lastChunk];
        if (arrayOfChar == null) {
          if (this.m_lastChunk == 1 << this.m_rebundleBits && this.m_chunkBits < this.m_maxChunkBits)
            this.m_innerFSB = new FastStringBuffer(this); 
          arrayOfChar = this.m_array[this.m_lastChunk] = new char[this.m_chunkSize];
        } 
        k = this.m_chunkSize;
        this.m_firstFree = 0;
      } 
    } 
    this.m_firstFree += k;
  }
  
  public final void append(FastStringBuffer paramFastStringBuffer) {
    if (paramFastStringBuffer == null)
      return; 
    int i = paramFastStringBuffer.length();
    if (0 == i)
      return; 
    int j = 0;
    char[] arrayOfChar = this.m_array[this.m_lastChunk];
    int k = this.m_chunkSize - this.m_firstFree;
    while (i > 0) {
      if (k > i)
        k = i; 
      int m = j + paramFastStringBuffer.m_chunkSize - 1 >>> paramFastStringBuffer.m_chunkBits;
      int n = j & paramFastStringBuffer.m_chunkMask;
      int i1 = paramFastStringBuffer.m_chunkSize - n;
      if (i1 > k)
        i1 = k; 
      System.arraycopy(paramFastStringBuffer.m_array[m], n, this.m_array[this.m_lastChunk], this.m_firstFree, i1);
      if (i1 != k)
        System.arraycopy(paramFastStringBuffer.m_array[m + 1], 0, this.m_array[this.m_lastChunk], this.m_firstFree + i1, k - i1); 
      i -= k;
      j += k;
      if (i > 0) {
        int i2 = this.m_array.length;
        if (this.m_lastChunk + 1 == i2) {
          char[][] arrayOfChar1 = new char[i2 + 16][];
          System.arraycopy(this.m_array, 0, arrayOfChar1, 0, i2);
          this.m_array = arrayOfChar1;
        } 
        arrayOfChar = this.m_array[++this.m_lastChunk];
        if (arrayOfChar == null) {
          if (this.m_lastChunk == 1 << this.m_rebundleBits && this.m_chunkBits < this.m_maxChunkBits)
            this.m_innerFSB = new FastStringBuffer(this); 
          arrayOfChar = this.m_array[this.m_lastChunk] = new char[this.m_chunkSize];
        } 
        k = this.m_chunkSize;
        this.m_firstFree = 0;
      } 
    } 
    this.m_firstFree += k;
  }
  
  public boolean isWhitespace(int paramInt1, int paramInt2) {
    int i = paramInt1 >>> this.m_chunkBits;
    int j = paramInt1 & this.m_chunkMask;
    int k;
    for (k = this.m_chunkSize - j; paramInt2 > 0; k = this.m_chunkSize) {
      boolean bool;
      int m = (paramInt2 <= k) ? paramInt2 : k;
      if (i == 0 && this.m_innerFSB != null) {
        bool = this.m_innerFSB.isWhitespace(j, m);
      } else {
        bool = XMLCharacterRecognizer.isWhiteSpace(this.m_array[i], j, m);
      } 
      if (!bool)
        return false; 
      paramInt2 -= m;
      i++;
      j = 0;
    } 
    return true;
  }
  
  public String getString(int paramInt1, int paramInt2) {
    int i = paramInt1 & this.m_chunkMask;
    int j = paramInt1 >>> this.m_chunkBits;
    return (i + paramInt2 < this.m_chunkMask && this.m_innerFSB == null) ? getOneChunkString(j, i, paramInt2) : getString(new StringBuffer(paramInt2), j, i, paramInt2).toString();
  }
  
  protected String getOneChunkString(int paramInt1, int paramInt2, int paramInt3) { return new String(this.m_array[paramInt1], paramInt2, paramInt3); }
  
  StringBuffer getString(StringBuffer paramStringBuffer, int paramInt1, int paramInt2) { return getString(paramStringBuffer, paramInt1 >>> this.m_chunkBits, paramInt1 & this.m_chunkMask, paramInt2); }
  
  StringBuffer getString(StringBuffer paramStringBuffer, int paramInt1, int paramInt2, int paramInt3) {
    int i = (paramInt1 << this.m_chunkBits) + paramInt2 + paramInt3;
    int j = i >>> this.m_chunkBits;
    int k = i & this.m_chunkMask;
    for (int m = paramInt1; m < j; m++) {
      if (m == 0 && this.m_innerFSB != null) {
        this.m_innerFSB.getString(paramStringBuffer, paramInt2, this.m_chunkSize - paramInt2);
      } else {
        paramStringBuffer.append(this.m_array[m], paramInt2, this.m_chunkSize - paramInt2);
      } 
      paramInt2 = 0;
    } 
    if (j == 0 && this.m_innerFSB != null) {
      this.m_innerFSB.getString(paramStringBuffer, paramInt2, k - paramInt2);
    } else if (k > paramInt2) {
      paramStringBuffer.append(this.m_array[j], paramInt2, k - paramInt2);
    } 
    return paramStringBuffer;
  }
  
  public char charAt(int paramInt) {
    int i = paramInt >>> this.m_chunkBits;
    return (i == 0 && this.m_innerFSB != null) ? this.m_innerFSB.charAt(paramInt & this.m_chunkMask) : this.m_array[i][paramInt & this.m_chunkMask];
  }
  
  public void sendSAXcharacters(ContentHandler paramContentHandler, int paramInt1, int paramInt2) throws SAXException {
    int i = paramInt1 >>> this.m_chunkBits;
    int j = paramInt1 & this.m_chunkMask;
    if (j + paramInt2 < this.m_chunkMask && this.m_innerFSB == null) {
      paramContentHandler.characters(this.m_array[i], j, paramInt2);
      return;
    } 
    int k = paramInt1 + paramInt2;
    int m = k >>> this.m_chunkBits;
    int n = k & this.m_chunkMask;
    for (int i1 = i; i1 < m; i1++) {
      if (i1 == 0 && this.m_innerFSB != null) {
        this.m_innerFSB.sendSAXcharacters(paramContentHandler, j, this.m_chunkSize - j);
      } else {
        paramContentHandler.characters(this.m_array[i1], j, this.m_chunkSize - j);
      } 
      j = 0;
    } 
    if (m == 0 && this.m_innerFSB != null) {
      this.m_innerFSB.sendSAXcharacters(paramContentHandler, j, n - j);
    } else if (n > j) {
      paramContentHandler.characters(this.m_array[m], j, n - j);
    } 
  }
  
  public int sendNormalizedSAXcharacters(ContentHandler paramContentHandler, int paramInt1, int paramInt2) throws SAXException {
    int i = 1;
    int j = paramInt1 + paramInt2;
    int k = paramInt1 >>> this.m_chunkBits;
    int m = paramInt1 & this.m_chunkMask;
    int n = j >>> this.m_chunkBits;
    int i1 = j & this.m_chunkMask;
    for (int i2 = k; i2 < n; i2++) {
      if (i2 == 0 && this.m_innerFSB != null) {
        i = this.m_innerFSB.sendNormalizedSAXcharacters(paramContentHandler, m, this.m_chunkSize - m);
      } else {
        i = sendNormalizedSAXcharacters(this.m_array[i2], m, this.m_chunkSize - m, paramContentHandler, i);
      } 
      m = 0;
    } 
    if (n == 0 && this.m_innerFSB != null) {
      i = this.m_innerFSB.sendNormalizedSAXcharacters(paramContentHandler, m, i1 - m);
    } else if (i1 > m) {
      i = sendNormalizedSAXcharacters(this.m_array[n], m, i1 - m, paramContentHandler, i | 0x2);
    } 
    return i;
  }
  
  static int sendNormalizedSAXcharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2, ContentHandler paramContentHandler, int paramInt3) throws SAXException {
    boolean bool1 = ((paramInt3 & true) != 0) ? 1 : 0;
    boolean bool2 = ((paramInt3 & 0x4) != 0) ? 1 : 0;
    boolean bool3 = ((paramInt3 & 0x2) != 0) ? 1 : 0;
    int i = paramInt1;
    int j = paramInt1 + paramInt2;
    if (bool1) {
      while (i < j && XMLCharacterRecognizer.isWhiteSpace(paramArrayOfChar[i]))
        i++; 
      if (i == j)
        return paramInt3; 
    } 
    while (i < j) {
      int k = i;
      while (i < j && !XMLCharacterRecognizer.isWhiteSpace(paramArrayOfChar[i]))
        i++; 
      if (k != i) {
        if (bool2) {
          paramContentHandler.characters(SINGLE_SPACE, 0, 1);
          bool2 = false;
        } 
        paramContentHandler.characters(paramArrayOfChar, k, i - k);
      } 
      int m = i;
      while (i < j && XMLCharacterRecognizer.isWhiteSpace(paramArrayOfChar[i]))
        i++; 
      if (m != i)
        bool2 = true; 
    } 
    return (bool2 ? 4 : 0) | paramInt3 & 0x2;
  }
  
  public static void sendNormalizedSAXcharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2, ContentHandler paramContentHandler) throws SAXException { sendNormalizedSAXcharacters(paramArrayOfChar, paramInt1, paramInt2, paramContentHandler, 3); }
  
  public void sendSAXComment(LexicalHandler paramLexicalHandler, int paramInt1, int paramInt2) throws SAXException {
    String str = getString(paramInt1, paramInt2);
    paramLexicalHandler.comment(str.toCharArray(), 0, paramInt2);
  }
  
  private void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3) {}
  
  private FastStringBuffer(FastStringBuffer paramFastStringBuffer) {
    this.m_chunkBits = paramFastStringBuffer.m_chunkBits;
    this.m_maxChunkBits = paramFastStringBuffer.m_maxChunkBits;
    this.m_rebundleBits = paramFastStringBuffer.m_rebundleBits;
    this.m_chunkSize = paramFastStringBuffer.m_chunkSize;
    this.m_chunkMask = paramFastStringBuffer.m_chunkMask;
    this.m_array = paramFastStringBuffer.m_array;
    this.m_innerFSB = paramFastStringBuffer.m_innerFSB;
    paramFastStringBuffer.m_lastChunk--;
    this.m_firstFree = paramFastStringBuffer.m_chunkSize;
    paramFastStringBuffer.m_array = new char[16][];
    paramFastStringBuffer.m_innerFSB = this;
    paramFastStringBuffer.m_lastChunk = 1;
    paramFastStringBuffer.m_firstFree = 0;
    paramFastStringBuffer.m_chunkBits += this.m_rebundleBits;
    paramFastStringBuffer.m_chunkSize = 1 << paramFastStringBuffer.m_chunkBits;
    paramFastStringBuffer.m_chunkMask = paramFastStringBuffer.m_chunkSize - 1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\FastStringBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */