package java.text;

class PatternEntry {
  static final int RESET = -2;
  
  static final int UNSET = -1;
  
  int strength = -1;
  
  String chars = "";
  
  String extension = "";
  
  public void appendQuotedExtension(StringBuffer paramStringBuffer) { appendQuoted(this.extension, paramStringBuffer); }
  
  public void appendQuotedChars(StringBuffer paramStringBuffer) { appendQuoted(this.chars, paramStringBuffer); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    PatternEntry patternEntry = (PatternEntry)paramObject;
    return this.chars.equals(patternEntry.chars);
  }
  
  public int hashCode() { return this.chars.hashCode(); }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    addToBuffer(stringBuffer, true, false, null);
    return stringBuffer.toString();
  }
  
  final int getStrength() { return this.strength; }
  
  final String getExtension() { return this.extension; }
  
  final String getChars() { return this.chars; }
  
  void addToBuffer(StringBuffer paramStringBuffer, boolean paramBoolean1, boolean paramBoolean2, PatternEntry paramPatternEntry) {
    if (paramBoolean2 && paramStringBuffer.length() > 0)
      if (this.strength == 0 || paramPatternEntry != null) {
        paramStringBuffer.append('\n');
      } else {
        paramStringBuffer.append(' ');
      }  
    if (paramPatternEntry != null) {
      paramStringBuffer.append('&');
      if (paramBoolean2)
        paramStringBuffer.append(' '); 
      paramPatternEntry.appendQuotedChars(paramStringBuffer);
      appendQuotedExtension(paramStringBuffer);
      if (paramBoolean2)
        paramStringBuffer.append(' '); 
    } 
    switch (this.strength) {
      case 3:
        paramStringBuffer.append('=');
        break;
      case 2:
        paramStringBuffer.append(',');
        break;
      case 1:
        paramStringBuffer.append(';');
        break;
      case 0:
        paramStringBuffer.append('<');
        break;
      case -2:
        paramStringBuffer.append('&');
        break;
      case -1:
        paramStringBuffer.append('?');
        break;
    } 
    if (paramBoolean2)
      paramStringBuffer.append(' '); 
    appendQuoted(this.chars, paramStringBuffer);
    if (paramBoolean1 && this.extension.length() != 0) {
      paramStringBuffer.append('/');
      appendQuoted(this.extension, paramStringBuffer);
    } 
  }
  
  static void appendQuoted(String paramString, StringBuffer paramStringBuffer) {
    boolean bool = false;
    char c = paramString.charAt(0);
    if (Character.isSpaceChar(c)) {
      bool = true;
      paramStringBuffer.append('\'');
    } else if (isSpecialChar(c)) {
      bool = true;
      paramStringBuffer.append('\'');
    } else {
      switch (c) {
        case '\t':
        case '\n':
        case '\f':
        case '\r':
        case '\020':
        case '@':
          bool = true;
          paramStringBuffer.append('\'');
          break;
        case '\'':
          bool = true;
          paramStringBuffer.append('\'');
          break;
        default:
          if (bool) {
            bool = false;
            paramStringBuffer.append('\'');
          } 
          break;
      } 
    } 
    paramStringBuffer.append(paramString);
    if (bool)
      paramStringBuffer.append('\''); 
  }
  
  PatternEntry(int paramInt, StringBuffer paramStringBuffer1, StringBuffer paramStringBuffer2) {
    this.strength = paramInt;
    this.chars = paramStringBuffer1.toString();
    this.extension = (paramStringBuffer2.length() > 0) ? paramStringBuffer2.toString() : "";
  }
  
  static boolean isSpecialChar(char paramChar) { return (paramChar == ' ' || (paramChar <= '/' && paramChar >= '"') || (paramChar <= '?' && paramChar >= ':') || (paramChar <= '`' && paramChar >= '[') || (paramChar <= '~' && paramChar >= '{')); }
  
  static class Parser {
    private String pattern;
    
    private int i;
    
    private StringBuffer newChars = new StringBuffer();
    
    private StringBuffer newExtension = new StringBuffer();
    
    public Parser(String param1String) {
      this.pattern = param1String;
      this.i = 0;
    }
    
    public PatternEntry next() throws ParseException {
      byte b = -1;
      this.newChars.setLength(0);
      this.newExtension.setLength(0);
      boolean bool1 = true;
      boolean bool2 = false;
      while (this.i < this.pattern.length()) {
        char c = this.pattern.charAt(this.i);
        if (bool2) {
          if (c == '\'') {
            bool2 = false;
          } else if (this.newChars.length() == 0) {
            this.newChars.append(c);
          } else if (bool1) {
            this.newChars.append(c);
          } else {
            this.newExtension.append(c);
          } 
        } else {
          switch (c) {
            case '=':
              if (b != -1)
                break; 
              b = 3;
              break;
            case ',':
              if (b != -1)
                break; 
              b = 2;
              break;
            case ';':
              if (b != -1)
                break; 
              b = 1;
              break;
            case '<':
              if (b != -1)
                break; 
              b = 0;
              break;
            case '&':
              if (b != -1)
                break; 
              b = -2;
              break;
            case '\t':
            case '\n':
            case '\f':
            case '\r':
            case ' ':
              break;
            case '/':
              bool1 = false;
              break;
            case '\'':
              bool2 = true;
              c = this.pattern.charAt(++this.i);
              if (this.newChars.length() == 0) {
                this.newChars.append(c);
                break;
              } 
              if (bool1) {
                this.newChars.append(c);
                break;
              } 
              this.newExtension.append(c);
              break;
            default:
              if (b == -1)
                throw new ParseException("missing char (=,;<&) : " + this.pattern.substring(this.i, (this.i + 10 < this.pattern.length()) ? (this.i + 10) : this.pattern.length()), this.i); 
              if (PatternEntry.isSpecialChar(c) && !bool2)
                throw new ParseException("Unquoted punctuation character : " + Integer.toString(c, 16), this.i); 
              if (bool1) {
                this.newChars.append(c);
                break;
              } 
              this.newExtension.append(c);
              break;
          } 
        } 
        this.i++;
      } 
      if (b == -1)
        return null; 
      if (this.newChars.length() == 0)
        throw new ParseException("missing chars (=,;<&): " + this.pattern.substring(this.i, (this.i + 10 < this.pattern.length()) ? (this.i + 10) : this.pattern.length()), this.i); 
      return new PatternEntry(b, this.newChars, this.newExtension);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\PatternEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */