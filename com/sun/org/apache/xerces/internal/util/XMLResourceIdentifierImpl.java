package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;

public class XMLResourceIdentifierImpl implements XMLResourceIdentifier {
  protected String fPublicId;
  
  protected String fLiteralSystemId;
  
  protected String fBaseSystemId;
  
  protected String fExpandedSystemId;
  
  protected String fNamespace;
  
  public XMLResourceIdentifierImpl() {}
  
  public XMLResourceIdentifierImpl(String paramString1, String paramString2, String paramString3, String paramString4) { setValues(paramString1, paramString2, paramString3, paramString4, null); }
  
  public XMLResourceIdentifierImpl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) { setValues(paramString1, paramString2, paramString3, paramString4, paramString5); }
  
  public void setValues(String paramString1, String paramString2, String paramString3, String paramString4) { setValues(paramString1, paramString2, paramString3, paramString4, null); }
  
  public void setValues(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    this.fPublicId = paramString1;
    this.fLiteralSystemId = paramString2;
    this.fBaseSystemId = paramString3;
    this.fExpandedSystemId = paramString4;
    this.fNamespace = paramString5;
  }
  
  public void clear() {
    this.fPublicId = null;
    this.fLiteralSystemId = null;
    this.fBaseSystemId = null;
    this.fExpandedSystemId = null;
    this.fNamespace = null;
  }
  
  public void setPublicId(String paramString) { this.fPublicId = paramString; }
  
  public void setLiteralSystemId(String paramString) { this.fLiteralSystemId = paramString; }
  
  public void setBaseSystemId(String paramString) { this.fBaseSystemId = paramString; }
  
  public void setExpandedSystemId(String paramString) { this.fExpandedSystemId = paramString; }
  
  public void setNamespace(String paramString) { this.fNamespace = paramString; }
  
  public String getPublicId() { return this.fPublicId; }
  
  public String getLiteralSystemId() { return this.fLiteralSystemId; }
  
  public String getBaseSystemId() { return this.fBaseSystemId; }
  
  public String getExpandedSystemId() { return this.fExpandedSystemId; }
  
  public String getNamespace() { return this.fNamespace; }
  
  public int hashCode() {
    int i = 0;
    if (this.fPublicId != null)
      i += this.fPublicId.hashCode(); 
    if (this.fLiteralSystemId != null)
      i += this.fLiteralSystemId.hashCode(); 
    if (this.fBaseSystemId != null)
      i += this.fBaseSystemId.hashCode(); 
    if (this.fExpandedSystemId != null)
      i += this.fExpandedSystemId.hashCode(); 
    if (this.fNamespace != null)
      i += this.fNamespace.hashCode(); 
    return i;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.fPublicId != null)
      stringBuffer.append(this.fPublicId); 
    stringBuffer.append(':');
    if (this.fLiteralSystemId != null)
      stringBuffer.append(this.fLiteralSystemId); 
    stringBuffer.append(':');
    if (this.fBaseSystemId != null)
      stringBuffer.append(this.fBaseSystemId); 
    stringBuffer.append(':');
    if (this.fExpandedSystemId != null)
      stringBuffer.append(this.fExpandedSystemId); 
    stringBuffer.append(':');
    if (this.fNamespace != null)
      stringBuffer.append(this.fNamespace); 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\XMLResourceIdentifierImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */