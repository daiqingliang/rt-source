package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class XStringForChars extends XString {
  static final long serialVersionUID = -2235248887220850467L;
  
  int m_start;
  
  int m_length;
  
  protected String m_strCache = null;
  
  public XStringForChars(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    super(paramArrayOfChar);
    this.m_start = paramInt1;
    this.m_length = paramInt2;
    if (null == paramArrayOfChar)
      throw new IllegalArgumentException(XSLMessages.createXPATHMessage("ER_FASTSTRINGBUFFER_CANNOT_BE_NULL", null)); 
  }
  
  private XStringForChars(String paramString) {
    super(paramString);
    throw new IllegalArgumentException(XSLMessages.createXPATHMessage("ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING", null));
  }
  
  public FastStringBuffer fsb() { throw new RuntimeException(XSLMessages.createXPATHMessage("ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS", null)); }
  
  public void appendToFsb(FastStringBuffer paramFastStringBuffer) { paramFastStringBuffer.append((char[])this.m_obj, this.m_start, this.m_length); }
  
  public boolean hasString() { return (null != this.m_strCache); }
  
  public String str() {
    if (null == this.m_strCache)
      this.m_strCache = new String((char[])this.m_obj, this.m_start, this.m_length); 
    return this.m_strCache;
  }
  
  public Object object() { return str(); }
  
  public void dispatchCharactersEvents(ContentHandler paramContentHandler) throws SAXException { paramContentHandler.characters((char[])this.m_obj, this.m_start, this.m_length); }
  
  public void dispatchAsComment(LexicalHandler paramLexicalHandler) throws SAXException { paramLexicalHandler.comment((char[])this.m_obj, this.m_start, this.m_length); }
  
  public int length() { return this.m_length; }
  
  public char charAt(int paramInt) { return (char[])this.m_obj[paramInt + this.m_start]; }
  
  public void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3) { System.arraycopy((char[])this.m_obj, this.m_start + paramInt1, paramArrayOfChar, paramInt3, paramInt2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\objects\XStringForChars.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */