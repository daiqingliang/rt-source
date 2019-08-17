package com.sun.org.apache.xerces.internal.util;

final class XMLErrorCode {
  private String fDomain;
  
  private String fKey;
  
  public XMLErrorCode(String paramString1, String paramString2) {
    this.fDomain = paramString1;
    this.fKey = paramString2;
  }
  
  public void setValues(String paramString1, String paramString2) {
    this.fDomain = paramString1;
    this.fKey = paramString2;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof XMLErrorCode))
      return false; 
    XMLErrorCode xMLErrorCode = (XMLErrorCode)paramObject;
    return (this.fDomain.equals(xMLErrorCode.fDomain) && this.fKey.equals(xMLErrorCode.fKey));
  }
  
  public int hashCode() { return this.fDomain.hashCode() + this.fKey.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\XMLErrorCode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */