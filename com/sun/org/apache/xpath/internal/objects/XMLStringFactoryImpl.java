package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;

public class XMLStringFactoryImpl extends XMLStringFactory {
  private static XMLStringFactory m_xstringfactory = new XMLStringFactoryImpl();
  
  public static XMLStringFactory getFactory() { return m_xstringfactory; }
  
  public XMLString newstr(String paramString) { return new XString(paramString); }
  
  public XMLString newstr(FastStringBuffer paramFastStringBuffer, int paramInt1, int paramInt2) { return new XStringForFSB(paramFastStringBuffer, paramInt1, paramInt2); }
  
  public XMLString newstr(char[] paramArrayOfChar, int paramInt1, int paramInt2) { return new XStringForChars(paramArrayOfChar, paramInt1, paramInt2); }
  
  public XMLString emptystr() { return XString.EMPTYSTRING; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\objects\XMLStringFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */