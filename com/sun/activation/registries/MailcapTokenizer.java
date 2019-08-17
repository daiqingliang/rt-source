package com.sun.activation.registries;

public class MailcapTokenizer {
  public static final int UNKNOWN_TOKEN = 0;
  
  public static final int START_TOKEN = 1;
  
  public static final int STRING_TOKEN = 2;
  
  public static final int EOI_TOKEN = 5;
  
  public static final int SLASH_TOKEN = 47;
  
  public static final int SEMICOLON_TOKEN = 59;
  
  public static final int EQUALS_TOKEN = 61;
  
  private String data;
  
  private int dataIndex;
  
  private int dataLength;
  
  private int currentToken;
  
  private String currentTokenValue;
  
  private boolean isAutoquoting;
  
  private char autoquoteChar;
  
  public MailcapTokenizer(String paramString) {
    this.data = paramString;
    this.dataIndex = 0;
    this.dataLength = paramString.length();
    this.currentToken = 1;
    this.currentTokenValue = "";
    this.isAutoquoting = false;
    this.autoquoteChar = ';';
  }
  
  public void setIsAutoquoting(boolean paramBoolean) { this.isAutoquoting = paramBoolean; }
  
  public int getCurrentToken() { return this.currentToken; }
  
  public static String nameForToken(int paramInt) {
    String str = "really unknown";
    switch (paramInt) {
      case 0:
        str = "unknown";
        break;
      case 1:
        str = "start";
        break;
      case 2:
        str = "string";
        break;
      case 5:
        str = "EOI";
        break;
      case 47:
        str = "'/'";
        break;
      case 59:
        str = "';'";
        break;
      case 61:
        str = "'='";
        break;
    } 
    return str;
  }
  
  public String getCurrentTokenValue() { return this.currentTokenValue; }
  
  public int nextToken() {
    if (this.dataIndex < this.dataLength) {
      while (this.dataIndex < this.dataLength && isWhiteSpaceChar(this.data.charAt(this.dataIndex)))
        this.dataIndex++; 
      if (this.dataIndex < this.dataLength) {
        char c = this.data.charAt(this.dataIndex);
        if (this.isAutoquoting) {
          if (c == ';' || c == '=') {
            this.currentToken = c;
            this.currentTokenValue = (new Character(c)).toString();
            this.dataIndex++;
          } else {
            processAutoquoteToken();
          } 
        } else if (isStringTokenChar(c)) {
          processStringToken();
        } else if (c == '/' || c == ';' || c == '=') {
          this.currentToken = c;
          this.currentTokenValue = (new Character(c)).toString();
          this.dataIndex++;
        } else {
          this.currentToken = 0;
          this.currentTokenValue = (new Character(c)).toString();
          this.dataIndex++;
        } 
      } else {
        this.currentToken = 5;
        this.currentTokenValue = null;
      } 
    } else {
      this.currentToken = 5;
      this.currentTokenValue = null;
    } 
    return this.currentToken;
  }
  
  private void processStringToken() {
    int i = this.dataIndex;
    while (this.dataIndex < this.dataLength && isStringTokenChar(this.data.charAt(this.dataIndex)))
      this.dataIndex++; 
    this.currentToken = 2;
    this.currentTokenValue = this.data.substring(i, this.dataIndex);
  }
  
  private void processAutoquoteToken() {
    int i = this.dataIndex;
    for (boolean bool = false; this.dataIndex < this.dataLength && !bool; bool = true) {
      char c = this.data.charAt(this.dataIndex);
      if (c != this.autoquoteChar) {
        this.dataIndex++;
        continue;
      } 
    } 
    this.currentToken = 2;
    this.currentTokenValue = fixEscapeSequences(this.data.substring(i, this.dataIndex));
  }
  
  private static boolean isSpecialChar(char paramChar) {
    boolean bool = false;
    switch (paramChar) {
      case '"':
      case '(':
      case ')':
      case ',':
      case '/':
      case ':':
      case ';':
      case '<':
      case '=':
      case '>':
      case '?':
      case '@':
      case '[':
      case '\\':
      case ']':
        bool = true;
        break;
    } 
    return bool;
  }
  
  private static boolean isControlChar(char paramChar) { return Character.isISOControl(paramChar); }
  
  private static boolean isWhiteSpaceChar(char paramChar) { return Character.isWhitespace(paramChar); }
  
  private static boolean isStringTokenChar(char paramChar) { return (!isSpecialChar(paramChar) && !isControlChar(paramChar) && !isWhiteSpaceChar(paramChar)); }
  
  private static String fixEscapeSequences(String paramString) {
    int i = paramString.length();
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.ensureCapacity(i);
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c != '\\') {
        stringBuffer.append(c);
      } else if (b < i - 1) {
        char c1 = paramString.charAt(b + 1);
        stringBuffer.append(c1);
        b++;
      } else {
        stringBuffer.append(c);
      } 
    } 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\activation\registries\MailcapTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */