package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.namespace.QName;

public final class Name extends Object implements Comparable<Name> {
  public final String nsUri;
  
  public final String localName;
  
  public final short nsUriIndex;
  
  public final short localNameIndex;
  
  public final short qNameIndex;
  
  public final boolean isAttribute;
  
  Name(int paramInt1, int paramInt2, String paramString1, int paramInt3, String paramString2, boolean paramBoolean) {
    this.qNameIndex = (short)paramInt1;
    this.nsUri = paramString1;
    this.localName = paramString2;
    this.nsUriIndex = (short)paramInt2;
    this.localNameIndex = (short)paramInt3;
    this.isAttribute = paramBoolean;
  }
  
  public String toString() { return '{' + this.nsUri + '}' + this.localName; }
  
  public QName toQName() { return new QName(this.nsUri, this.localName); }
  
  public boolean equals(String paramString1, String paramString2) { return (paramString2.equals(this.localName) && paramString1.equals(this.nsUri)); }
  
  public int compareTo(Name paramName) {
    int i = this.nsUri.compareTo(paramName.nsUri);
    return (i != 0) ? i : this.localName.compareTo(paramName.localName);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\Name.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */