package java.io;

import java.util.Arrays;

public class StreamTokenizer {
  private Reader reader = null;
  
  private InputStream input = null;
  
  private char[] buf = new char[20];
  
  private int peekc = Integer.MAX_VALUE;
  
  private static final int NEED_CHAR = 2147483647;
  
  private static final int SKIP_LF = 2147483646;
  
  private boolean pushedBack;
  
  private boolean forceLower;
  
  private int LINENO = 1;
  
  private boolean eolIsSignificantP = false;
  
  private boolean slashSlashCommentsP = false;
  
  private boolean slashStarCommentsP = false;
  
  private byte[] ctype = new byte[256];
  
  private static final byte CT_WHITESPACE = 1;
  
  private static final byte CT_DIGIT = 2;
  
  private static final byte CT_ALPHA = 4;
  
  private static final byte CT_QUOTE = 8;
  
  private static final byte CT_COMMENT = 16;
  
  public int ttype = -4;
  
  public static final int TT_EOF = -1;
  
  public static final int TT_EOL = 10;
  
  public static final int TT_NUMBER = -2;
  
  public static final int TT_WORD = -3;
  
  private static final int TT_NOTHING = -4;
  
  public String sval;
  
  public double nval;
  
  private StreamTokenizer() {
    wordChars(97, 122);
    wordChars(65, 90);
    wordChars(160, 255);
    whitespaceChars(0, 32);
    commentChar(47);
    quoteChar(34);
    quoteChar(39);
    parseNumbers();
  }
  
  @Deprecated
  public StreamTokenizer(InputStream paramInputStream) {
    this();
    if (paramInputStream == null)
      throw new NullPointerException(); 
    this.input = paramInputStream;
  }
  
  public StreamTokenizer(Reader paramReader) {
    this();
    if (paramReader == null)
      throw new NullPointerException(); 
    this.reader = paramReader;
  }
  
  public void resetSyntax() {
    int i = this.ctype.length;
    while (--i >= 0)
      this.ctype[i] = 0; 
  }
  
  public void wordChars(int paramInt1, int paramInt2) {
    if (paramInt1 < 0)
      paramInt1 = 0; 
    if (paramInt2 >= this.ctype.length)
      paramInt2 = this.ctype.length - 1; 
    while (paramInt1 <= paramInt2)
      this.ctype[paramInt1++] = (byte)(this.ctype[paramInt1++] | 0x4); 
  }
  
  public void whitespaceChars(int paramInt1, int paramInt2) {
    if (paramInt1 < 0)
      paramInt1 = 0; 
    if (paramInt2 >= this.ctype.length)
      paramInt2 = this.ctype.length - 1; 
    while (paramInt1 <= paramInt2)
      this.ctype[paramInt1++] = 1; 
  }
  
  public void ordinaryChars(int paramInt1, int paramInt2) {
    if (paramInt1 < 0)
      paramInt1 = 0; 
    if (paramInt2 >= this.ctype.length)
      paramInt2 = this.ctype.length - 1; 
    while (paramInt1 <= paramInt2)
      this.ctype[paramInt1++] = 0; 
  }
  
  public void ordinaryChar(int paramInt) {
    if (paramInt >= 0 && paramInt < this.ctype.length)
      this.ctype[paramInt] = 0; 
  }
  
  public void commentChar(int paramInt) {
    if (paramInt >= 0 && paramInt < this.ctype.length)
      this.ctype[paramInt] = 16; 
  }
  
  public void quoteChar(int paramInt) {
    if (paramInt >= 0 && paramInt < this.ctype.length)
      this.ctype[paramInt] = 8; 
  }
  
  public void parseNumbers() {
    for (byte b = 48; b <= 57; b++)
      this.ctype[b] = (byte)(this.ctype[b] | 0x2); 
    this.ctype[46] = (byte)(this.ctype[46] | 0x2);
    this.ctype[45] = (byte)(this.ctype[45] | 0x2);
  }
  
  public void eolIsSignificant(boolean paramBoolean) { this.eolIsSignificantP = paramBoolean; }
  
  public void slashStarComments(boolean paramBoolean) { this.slashStarCommentsP = paramBoolean; }
  
  public void slashSlashComments(boolean paramBoolean) { this.slashSlashCommentsP = paramBoolean; }
  
  public void lowerCaseMode(boolean paramBoolean) { this.forceLower = paramBoolean; }
  
  private int read() throws IOException {
    if (this.reader != null)
      return this.reader.read(); 
    if (this.input != null)
      return this.input.read(); 
    throw new IllegalStateException();
  }
  
  public int nextToken() throws IOException {
    if (this.pushedBack) {
      this.pushedBack = false;
      return this.ttype;
    } 
    byte[] arrayOfByte = this.ctype;
    this.sval = null;
    int i = this.peekc;
    if (i < 0)
      i = Integer.MAX_VALUE; 
    if (i == 2147483646) {
      i = read();
      if (i < 0)
        return this.ttype = -1; 
      if (i == 10)
        i = Integer.MAX_VALUE; 
    } 
    if (i == Integer.MAX_VALUE) {
      i = read();
      if (i < 0)
        return this.ttype = -1; 
    } 
    this.ttype = i;
    this.peekc = Integer.MAX_VALUE;
    byte b;
    for (b = (i < 256) ? arrayOfByte[i] : 4; (b & true) != 0; b = (i < 256) ? arrayOfByte[i] : 4) {
      if (i == 13) {
        this.LINENO++;
        if (this.eolIsSignificantP) {
          this.peekc = 2147483646;
          return this.ttype = 10;
        } 
        i = read();
        if (i == 10)
          i = read(); 
      } else {
        if (i == 10) {
          this.LINENO++;
          if (this.eolIsSignificantP)
            return this.ttype = 10; 
        } 
        i = read();
      } 
      if (i < 0)
        return this.ttype = -1; 
    } 
    if ((b & 0x2) != 0) {
      boolean bool = false;
      if (i == 45) {
        i = read();
        if (i != 46 && (i < 48 || i > 57)) {
          this.peekc = i;
          return this.ttype = 45;
        } 
        bool = true;
      } 
      double d = 0.0D;
      byte b1 = 0;
      byte b2 = 0;
      while (true) {
        if (i == 46 && !b2) {
          b2 = 1;
        } else if (48 <= i && i <= 57) {
          d = d * 10.0D + (i - 48);
          b1 += b2;
        } else {
          break;
        } 
        i = read();
      } 
      this.peekc = i;
      if (b1 != 0) {
        double d1 = 10.0D;
        while (--b1 > 0) {
          d1 *= 10.0D;
          b1--;
        } 
        d /= d1;
      } 
      this.nval = bool ? -d : d;
      return this.ttype = -2;
    } 
    if ((b & 0x4) != 0) {
      byte b1 = 0;
      do {
        if (b1 >= this.buf.length)
          this.buf = Arrays.copyOf(this.buf, this.buf.length * 2); 
        this.buf[b1++] = (char)i;
        i = read();
        b = (i < 0) ? 1 : ((i < 256) ? arrayOfByte[i] : 4);
      } while ((b & 0x6) != 0);
      this.peekc = i;
      this.sval = String.copyValueOf(this.buf, 0, b1);
      if (this.forceLower)
        this.sval = this.sval.toLowerCase(); 
      return this.ttype = -3;
    } 
    if ((b & 0x8) != 0) {
      this.ttype = i;
      byte b1 = 0;
      int j = read();
      while (j >= 0 && j != this.ttype && j != 10 && j != 13) {
        if (j == 92) {
          i = read();
          int k = i;
          if (i >= 48 && i <= 55) {
            i -= 48;
            int m = read();
            if (48 <= m && m <= 55) {
              i = (i << 3) + m - 48;
              m = read();
              if (48 <= m && m <= 55 && k <= 51) {
                i = (i << 3) + m - 48;
                j = read();
              } else {
                j = m;
              } 
            } else {
              j = m;
            } 
          } else {
            switch (i) {
              case 97:
                i = 7;
                break;
              case 98:
                i = 8;
                break;
              case 102:
                i = 12;
                break;
              case 110:
                i = 10;
                break;
              case 114:
                i = 13;
                break;
              case 116:
                i = 9;
                break;
              case 118:
                i = 11;
                break;
            } 
            j = read();
          } 
        } else {
          i = j;
          j = read();
        } 
        if (b1 >= this.buf.length)
          this.buf = Arrays.copyOf(this.buf, this.buf.length * 2); 
        this.buf[b1++] = (char)i;
      } 
      this.peekc = (j == this.ttype) ? Integer.MAX_VALUE : j;
      this.sval = String.copyValueOf(this.buf, 0, b1);
      return this.ttype;
    } 
    if (i == 47 && (this.slashSlashCommentsP || this.slashStarCommentsP)) {
      i = read();
      if (i == 42 && this.slashStarCommentsP) {
        int j;
        for (j = 0; (i = read()) != 47 || j != 42; j = i) {
          if (i == 13) {
            this.LINENO++;
            i = read();
            if (i == 10)
              i = read(); 
          } else if (i == 10) {
            this.LINENO++;
            i = read();
          } 
          if (i < 0)
            return this.ttype = -1; 
        } 
        return nextToken();
      } 
      if (i == 47 && this.slashSlashCommentsP) {
        while ((i = read()) != 10 && i != 13 && i >= 0);
        this.peekc = i;
        return nextToken();
      } 
      if ((arrayOfByte[47] & 0x10) != 0) {
        while ((i = read()) != 10 && i != 13 && i >= 0);
        this.peekc = i;
        return nextToken();
      } 
      this.peekc = i;
      return this.ttype = 47;
    } 
    if ((b & 0x10) != 0) {
      while ((i = read()) != 10 && i != 13 && i >= 0);
      this.peekc = i;
      return nextToken();
    } 
    return this.ttype = i;
  }
  
  public void pushBack() {
    if (this.ttype != -4)
      this.pushedBack = true; 
  }
  
  public int lineno() throws IOException { return this.LINENO; }
  
  public String toString() {
    String str;
    switch (this.ttype) {
      case -1:
        str = "EOF";
        return "Token[" + str + "], line " + this.LINENO;
      case 10:
        str = "EOL";
        return "Token[" + str + "], line " + this.LINENO;
      case -3:
        str = this.sval;
        return "Token[" + str + "], line " + this.LINENO;
      case -2:
        str = "n=" + this.nval;
        return "Token[" + str + "], line " + this.LINENO;
      case -4:
        str = "NOTHING";
        return "Token[" + str + "], line " + this.LINENO;
    } 
    if (this.ttype < 256 && (this.ctype[this.ttype] & 0x8) != 0) {
      str = this.sval;
    } else {
      char[] arrayOfChar = new char[3];
      arrayOfChar[2] = '\'';
      arrayOfChar[0] = '\'';
      arrayOfChar[1] = (char)this.ttype;
      str = new String(arrayOfChar);
    } 
    return "Token[" + str + "], line " + this.LINENO;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\StreamTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */