package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;

public class SimpleLocator implements XMLLocator {
  String lsid;
  
  String esid;
  
  int line;
  
  int column;
  
  int charOffset;
  
  public SimpleLocator() {}
  
  public SimpleLocator(String paramString1, String paramString2, int paramInt1, int paramInt2) { this(paramString1, paramString2, paramInt1, paramInt2, -1); }
  
  public void setValues(String paramString1, String paramString2, int paramInt1, int paramInt2) { setValues(paramString1, paramString2, paramInt1, paramInt2, -1); }
  
  public SimpleLocator(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3) {
    this.line = paramInt1;
    this.column = paramInt2;
    this.lsid = paramString1;
    this.esid = paramString2;
    this.charOffset = paramInt3;
  }
  
  public void setValues(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3) {
    this.line = paramInt1;
    this.column = paramInt2;
    this.lsid = paramString1;
    this.esid = paramString2;
    this.charOffset = paramInt3;
  }
  
  public int getLineNumber() { return this.line; }
  
  public int getColumnNumber() { return this.column; }
  
  public int getCharacterOffset() { return this.charOffset; }
  
  public String getPublicId() { return null; }
  
  public String getExpandedSystemId() { return this.esid; }
  
  public String getLiteralSystemId() { return this.lsid; }
  
  public String getBaseSystemId() { return null; }
  
  public void setColumnNumber(int paramInt) { this.column = paramInt; }
  
  public void setLineNumber(int paramInt) { this.line = paramInt; }
  
  public void setCharacterOffset(int paramInt) { this.charOffset = paramInt; }
  
  public void setBaseSystemId(String paramString) {}
  
  public void setExpandedSystemId(String paramString) { this.esid = paramString; }
  
  public void setLiteralSystemId(String paramString) { this.lsid = paramString; }
  
  public void setPublicId(String paramString) {}
  
  public String getEncoding() { return null; }
  
  public String getXMLVersion() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\x\\util\SimpleLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */