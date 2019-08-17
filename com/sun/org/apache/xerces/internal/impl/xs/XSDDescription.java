package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLSchemaDescription;

public class XSDDescription extends XMLResourceIdentifierImpl implements XMLSchemaDescription {
  public static final short CONTEXT_INITIALIZE = -1;
  
  public static final short CONTEXT_INCLUDE = 0;
  
  public static final short CONTEXT_REDEFINE = 1;
  
  public static final short CONTEXT_IMPORT = 2;
  
  public static final short CONTEXT_PREPARSE = 3;
  
  public static final short CONTEXT_INSTANCE = 4;
  
  public static final short CONTEXT_ELEMENT = 5;
  
  public static final short CONTEXT_ATTRIBUTE = 6;
  
  public static final short CONTEXT_XSITYPE = 7;
  
  protected short fContextType;
  
  protected String[] fLocationHints;
  
  protected QName fTriggeringComponent;
  
  protected QName fEnclosedElementName;
  
  protected XMLAttributes fAttributes;
  
  public String getGrammarType() { return "http://www.w3.org/2001/XMLSchema"; }
  
  public short getContextType() { return this.fContextType; }
  
  public String getTargetNamespace() { return this.fNamespace; }
  
  public String[] getLocationHints() { return this.fLocationHints; }
  
  public QName getTriggeringComponent() { return this.fTriggeringComponent; }
  
  public QName getEnclosingElementName() { return this.fEnclosedElementName; }
  
  public XMLAttributes getAttributes() { return this.fAttributes; }
  
  public boolean fromInstance() { return (this.fContextType == 6 || this.fContextType == 5 || this.fContextType == 4 || this.fContextType == 7); }
  
  public boolean isExternal() { return (this.fContextType == 0 || this.fContextType == 1 || this.fContextType == 2 || this.fContextType == 5 || this.fContextType == 6 || this.fContextType == 7); }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof XMLSchemaDescription))
      return false; 
    XMLSchemaDescription xMLSchemaDescription = (XMLSchemaDescription)paramObject;
    return (this.fNamespace != null) ? this.fNamespace.equals(xMLSchemaDescription.getTargetNamespace()) : ((xMLSchemaDescription.getTargetNamespace() == null) ? 1 : 0);
  }
  
  public int hashCode() { return (this.fNamespace == null) ? 0 : this.fNamespace.hashCode(); }
  
  public void setContextType(short paramShort) { this.fContextType = paramShort; }
  
  public void setTargetNamespace(String paramString) { this.fNamespace = paramString; }
  
  public void setLocationHints(String[] paramArrayOfString) {
    int i = paramArrayOfString.length;
    this.fLocationHints = new String[i];
    System.arraycopy(paramArrayOfString, 0, this.fLocationHints, 0, i);
  }
  
  public void setTriggeringComponent(QName paramQName) { this.fTriggeringComponent = paramQName; }
  
  public void setEnclosingElementName(QName paramQName) { this.fEnclosedElementName = paramQName; }
  
  public void setAttributes(XMLAttributes paramXMLAttributes) { this.fAttributes = paramXMLAttributes; }
  
  public void reset() {
    clear();
    this.fContextType = -1;
    this.fLocationHints = null;
    this.fTriggeringComponent = null;
    this.fEnclosedElementName = null;
    this.fAttributes = null;
  }
  
  public XSDDescription makeClone() {
    XSDDescription xSDDescription = new XSDDescription();
    xSDDescription.fAttributes = this.fAttributes;
    xSDDescription.fBaseSystemId = this.fBaseSystemId;
    xSDDescription.fContextType = this.fContextType;
    xSDDescription.fEnclosedElementName = this.fEnclosedElementName;
    xSDDescription.fExpandedSystemId = this.fExpandedSystemId;
    xSDDescription.fLiteralSystemId = this.fLiteralSystemId;
    xSDDescription.fLocationHints = this.fLocationHints;
    xSDDescription.fPublicId = this.fPublicId;
    xSDDescription.fNamespace = this.fNamespace;
    xSDDescription.fTriggeringComponent = this.fTriggeringComponent;
    return xSDDescription;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSDDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */