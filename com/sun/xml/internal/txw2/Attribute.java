package com.sun.xml.internal.txw2;

final class Attribute {
  final String nsUri;
  
  final String localName;
  
  Attribute next;
  
  final StringBuilder value = new StringBuilder();
  
  Attribute(String paramString1, String paramString2) {
    assert paramString1 != null && paramString2 != null;
    this.nsUri = paramString1;
    this.localName = paramString2;
  }
  
  boolean hasName(String paramString1, String paramString2) { return (this.localName.equals(paramString2) && this.nsUri.equals(paramString1)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */