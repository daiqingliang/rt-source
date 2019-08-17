package com.sun.activation.registries;

import java.util.NoSuchElementException;
import java.util.Vector;

class LineTokenizer {
  private int currentPosition = 0;
  
  private int maxPosition;
  
  private String str;
  
  private Vector stack = new Vector();
  
  private static final String singles = "=";
  
  public LineTokenizer(String paramString) {
    this.str = paramString;
    this.maxPosition = paramString.length();
  }
  
  private void skipWhiteSpace() {
    while (this.currentPosition < this.maxPosition && Character.isWhitespace(this.str.charAt(this.currentPosition)))
      this.currentPosition++; 
  }
  
  public boolean hasMoreTokens() {
    if (this.stack.size() > 0)
      return true; 
    skipWhiteSpace();
    return (this.currentPosition < this.maxPosition);
  }
  
  public String nextToken() {
    int i = this.stack.size();
    if (i > 0) {
      String str1 = (String)this.stack.elementAt(i - 1);
      this.stack.removeElementAt(i - 1);
      return str1;
    } 
    skipWhiteSpace();
    if (this.currentPosition >= this.maxPosition)
      throw new NoSuchElementException(); 
    int j = this.currentPosition;
    char c = this.str.charAt(j);
    if (c == '"') {
      this.currentPosition++;
      boolean bool = false;
      while (this.currentPosition < this.maxPosition) {
        c = this.str.charAt(this.currentPosition++);
        if (c == '\\') {
          this.currentPosition++;
          bool = true;
          continue;
        } 
        if (c == '"') {
          String str1;
          if (bool) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int k = j + 1; k < this.currentPosition - 1; k++) {
              c = this.str.charAt(k);
              if (c != '\\')
                stringBuffer.append(c); 
            } 
            str1 = stringBuffer.toString();
          } else {
            str1 = this.str.substring(j + 1, this.currentPosition - 1);
          } 
          return str1;
        } 
      } 
    } else if ("=".indexOf(c) >= 0) {
      this.currentPosition++;
    } else {
      while (this.currentPosition < this.maxPosition && "=".indexOf(this.str.charAt(this.currentPosition)) < 0 && !Character.isWhitespace(this.str.charAt(this.currentPosition)))
        this.currentPosition++; 
    } 
    return this.str.substring(j, this.currentPosition);
  }
  
  public void pushToken(String paramString) { this.stack.addElement(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\activation\registries\LineTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */