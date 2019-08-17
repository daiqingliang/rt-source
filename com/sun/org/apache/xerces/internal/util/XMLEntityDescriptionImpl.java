package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.impl.XMLEntityDescription;

public class XMLEntityDescriptionImpl extends XMLResourceIdentifierImpl implements XMLEntityDescription {
  protected String fEntityName;
  
  public XMLEntityDescriptionImpl() {}
  
  public XMLEntityDescriptionImpl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) { setDescription(paramString1, paramString2, paramString3, paramString4, paramString5); }
  
  public XMLEntityDescriptionImpl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6) { setDescription(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6); }
  
  public void setEntityName(String paramString) { this.fEntityName = paramString; }
  
  public String getEntityName() { return this.fEntityName; }
  
  public void setDescription(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) { setDescription(paramString1, paramString2, paramString3, paramString4, paramString5, null); }
  
  public void setDescription(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6) {
    this.fEntityName = paramString1;
    setValues(paramString2, paramString3, paramString4, paramString5, paramString6);
  }
  
  public void clear() {
    super.clear();
    this.fEntityName = null;
  }
  
  public int hashCode() {
    int i = super.hashCode();
    if (this.fEntityName != null)
      i += this.fEntityName.hashCode(); 
    return i;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.fEntityName != null)
      stringBuffer.append(this.fEntityName); 
    stringBuffer.append(':');
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\XMLEntityDescriptionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */