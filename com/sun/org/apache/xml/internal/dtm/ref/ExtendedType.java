package com.sun.org.apache.xml.internal.dtm.ref;

public final class ExtendedType {
  private int nodetype;
  
  private String namespace;
  
  private String localName;
  
  private int hash;
  
  public ExtendedType(int paramInt, String paramString1, String paramString2) {
    this.nodetype = paramInt;
    this.namespace = paramString1;
    this.localName = paramString2;
    this.hash = paramInt + paramString1.hashCode() + paramString2.hashCode();
  }
  
  public ExtendedType(int paramInt1, String paramString1, String paramString2, int paramInt2) {
    this.nodetype = paramInt1;
    this.namespace = paramString1;
    this.localName = paramString2;
    this.hash = paramInt2;
  }
  
  protected void redefine(int paramInt, String paramString1, String paramString2) {
    this.nodetype = paramInt;
    this.namespace = paramString1;
    this.localName = paramString2;
    this.hash = paramInt + paramString1.hashCode() + paramString2.hashCode();
  }
  
  protected void redefine(int paramInt1, String paramString1, String paramString2, int paramInt2) {
    this.nodetype = paramInt1;
    this.namespace = paramString1;
    this.localName = paramString2;
    this.hash = paramInt2;
  }
  
  public int hashCode() { return this.hash; }
  
  public boolean equals(ExtendedType paramExtendedType) {
    try {
      return (paramExtendedType.nodetype == this.nodetype && paramExtendedType.localName.equals(this.localName) && paramExtendedType.namespace.equals(this.namespace));
    } catch (NullPointerException nullPointerException) {
      return false;
    } 
  }
  
  public int getNodeType() { return this.nodetype; }
  
  public String getLocalName() { return this.localName; }
  
  public String getNamespace() { return this.namespace; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\ExtendedType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */