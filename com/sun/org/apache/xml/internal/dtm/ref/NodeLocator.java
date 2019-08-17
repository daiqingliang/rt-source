package com.sun.org.apache.xml.internal.dtm.ref;

import javax.xml.transform.SourceLocator;

public class NodeLocator implements SourceLocator {
  protected String m_publicId;
  
  protected String m_systemId;
  
  protected int m_lineNumber;
  
  protected int m_columnNumber;
  
  public NodeLocator(String paramString1, String paramString2, int paramInt1, int paramInt2) {
    this.m_publicId = paramString1;
    this.m_systemId = paramString2;
    this.m_lineNumber = paramInt1;
    this.m_columnNumber = paramInt2;
  }
  
  public String getPublicId() { return this.m_publicId; }
  
  public String getSystemId() { return this.m_systemId; }
  
  public int getLineNumber() { return this.m_lineNumber; }
  
  public int getColumnNumber() { return this.m_columnNumber; }
  
  public String toString() { return "file '" + this.m_systemId + "', line #" + this.m_lineNumber + ", column #" + this.m_columnNumber; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\NodeLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */