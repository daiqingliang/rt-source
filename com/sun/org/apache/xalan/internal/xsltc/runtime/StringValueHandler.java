package com.sun.org.apache.xalan.internal.xsltc.runtime;

import com.sun.org.apache.xml.internal.serializer.EmptySerializer;
import org.xml.sax.SAXException;

public final class StringValueHandler extends EmptySerializer {
  private StringBuilder _buffer = new StringBuilder();
  
  private String _str = null;
  
  private static final String EMPTY_STR = "";
  
  private boolean m_escaping = false;
  
  private int _nestedLevel = 0;
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this._nestedLevel > 0)
      return; 
    if (this._str != null) {
      this._buffer.append(this._str);
      this._str = null;
    } 
    this._buffer.append(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public String getValue() {
    if (this._buffer.length() != 0) {
      String str1 = this._buffer.toString();
      this._buffer.setLength(0);
      return str1;
    } 
    String str = this._str;
    this._str = null;
    return (str != null) ? str : "";
  }
  
  public void characters(String paramString) throws SAXException {
    if (this._nestedLevel > 0)
      return; 
    if (this._str == null && this._buffer.length() == 0) {
      this._str = paramString;
    } else {
      if (this._str != null) {
        this._buffer.append(this._str);
        this._str = null;
      } 
      this._buffer.append(paramString);
    } 
  }
  
  public void startElement(String paramString) throws SAXException { this._nestedLevel++; }
  
  public void endElement(String paramString) throws SAXException { this._nestedLevel--; }
  
  public boolean setEscaping(boolean paramBoolean) {
    boolean bool = this.m_escaping;
    this.m_escaping = paramBoolean;
    return paramBoolean;
  }
  
  public String getValueOfPI() {
    String str = getValue();
    if (str.indexOf("?>") > 0) {
      int i = str.length();
      StringBuilder stringBuilder = new StringBuilder();
      byte b = 0;
      while (b < i) {
        char c = str.charAt(b++);
        if (c == '?' && str.charAt(b) == '>') {
          stringBuilder.append("? >");
          b++;
          continue;
        } 
        stringBuilder.append(c);
      } 
      return stringBuilder.toString();
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\StringValueHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */