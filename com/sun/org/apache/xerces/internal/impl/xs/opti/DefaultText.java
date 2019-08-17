package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

public class DefaultText extends NodeImpl implements Text {
  public String getData() throws DOMException { return null; }
  
  public void setData(String paramString) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public int getLength() { return 0; }
  
  public String substringData(int paramInt1, int paramInt2) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public void appendData(String paramString) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public void insertData(int paramInt, String paramString) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public void deleteData(int paramInt1, int paramInt2) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public void replaceData(int paramInt1, int paramInt2, String paramString) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public Text splitText(int paramInt) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public boolean isElementContentWhitespace() { throw new DOMException((short)9, "Method not supported"); }
  
  public String getWholeText() throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public Text replaceWholeText(String paramString) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\opti\DefaultText.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */