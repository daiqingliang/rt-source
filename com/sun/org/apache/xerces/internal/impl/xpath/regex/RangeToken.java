package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import java.io.Serializable;

final class RangeToken extends Token implements Serializable {
  private static final long serialVersionUID = 3257568399592010545L;
  
  int[] ranges;
  
  boolean sorted;
  
  boolean compacted;
  
  RangeToken icaseCache = null;
  
  int[] map = null;
  
  int nonMapIndex;
  
  private static final int MAPSIZE = 256;
  
  RangeToken(int paramInt) {
    super(paramInt);
    setSorted(false);
  }
  
  protected void addRange(int paramInt1, int paramInt2) {
    int j;
    int i;
    this.icaseCache = null;
    if (paramInt1 <= paramInt2) {
      i = paramInt1;
      j = paramInt2;
    } else {
      i = paramInt2;
      j = paramInt1;
    } 
    int k = 0;
    if (this.ranges == null) {
      this.ranges = new int[2];
      this.ranges[0] = i;
      this.ranges[1] = j;
      setSorted(true);
    } else {
      k = this.ranges.length;
      if (this.ranges[k - 1] + 1 == i) {
        this.ranges[k - 1] = j;
        return;
      } 
      int[] arrayOfInt = new int[k + 2];
      System.arraycopy(this.ranges, 0, arrayOfInt, 0, k);
      this.ranges = arrayOfInt;
      if (this.ranges[k - 1] >= i)
        setSorted(false); 
      this.ranges[k++] = i;
      this.ranges[k] = j;
      if (!this.sorted)
        sortRanges(); 
    } 
  }
  
  private final boolean isSorted() { return this.sorted; }
  
  private final void setSorted(boolean paramBoolean) {
    this.sorted = paramBoolean;
    if (!paramBoolean)
      this.compacted = false; 
  }
  
  private final boolean isCompacted() { return this.compacted; }
  
  private final void setCompacted() { this.compacted = true; }
  
  protected void sortRanges() {
    if (isSorted())
      return; 
    if (this.ranges == null)
      return; 
    for (int i = this.ranges.length - 4; i >= 0; i -= 2) {
      for (boolean bool = false; bool <= i; bool += true) {
        if (this.ranges[bool] > this.ranges[bool + 2] || (this.ranges[bool] == this.ranges[bool + 2] && this.ranges[bool + true] > this.ranges[bool + 3])) {
          int j = this.ranges[bool + 2];
          this.ranges[bool + 2] = this.ranges[bool];
          this.ranges[bool] = j;
          j = this.ranges[bool + 3];
          this.ranges[bool + 3] = this.ranges[bool + true];
          this.ranges[bool + true] = j;
        } 
      } 
    } 
    setSorted(true);
  }
  
  protected void compactRanges() {
    boolean bool = false;
    if (this.ranges == null || this.ranges.length <= 2)
      return; 
    if (isCompacted())
      return; 
    byte b1 = 0;
    byte b2 = 0;
    while (b2 < this.ranges.length) {
      if (b1 != b2) {
        this.ranges[b1] = this.ranges[b2++];
        this.ranges[b1 + true] = this.ranges[b2++];
      } else {
        b2 += 2;
      } 
      int i = this.ranges[b1 + true];
      while (b2 < this.ranges.length && i + 1 >= this.ranges[b2]) {
        if (i + 1 == this.ranges[b2]) {
          if (bool)
            System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[b1] + ", " + this.ranges[b1 + true] + "], [" + this.ranges[b2] + ", " + this.ranges[b2 + 1] + "] -> [" + this.ranges[b1] + ", " + this.ranges[b2 + 1] + "]"); 
          this.ranges[b1 + true] = this.ranges[b2 + 1];
          i = this.ranges[b1 + true];
          b2 += 2;
          continue;
        } 
        if (i >= this.ranges[b2 + 1]) {
          if (bool)
            System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[b1] + ", " + this.ranges[b1 + true] + "], [" + this.ranges[b2] + ", " + this.ranges[b2 + 1] + "] -> [" + this.ranges[b1] + ", " + this.ranges[b1 + true] + "]"); 
          b2 += 2;
          continue;
        } 
        if (i < this.ranges[b2 + 1]) {
          if (bool)
            System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[b1] + ", " + this.ranges[b1 + true] + "], [" + this.ranges[b2] + ", " + this.ranges[b2 + 1] + "] -> [" + this.ranges[b1] + ", " + this.ranges[b2 + 1] + "]"); 
          this.ranges[b1 + true] = this.ranges[b2 + 1];
          i = this.ranges[b1 + true];
          b2 += 2;
          continue;
        } 
        throw new RuntimeException("Token#compactRanges(): Internel Error: [" + this.ranges[b1] + "," + this.ranges[b1 + true] + "] [" + this.ranges[b2] + "," + this.ranges[b2 + 1] + "]");
      } 
      b1 += true;
    } 
    if (b1 != this.ranges.length) {
      int[] arrayOfInt = new int[b1];
      System.arraycopy(this.ranges, 0, arrayOfInt, 0, b1);
      this.ranges = arrayOfInt;
    } 
    setCompacted();
  }
  
  protected void mergeRanges(Token paramToken) {
    RangeToken rangeToken = (RangeToken)paramToken;
    sortRanges();
    rangeToken.sortRanges();
    if (rangeToken.ranges == null)
      return; 
    this.icaseCache = null;
    setSorted(true);
    if (this.ranges == null) {
      this.ranges = new int[rangeToken.ranges.length];
      System.arraycopy(rangeToken.ranges, 0, this.ranges, 0, rangeToken.ranges.length);
      return;
    } 
    int[] arrayOfInt = new int[this.ranges.length + rangeToken.ranges.length];
    byte b1 = 0;
    byte b2 = 0;
    byte b3 = 0;
    while (b1 < this.ranges.length || b2 < rangeToken.ranges.length) {
      if (b1 >= this.ranges.length) {
        arrayOfInt[b3++] = rangeToken.ranges[b2++];
        arrayOfInt[b3++] = rangeToken.ranges[b2++];
        continue;
      } 
      if (b2 >= rangeToken.ranges.length) {
        arrayOfInt[b3++] = this.ranges[b1++];
        arrayOfInt[b3++] = this.ranges[b1++];
        continue;
      } 
      if (rangeToken.ranges[b2] < this.ranges[b1] || (rangeToken.ranges[b2] == this.ranges[b1] && rangeToken.ranges[b2 + 1] < this.ranges[b1 + 1])) {
        arrayOfInt[b3++] = rangeToken.ranges[b2++];
        arrayOfInt[b3++] = rangeToken.ranges[b2++];
        continue;
      } 
      arrayOfInt[b3++] = this.ranges[b1++];
      arrayOfInt[b3++] = this.ranges[b1++];
    } 
    this.ranges = arrayOfInt;
  }
  
  protected void subtractRanges(Token paramToken) {
    if (paramToken.type == 5) {
      intersectRanges(paramToken);
      return;
    } 
    RangeToken rangeToken = (RangeToken)paramToken;
    if (rangeToken.ranges == null || this.ranges == null)
      return; 
    this.icaseCache = null;
    sortRanges();
    compactRanges();
    rangeToken.sortRanges();
    rangeToken.compactRanges();
    int[] arrayOfInt = new int[this.ranges.length + rangeToken.ranges.length];
    byte b1 = 0;
    byte b2 = 0;
    boolean bool = false;
    while (b2 < this.ranges.length && bool < rangeToken.ranges.length) {
      int i = this.ranges[b2];
      int j = this.ranges[b2 + true];
      int k = rangeToken.ranges[bool];
      int m = rangeToken.ranges[bool + true];
      if (j < k) {
        arrayOfInt[b1++] = this.ranges[b2++];
        arrayOfInt[b1++] = this.ranges[b2++];
        continue;
      } 
      if (j >= k && i <= m) {
        if (k <= i && j <= m) {
          b2 += 2;
          continue;
        } 
        if (k <= i) {
          this.ranges[b2] = m + 1;
          bool += true;
          continue;
        } 
        if (j <= m) {
          arrayOfInt[b1++] = i;
          arrayOfInt[b1++] = k - 1;
          b2 += 2;
          continue;
        } 
        arrayOfInt[b1++] = i;
        arrayOfInt[b1++] = k - 1;
        this.ranges[b2] = m + 1;
        bool += true;
        continue;
      } 
      if (m < i) {
        bool += true;
        continue;
      } 
      throw new RuntimeException("Token#subtractRanges(): Internal Error: [" + this.ranges[b2] + "," + this.ranges[b2 + 1] + "] - [" + rangeToken.ranges[bool] + "," + rangeToken.ranges[bool + true] + "]");
    } 
    while (b2 < this.ranges.length) {
      arrayOfInt[b1++] = this.ranges[b2++];
      arrayOfInt[b1++] = this.ranges[b2++];
    } 
    this.ranges = new int[b1];
    System.arraycopy(arrayOfInt, 0, this.ranges, 0, b1);
  }
  
  protected void intersectRanges(Token paramToken) {
    RangeToken rangeToken = (RangeToken)paramToken;
    if (rangeToken.ranges == null || this.ranges == null)
      return; 
    this.icaseCache = null;
    sortRanges();
    compactRanges();
    rangeToken.sortRanges();
    rangeToken.compactRanges();
    int[] arrayOfInt = new int[this.ranges.length + rangeToken.ranges.length];
    byte b1 = 0;
    byte b2 = 0;
    boolean bool = false;
    while (b2 < this.ranges.length && bool < rangeToken.ranges.length) {
      int i = this.ranges[b2];
      int j = this.ranges[b2 + true];
      int k = rangeToken.ranges[bool];
      int m = rangeToken.ranges[bool + true];
      if (j < k) {
        b2 += true;
        continue;
      } 
      if (j >= k && i <= m) {
        if (k <= k && j <= m) {
          arrayOfInt[b1++] = i;
          arrayOfInt[b1++] = j;
          b2 += true;
          continue;
        } 
        if (k <= i) {
          arrayOfInt[b1++] = i;
          arrayOfInt[b1++] = m;
          this.ranges[b2] = m + 1;
          bool += true;
          continue;
        } 
        if (j <= m) {
          arrayOfInt[b1++] = k;
          arrayOfInt[b1++] = j;
          b2 += true;
          continue;
        } 
        arrayOfInt[b1++] = k;
        arrayOfInt[b1++] = m;
        this.ranges[b2] = m + 1;
        continue;
      } 
      if (m < i) {
        bool += true;
        continue;
      } 
      throw new RuntimeException("Token#intersectRanges(): Internal Error: [" + this.ranges[b2] + "," + this.ranges[b2 + true] + "] & [" + rangeToken.ranges[bool] + "," + rangeToken.ranges[bool + true] + "]");
    } 
    while (b2 < this.ranges.length) {
      arrayOfInt[b1++] = this.ranges[b2++];
      arrayOfInt[b1++] = this.ranges[b2++];
    } 
    this.ranges = new int[b1];
    System.arraycopy(arrayOfInt, 0, this.ranges, 0, b1);
  }
  
  static Token complementRanges(Token paramToken) {
    if (paramToken.type != 4 && paramToken.type != 5)
      throw new IllegalArgumentException("Token#complementRanges(): must be RANGE: " + paramToken.type); 
    RangeToken rangeToken1 = (RangeToken)paramToken;
    rangeToken1.sortRanges();
    rangeToken1.compactRanges();
    int i = rangeToken1.ranges.length + 2;
    if (rangeToken1.ranges[0] == 0)
      i -= 2; 
    int j = rangeToken1.ranges[rangeToken1.ranges.length - 1];
    if (j == 1114111)
      i -= 2; 
    RangeToken rangeToken2 = Token.createRange();
    rangeToken2.ranges = new int[i];
    byte b = 0;
    if (rangeToken1.ranges[0] > 0) {
      rangeToken2.ranges[b++] = 0;
      rangeToken2.ranges[b++] = rangeToken1.ranges[0] - 1;
    } 
    for (boolean bool = true; bool < rangeToken1.ranges.length - 2; bool += true) {
      rangeToken2.ranges[b++] = rangeToken1.ranges[bool] + 1;
      rangeToken2.ranges[b++] = rangeToken1.ranges[bool + true] - 1;
    } 
    if (j != 1114111) {
      rangeToken2.ranges[b++] = j + 1;
      rangeToken2.ranges[b] = 1114111;
    } 
    rangeToken2.setCompacted();
    return rangeToken2;
  }
  
  RangeToken getCaseInsensitiveToken() {
    if (this.icaseCache != null)
      return this.icaseCache; 
    RangeToken rangeToken1 = (this.type == 4) ? Token.createRange() : Token.createNRange();
    for (boolean bool1 = false; bool1 < this.ranges.length; bool1 += true) {
      for (int i = this.ranges[bool1]; i <= this.ranges[bool1 + true]; i++) {
        if (i > 65535) {
          rangeToken1.addRange(i, i);
        } else {
          char c = Character.toUpperCase((char)i);
          rangeToken1.addRange(c, c);
        } 
      } 
    } 
    RangeToken rangeToken2 = (this.type == 4) ? Token.createRange() : Token.createNRange();
    for (boolean bool2 = false; bool2 < rangeToken1.ranges.length; bool2 += true) {
      for (int i = rangeToken1.ranges[bool2]; i <= rangeToken1.ranges[bool2 + true]; i++) {
        if (i > 65535) {
          rangeToken2.addRange(i, i);
        } else {
          char c = Character.toUpperCase((char)i);
          rangeToken2.addRange(c, c);
        } 
      } 
    } 
    rangeToken2.mergeRanges(rangeToken1);
    rangeToken2.mergeRanges(this);
    rangeToken2.compactRanges();
    this.icaseCache = rangeToken2;
    return rangeToken2;
  }
  
  void dumpRanges() {
    System.err.print("RANGE: ");
    if (this.ranges == null)
      System.err.println(" NULL"); 
    for (boolean bool = false; bool < this.ranges.length; bool += true)
      System.err.print("[" + this.ranges[bool] + "," + this.ranges[bool + true] + "] "); 
    System.err.println("");
  }
  
  boolean match(int paramInt) {
    boolean bool;
    if (this.map == null)
      createMap(); 
    if (this.type == 4) {
      if (paramInt < 256)
        return ((this.map[paramInt / 32] & 1 << (paramInt & 0x1F)) != 0); 
      bool = false;
      for (int i = this.nonMapIndex; i < this.ranges.length; i += 2) {
        if (this.ranges[i] <= paramInt && paramInt <= this.ranges[i + 1])
          return true; 
      } 
    } else {
      if (paramInt < 256)
        return ((this.map[paramInt / 32] & 1 << (paramInt & 0x1F)) == 0); 
      bool = true;
      for (int i = this.nonMapIndex; i < this.ranges.length; i += 2) {
        if (this.ranges[i] <= paramInt && paramInt <= this.ranges[i + 1])
          return false; 
      } 
    } 
    return bool;
  }
  
  private void createMap() {
    byte b = 8;
    int[] arrayOfInt = new int[b];
    int i = this.ranges.length;
    int j;
    for (j = 0; j < b; j++)
      arrayOfInt[j] = 0; 
    for (j = 0; j < this.ranges.length; j += 2) {
      int k = this.ranges[j];
      int m = this.ranges[j + 1];
      if (k < 256) {
        for (int n = k; n <= m && n < 256; n++)
          arrayOfInt[n / 32] = arrayOfInt[n / 32] | 1 << (n & 0x1F); 
      } else {
        i = j;
        break;
      } 
      if (m >= 256) {
        i = j;
        break;
      } 
    } 
    this.map = arrayOfInt;
    this.nonMapIndex = i;
  }
  
  public String toString(int paramInt) {
    String str;
    if (this.type == 4) {
      if (this == Token.token_dot) {
        str = ".";
      } else if (this == Token.token_0to9) {
        str = "\\d";
      } else if (this == Token.token_wordchars) {
        str = "\\w";
      } else if (this == Token.token_spaces) {
        str = "\\s";
      } else {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        for (boolean bool = false; bool < this.ranges.length; bool += true) {
          if ((paramInt & 0x400) != 0 && bool)
            stringBuffer.append(","); 
          if (this.ranges[bool] == this.ranges[bool + true]) {
            stringBuffer.append(escapeCharInCharClass(this.ranges[bool]));
          } else {
            stringBuffer.append(escapeCharInCharClass(this.ranges[bool]));
            stringBuffer.append('-');
            stringBuffer.append(escapeCharInCharClass(this.ranges[bool + true]));
          } 
        } 
        stringBuffer.append("]");
        str = stringBuffer.toString();
      } 
    } else if (this == Token.token_not_0to9) {
      str = "\\D";
    } else if (this == Token.token_not_wordchars) {
      str = "\\W";
    } else if (this == Token.token_not_spaces) {
      str = "\\S";
    } else {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("[^");
      for (boolean bool = false; bool < this.ranges.length; bool += true) {
        if ((paramInt & 0x400) != 0 && bool)
          stringBuffer.append(","); 
        if (this.ranges[bool] == this.ranges[bool + true]) {
          stringBuffer.append(escapeCharInCharClass(this.ranges[bool]));
        } else {
          stringBuffer.append(escapeCharInCharClass(this.ranges[bool]));
          stringBuffer.append('-');
          stringBuffer.append(escapeCharInCharClass(this.ranges[bool + true]));
        } 
      } 
      stringBuffer.append("]");
      str = stringBuffer.toString();
    } 
    return str;
  }
  
  private static String escapeCharInCharClass(int paramInt) {
    String str;
    switch (paramInt) {
      case 44:
      case 45:
      case 91:
      case 92:
      case 93:
      case 94:
        return "\\" + (char)paramInt;
      case 12:
        return "\\f";
      case 10:
        return "\\n";
      case 13:
        return "\\r";
      case 9:
        return "\\t";
      case 27:
        return "\\e";
    } 
    if (paramInt < 32) {
      String str1 = "0" + Integer.toHexString(paramInt);
      str = "\\x" + str1.substring(str1.length() - 2, str1.length());
    } else if (paramInt >= 65536) {
      String str1 = "0" + Integer.toHexString(paramInt);
      str = "\\v" + str1.substring(str1.length() - 6, str1.length());
    } else {
      str = "" + (char)paramInt;
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xpath\regex\RangeToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */