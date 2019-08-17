package com.sun.xml.internal.ws.encoding;

import javax.xml.ws.WebServiceException;

class HeaderTokenizer {
  private String string;
  
  private boolean skipComments;
  
  private String delimiters;
  
  private int currentPos;
  
  private int maxPos;
  
  private int nextPos;
  
  private int peekPos;
  
  private static final String RFC822 = "()<>@,;:\\\"\t .[]";
  
  static final String MIME = "()<>@,;:\\\"\t []/?=";
  
  private static final Token EOFToken = new Token(-4, null);
  
  HeaderTokenizer(String paramString1, String paramString2, boolean paramBoolean) {
    this.string = (paramString1 == null) ? "" : paramString1;
    this.skipComments = paramBoolean;
    this.delimiters = paramString2;
    this.currentPos = this.nextPos = this.peekPos = 0;
    this.maxPos = this.string.length();
  }
  
  HeaderTokenizer(String paramString1, String paramString2) { this(paramString1, paramString2, true); }
  
  HeaderTokenizer(String paramString) { this(paramString, "()<>@,;:\\\"\t .[]"); }
  
  Token next() throws WebServiceException {
    this.currentPos = this.nextPos;
    Token token = getNext();
    this.nextPos = this.peekPos = this.currentPos;
    return token;
  }
  
  Token peek() throws WebServiceException {
    this.currentPos = this.peekPos;
    Token token = getNext();
    this.peekPos = this.currentPos;
    return token;
  }
  
  String getRemainder() { return this.string.substring(this.nextPos); }
  
  private Token getNext() throws WebServiceException {
    if (this.currentPos >= this.maxPos)
      return EOFToken; 
    if (skipWhiteSpace() == -4)
      return EOFToken; 
    boolean bool = false;
    char c;
    for (c = this.string.charAt(this.currentPos); c == '('; c = this.string.charAt(this.currentPos)) {
      int j = ++this.currentPos;
      byte b = 1;
      while (b && this.currentPos < this.maxPos) {
        c = this.string.charAt(this.currentPos);
        if (c == '\\') {
          this.currentPos++;
          bool = true;
        } else if (c == '\r') {
          bool = true;
        } else if (c == '(') {
          b++;
        } else if (c == ')') {
          b--;
        } 
        this.currentPos++;
      } 
      if (b != 0)
        throw new WebServiceException("Unbalanced comments"); 
      if (!this.skipComments) {
        String str;
        if (bool) {
          str = filterToken(this.string, j, this.currentPos - 1);
        } else {
          str = this.string.substring(j, this.currentPos - 1);
        } 
        return new Token(-3, str);
      } 
      if (skipWhiteSpace() == -4)
        return EOFToken; 
    } 
    if (c == '"') {
      int j = ++this.currentPos;
      while (this.currentPos < this.maxPos) {
        c = this.string.charAt(this.currentPos);
        if (c == '\\') {
          this.currentPos++;
          bool = true;
        } else if (c == '\r') {
          bool = true;
        } else if (c == '"') {
          String str;
          this.currentPos++;
          if (bool) {
            str = filterToken(this.string, j, this.currentPos - 1);
          } else {
            str = this.string.substring(j, this.currentPos - 1);
          } 
          return new Token(-2, str);
        } 
        this.currentPos++;
      } 
      throw new WebServiceException("Unbalanced quoted string");
    } 
    if (c < ' ' || c >= '' || this.delimiters.indexOf(c) >= 0) {
      this.currentPos++;
      char[] arrayOfChar = new char[1];
      arrayOfChar[0] = c;
      return new Token(c, new String(arrayOfChar));
    } 
    int i = this.currentPos;
    while (this.currentPos < this.maxPos) {
      c = this.string.charAt(this.currentPos);
      if (c < ' ' || c >= '' || c == '(' || c == ' ' || c == '"' || this.delimiters.indexOf(c) >= 0)
        break; 
      this.currentPos++;
    } 
    return new Token(-1, this.string.substring(i, this.currentPos));
  }
  
  private int skipWhiteSpace() {
    while (this.currentPos < this.maxPos) {
      char c;
      if ((c = this.string.charAt(this.currentPos)) != ' ' && c != '\t' && c != '\r' && c != '\n')
        return this.currentPos; 
      this.currentPos++;
    } 
    return -4;
  }
  
  private static String filterToken(String paramString, int paramInt1, int paramInt2) {
    StringBuffer stringBuffer = new StringBuffer();
    boolean bool1 = false;
    boolean bool2 = false;
    for (int i = paramInt1; i < paramInt2; i++) {
      char c = paramString.charAt(i);
      if (c == '\n' && bool2) {
        bool2 = false;
      } else {
        bool2 = false;
        if (!bool1) {
          if (c == '\\') {
            bool1 = true;
          } else if (c == '\r') {
            bool2 = true;
          } else {
            stringBuffer.append(c);
          } 
        } else {
          stringBuffer.append(c);
          bool1 = false;
        } 
      } 
    } 
    return stringBuffer.toString();
  }
  
  static class Token {
    private int type;
    
    private String value;
    
    public static final int ATOM = -1;
    
    public static final int QUOTEDSTRING = -2;
    
    public static final int COMMENT = -3;
    
    public static final int EOF = -4;
    
    public Token(int param1Int, String param1String) {
      this.type = param1Int;
      this.value = param1String;
    }
    
    public int getType() { return this.type; }
    
    public String getValue() { return this.value; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\HeaderTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */