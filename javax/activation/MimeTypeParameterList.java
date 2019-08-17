package javax.activation;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;

public class MimeTypeParameterList {
  private Hashtable parameters = new Hashtable();
  
  private static final String TSPECIALS = "()<>@,;:/[]?=\\\"";
  
  public MimeTypeParameterList() {}
  
  public MimeTypeParameterList(String paramString) throws MimeTypeParseException { parse(paramString); }
  
  protected void parse(String paramString) throws MimeTypeParseException {
    if (paramString == null)
      return; 
    int i = paramString.length();
    if (i <= 0)
      return; 
    int j;
    char c;
    for (j = skipWhiteSpace(paramString, 0); j < i && (c = paramString.charAt(j)) == ';'; j = skipWhiteSpace(paramString, j)) {
      String str2;
      j = skipWhiteSpace(paramString, ++j);
      if (j >= i)
        return; 
      int k = j;
      while (j < i && isTokenChar(paramString.charAt(j)))
        j++; 
      String str1 = paramString.substring(k, j).toLowerCase(Locale.ENGLISH);
      j = skipWhiteSpace(paramString, j);
      if (j >= i || paramString.charAt(j) != '=')
        throw new MimeTypeParseException("Couldn't find the '=' that separates a parameter name from its value."); 
      j = skipWhiteSpace(paramString, ++j);
      if (j >= i)
        throw new MimeTypeParseException("Couldn't find a value for parameter named " + str1); 
      c = paramString.charAt(j);
      if (c == '"') {
        if (++j >= i)
          throw new MimeTypeParseException("Encountered unterminated quoted parameter value."); 
        k = j;
        while (j < i) {
          c = paramString.charAt(j);
          if (c == '"')
            break; 
          if (c == '\\')
            j++; 
          j++;
        } 
        if (c != '"')
          throw new MimeTypeParseException("Encountered unterminated quoted parameter value."); 
        str2 = unquote(paramString.substring(k, j));
        j++;
      } else if (isTokenChar(c)) {
        k = j;
        while (j < i && isTokenChar(paramString.charAt(j)))
          j++; 
        str2 = paramString.substring(k, j);
      } else {
        throw new MimeTypeParseException("Unexpected character encountered at index " + j);
      } 
      this.parameters.put(str1, str2);
    } 
    if (j < i)
      throw new MimeTypeParseException("More characters encountered in input than expected."); 
  }
  
  public int size() { return this.parameters.size(); }
  
  public boolean isEmpty() { return this.parameters.isEmpty(); }
  
  public String get(String paramString) { return (String)this.parameters.get(paramString.trim().toLowerCase(Locale.ENGLISH)); }
  
  public void set(String paramString1, String paramString2) { this.parameters.put(paramString1.trim().toLowerCase(Locale.ENGLISH), paramString2); }
  
  public void remove(String paramString) throws MimeTypeParseException { this.parameters.remove(paramString.trim().toLowerCase(Locale.ENGLISH)); }
  
  public Enumeration getNames() { return this.parameters.keys(); }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.ensureCapacity(this.parameters.size() * 16);
    Enumeration enumeration = this.parameters.keys();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      stringBuffer.append("; ");
      stringBuffer.append(str);
      stringBuffer.append('=');
      stringBuffer.append(quote((String)this.parameters.get(str)));
    } 
    return stringBuffer.toString();
  }
  
  private static boolean isTokenChar(char paramChar) { return (paramChar > ' ' && paramChar < '' && "()<>@,;:/[]?=\\\"".indexOf(paramChar) < 0); }
  
  private static int skipWhiteSpace(String paramString, int paramInt) {
    int i = paramString.length();
    while (paramInt < i && Character.isWhitespace(paramString.charAt(paramInt)))
      paramInt++; 
    return paramInt;
  }
  
  private static String quote(String paramString) {
    boolean bool = false;
    int i = paramString.length();
    for (byte b = 0; b < i && !bool; b++)
      bool = !isTokenChar(paramString.charAt(b)) ? 1 : 0; 
    if (bool) {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.ensureCapacity((int)(i * 1.5D));
      stringBuffer.append('"');
      for (byte b1 = 0; b1 < i; b1++) {
        char c = paramString.charAt(b1);
        if (c == '\\' || c == '"')
          stringBuffer.append('\\'); 
        stringBuffer.append(c);
      } 
      stringBuffer.append('"');
      return stringBuffer.toString();
    } 
    return paramString;
  }
  
  private static String unquote(String paramString) {
    int i = paramString.length();
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.ensureCapacity(i);
    boolean bool = false;
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (!bool && c != '\\') {
        stringBuffer.append(c);
      } else if (bool) {
        stringBuffer.append(c);
        bool = false;
      } else {
        bool = true;
      } 
    } 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activation\MimeTypeParameterList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */