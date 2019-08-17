package com.sun.org.apache.xerces.internal.impl.dtd;

public class XMLNotationDecl {
  public String name;
  
  public String publicId;
  
  public String systemId;
  
  public String baseSystemId;
  
  public void setValues(String paramString1, String paramString2, String paramString3, String paramString4) {
    this.name = paramString1;
    this.publicId = paramString2;
    this.systemId = paramString3;
    this.baseSystemId = paramString4;
  }
  
  public void clear() {
    this.name = null;
    this.publicId = null;
    this.systemId = null;
    this.baseSystemId = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\XMLNotationDecl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */