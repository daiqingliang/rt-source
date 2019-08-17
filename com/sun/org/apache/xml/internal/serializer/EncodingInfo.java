package com.sun.org.apache.xml.internal.serializer;

public final class EncodingInfo {
  final String name;
  
  final String javaName;
  
  private InEncoding m_encoding;
  
  public boolean isInEncoding(char paramChar) {
    if (this.m_encoding == null)
      this.m_encoding = new EncodingImpl(null); 
    return this.m_encoding.isInEncoding(paramChar);
  }
  
  public boolean isInEncoding(char paramChar1, char paramChar2) {
    if (this.m_encoding == null)
      this.m_encoding = new EncodingImpl(null); 
    return this.m_encoding.isInEncoding(paramChar1, paramChar2);
  }
  
  public EncodingInfo(String paramString1, String paramString2) {
    this.name = paramString1;
    this.javaName = paramString2;
  }
  
  private static boolean inEncoding(char paramChar, String paramString) {
    boolean bool;
    try {
      char[] arrayOfChar = new char[1];
      arrayOfChar[0] = paramChar;
      String str = new String(arrayOfChar);
      byte[] arrayOfByte = str.getBytes(paramString);
      bool = inEncoding(paramChar, arrayOfByte);
    } catch (Exception exception) {
      bool = false;
      if (paramString == null)
        bool = true; 
    } 
    return bool;
  }
  
  private static boolean inEncoding(char paramChar1, char paramChar2, String paramString) {
    boolean bool;
    try {
      char[] arrayOfChar = new char[2];
      arrayOfChar[0] = paramChar1;
      arrayOfChar[1] = paramChar2;
      String str = new String(arrayOfChar);
      byte[] arrayOfByte = str.getBytes(paramString);
      bool = inEncoding(paramChar1, arrayOfByte);
    } catch (Exception exception) {
      bool = false;
    } 
    return bool;
  }
  
  private static boolean inEncoding(char paramChar, byte[] paramArrayOfByte) {
    boolean bool;
    if (paramArrayOfByte == null || paramArrayOfByte.length == 0) {
      bool = false;
    } else if (paramArrayOfByte[0] == 0) {
      bool = false;
    } else if (paramArrayOfByte[0] == 63 && paramChar != '?') {
      bool = false;
    } else {
      bool = true;
    } 
    return bool;
  }
  
  private class EncodingImpl implements InEncoding {
    private final String m_encoding;
    
    private final int m_first;
    
    private final int m_explFirst;
    
    private final int m_explLast;
    
    private final int m_last;
    
    private EncodingInfo.InEncoding m_before;
    
    private EncodingInfo.InEncoding m_after;
    
    private static final int RANGE = 128;
    
    private final boolean[] m_alreadyKnown = new boolean[128];
    
    private final boolean[] m_isInEncoding = new boolean[128];
    
    public boolean isInEncoding(char param1Char) {
      boolean bool;
      int i = Encodings.toCodePoint(param1Char);
      if (i < this.m_explFirst) {
        if (this.m_before == null)
          this.m_before = new EncodingImpl(EncodingInfo.this, this.m_encoding, this.m_first, this.m_explFirst - 1, i); 
        bool = this.m_before.isInEncoding(param1Char);
      } else if (this.m_explLast < i) {
        if (this.m_after == null)
          this.m_after = new EncodingImpl(EncodingInfo.this, this.m_encoding, this.m_explLast + 1, this.m_last, i); 
        bool = this.m_after.isInEncoding(param1Char);
      } else {
        int j = i - this.m_explFirst;
        if (this.m_alreadyKnown[j]) {
          bool = this.m_isInEncoding[j];
        } else {
          bool = EncodingInfo.inEncoding(param1Char, this.m_encoding);
          this.m_alreadyKnown[j] = true;
          this.m_isInEncoding[j] = bool;
        } 
      } 
      return bool;
    }
    
    public boolean isInEncoding(char param1Char1, char param1Char2) {
      boolean bool;
      int i = Encodings.toCodePoint(param1Char1, param1Char2);
      if (i < this.m_explFirst) {
        if (this.m_before == null)
          this.m_before = new EncodingImpl(EncodingInfo.this, this.m_encoding, this.m_first, this.m_explFirst - 1, i); 
        bool = this.m_before.isInEncoding(param1Char1, param1Char2);
      } else if (this.m_explLast < i) {
        if (this.m_after == null)
          this.m_after = new EncodingImpl(EncodingInfo.this, this.m_encoding, this.m_explLast + 1, this.m_last, i); 
        bool = this.m_after.isInEncoding(param1Char1, param1Char2);
      } else {
        int j = i - this.m_explFirst;
        if (this.m_alreadyKnown[j]) {
          bool = this.m_isInEncoding[j];
        } else {
          bool = EncodingInfo.inEncoding(param1Char1, param1Char2, this.m_encoding);
          this.m_alreadyKnown[j] = true;
          this.m_isInEncoding[j] = bool;
        } 
      } 
      return bool;
    }
    
    private EncodingImpl(EncodingInfo this$0) { this(EncodingInfo.this.javaName, 0, 2147483647, 0); }
    
    private EncodingImpl(String param1String, int param1Int1, int param1Int2, int param1Int3) {
      this.m_first = param1Int1;
      this.m_last = param1Int2;
      this.m_explFirst = param1Int3 / 128 * 128;
      this.m_explLast = this.m_explFirst + 127;
      this.m_encoding = param1String;
      if (EncodingInfo.this.javaName != null) {
        if (0 <= this.m_explFirst && this.m_explFirst <= 127 && ("UTF8".equals(EncodingInfo.this.javaName) || "UTF-16".equals(EncodingInfo.this.javaName) || "ASCII".equals(EncodingInfo.this.javaName) || "US-ASCII".equals(EncodingInfo.this.javaName) || "Unicode".equals(EncodingInfo.this.javaName) || "UNICODE".equals(EncodingInfo.this.javaName) || EncodingInfo.this.javaName.startsWith("ISO8859")))
          for (int i = 1; i < 127; i++) {
            int j = i - this.m_explFirst;
            if (0 <= j && j < 128) {
              this.m_alreadyKnown[j] = true;
              this.m_isInEncoding[j] = true;
            } 
          }  
        if (EncodingInfo.this.javaName == null)
          for (byte b = 0; b < this.m_alreadyKnown.length; b++) {
            this.m_alreadyKnown[b] = true;
            this.m_isInEncoding[b] = true;
          }  
      } 
    }
  }
  
  private static interface InEncoding {
    boolean isInEncoding(char param1Char);
    
    boolean isInEncoding(char param1Char1, char param1Char2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\EncodingInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */