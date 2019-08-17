package sun.net.www;

import java.util.Iterator;

public class HeaderParser {
  String raw;
  
  String[][] tab;
  
  int nkeys;
  
  int asize = 10;
  
  public HeaderParser(String paramString) {
    this.raw = paramString;
    this.tab = new String[this.asize][2];
    parse();
  }
  
  private HeaderParser() {}
  
  public HeaderParser subsequence(int paramInt1, int paramInt2) {
    if (paramInt1 == 0 && paramInt2 == this.nkeys)
      return this; 
    if (paramInt1 < 0 || paramInt1 >= paramInt2 || paramInt2 > this.nkeys)
      throw new IllegalArgumentException("invalid start or end"); 
    HeaderParser headerParser = new HeaderParser();
    headerParser.tab = new String[this.asize][2];
    headerParser.asize = this.asize;
    System.arraycopy(this.tab, paramInt1, headerParser.tab, 0, paramInt2 - paramInt1);
    headerParser.nkeys = paramInt2 - paramInt1;
    return headerParser;
  }
  
  private void parse() {
    if (this.raw != null) {
      this.raw = this.raw.trim();
      char[] arrayOfChar = this.raw.toCharArray();
      byte b1 = 0;
      byte b2 = 0;
      byte b3 = 0;
      boolean bool1 = true;
      boolean bool2 = false;
      int i = arrayOfChar.length;
      while (b2 < i) {
        char c = arrayOfChar[b2];
        if (c == '=' && !bool2) {
          this.tab[b3][0] = (new String(arrayOfChar, b1, b2 - b1)).toLowerCase();
          bool1 = false;
          b1 = ++b2;
        } else if (c == '"') {
          if (bool2) {
            this.tab[b3++][1] = new String(arrayOfChar, b1, b2 - b1);
            bool2 = false;
            do {
            
            } while (++b2 < i && (arrayOfChar[b2] == ' ' || arrayOfChar[b2] == ','));
            bool1 = true;
            b1 = b2;
          } else {
            bool2 = true;
            b1 = ++b2;
          } 
        } else if (c == ' ' || c == ',') {
          if (bool2) {
            b2++;
            continue;
          } 
          if (bool1) {
            this.tab[b3++][0] = (new String(arrayOfChar, b1, b2 - b1)).toLowerCase();
          } else {
            this.tab[b3++][1] = new String(arrayOfChar, b1, b2 - b1);
          } 
          while (b2 < i && (arrayOfChar[b2] == ' ' || arrayOfChar[b2] == ','))
            b2++; 
          bool1 = true;
          b1 = b2;
        } else {
          b2++;
        } 
        if (b3 == this.asize) {
          this.asize *= 2;
          String[][] arrayOfString = new String[this.asize][2];
          System.arraycopy(this.tab, 0, arrayOfString, 0, this.tab.length);
          this.tab = arrayOfString;
        } 
      } 
      if (--b2 > b1) {
        if (!bool1) {
          if (arrayOfChar[b2] == '"') {
            this.tab[b3++][1] = new String(arrayOfChar, b1, b2 - b1);
          } else {
            this.tab[b3++][1] = new String(arrayOfChar, b1, b2 - b1 + 1);
          } 
        } else {
          this.tab[b3++][0] = (new String(arrayOfChar, b1, b2 - b1 + 1)).toLowerCase();
        } 
      } else if (b2 == b1) {
        if (!bool1) {
          if (arrayOfChar[b2] == '"') {
            this.tab[b3++][1] = String.valueOf(arrayOfChar[b2 - 1]);
          } else {
            this.tab[b3++][1] = String.valueOf(arrayOfChar[b2]);
          } 
        } else {
          this.tab[b3++][0] = String.valueOf(arrayOfChar[b2]).toLowerCase();
        } 
      } 
      this.nkeys = b3;
    } 
  }
  
  public String findKey(int paramInt) { return (paramInt < 0 || paramInt > this.asize) ? null : this.tab[paramInt][0]; }
  
  public String findValue(int paramInt) { return (paramInt < 0 || paramInt > this.asize) ? null : this.tab[paramInt][1]; }
  
  public String findValue(String paramString) { return findValue(paramString, null); }
  
  public String findValue(String paramString1, String paramString2) {
    if (paramString1 == null)
      return paramString2; 
    paramString1 = paramString1.toLowerCase();
    for (byte b = 0; b < this.asize; b++) {
      if (this.tab[b][false] == null)
        return paramString2; 
      if (paramString1.equals(this.tab[b][0]))
        return this.tab[b][1]; 
    } 
    return paramString2;
  }
  
  public Iterator<String> keys() { return new ParserIterator(false); }
  
  public Iterator<String> values() { return new ParserIterator(true); }
  
  public String toString() {
    Iterator iterator = keys();
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("{size=" + this.asize + " nkeys=" + this.nkeys + " ");
    for (byte b = 0; iterator.hasNext(); b++) {
      String str1 = (String)iterator.next();
      String str2 = findValue(b);
      if (str2 != null && "".equals(str2))
        str2 = null; 
      stringBuffer.append(" {" + str1 + ((str2 == null) ? "" : ("," + str2)) + "}");
      if (iterator.hasNext())
        stringBuffer.append(","); 
    } 
    stringBuffer.append(" }");
    return new String(stringBuffer);
  }
  
  public int findInt(String paramString, int paramInt) {
    try {
      return Integer.parseInt(findValue(paramString, String.valueOf(paramInt)));
    } catch (Throwable throwable) {
      return paramInt;
    } 
  }
  
  class ParserIterator extends Object implements Iterator<String> {
    int index;
    
    boolean returnsValue;
    
    ParserIterator(boolean param1Boolean) { this.returnsValue = param1Boolean; }
    
    public boolean hasNext() { return (this.index < HeaderParser.this.nkeys); }
    
    public String next() { return HeaderParser.this.tab[this.index++][this.returnsValue ? 1 : 0]; }
    
    public void remove() { throw new UnsupportedOperationException("remove not supported"); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\HeaderParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */