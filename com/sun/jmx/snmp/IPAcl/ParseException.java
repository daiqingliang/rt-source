package com.sun.jmx.snmp.IPAcl;

class ParseException extends Exception {
  private static final long serialVersionUID = -3695190720704845876L;
  
  protected boolean specialConstructor = true;
  
  public Token currentToken;
  
  public int[][] expectedTokenSequences;
  
  public String[] tokenImage;
  
  protected String eol = System.getProperty("line.separator", "\n");
  
  public ParseException(Token paramToken, int[][] paramArrayOfInt, String[] paramArrayOfString) {
    super("");
    this.currentToken = paramToken;
    this.expectedTokenSequences = paramArrayOfInt;
    this.tokenImage = paramArrayOfString;
  }
  
  public ParseException() {}
  
  public ParseException(String paramString) { super(paramString); }
  
  public String getMessage() {
    if (!this.specialConstructor)
      return super.getMessage(); 
    String str = "";
    int i = 0;
    for (byte b1 = 0; b1 < this.expectedTokenSequences.length; b1++) {
      if (i < this.expectedTokenSequences[b1].length)
        i = this.expectedTokenSequences[b1].length; 
      for (byte b = 0; b < this.expectedTokenSequences[b1].length; b++)
        str = str + this.tokenImage[this.expectedTokenSequences[b1][b]] + " "; 
      if (this.expectedTokenSequences[b1][this.expectedTokenSequences[b1].length - 1] != 0)
        str = str + "..."; 
      str = str + this.eol + "    ";
    } 
    null = "Encountered \"";
    Token token = this.currentToken.next;
    for (byte b2 = 0; b2 < i; b2++) {
      if (b2)
        null = null + " "; 
      if (token.kind == 0) {
        null = null + this.tokenImage[0];
        break;
      } 
      null = null + add_escapes(token.image);
      token = token.next;
    } 
    null = null + "\" at line " + this.currentToken.next.beginLine + ", column " + this.currentToken.next.beginColumn + "." + this.eol;
    if (this.expectedTokenSequences.length == 1) {
      null = null + "Was expecting:" + this.eol + "    ";
    } else {
      null = null + "Was expecting one of:" + this.eol + "    ";
    } 
    return null + str;
  }
  
  protected String add_escapes(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramString.length(); b++) {
      char c;
      switch (paramString.charAt(b)) {
        case '\000':
          break;
        case '\b':
          stringBuffer.append("\\b");
          break;
        case '\t':
          stringBuffer.append("\\t");
          break;
        case '\n':
          stringBuffer.append("\\n");
          break;
        case '\f':
          stringBuffer.append("\\f");
          break;
        case '\r':
          stringBuffer.append("\\r");
          break;
        case '"':
          stringBuffer.append("\\\"");
          break;
        case '\'':
          stringBuffer.append("\\'");
          break;
        case '\\':
          stringBuffer.append("\\\\");
          break;
        default:
          if ((c = paramString.charAt(b)) < ' ' || c > '~') {
            String str = "0000" + Integer.toString(c, 16);
            stringBuffer.append("\\u" + str.substring(str.length() - 4, str.length()));
            break;
          } 
          stringBuffer.append(c);
          break;
      } 
    } 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\ParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */