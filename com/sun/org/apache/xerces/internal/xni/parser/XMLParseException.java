package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;

public class XMLParseException extends XNIException {
  static final long serialVersionUID = 1732959359448549967L;
  
  protected String fPublicId;
  
  protected String fLiteralSystemId;
  
  protected String fExpandedSystemId;
  
  protected String fBaseSystemId;
  
  protected int fLineNumber = -1;
  
  protected int fColumnNumber = -1;
  
  protected int fCharacterOffset = -1;
  
  public XMLParseException(XMLLocator paramXMLLocator, String paramString) {
    super(paramString);
    if (paramXMLLocator != null) {
      this.fPublicId = paramXMLLocator.getPublicId();
      this.fLiteralSystemId = paramXMLLocator.getLiteralSystemId();
      this.fExpandedSystemId = paramXMLLocator.getExpandedSystemId();
      this.fBaseSystemId = paramXMLLocator.getBaseSystemId();
      this.fLineNumber = paramXMLLocator.getLineNumber();
      this.fColumnNumber = paramXMLLocator.getColumnNumber();
      this.fCharacterOffset = paramXMLLocator.getCharacterOffset();
    } 
  }
  
  public XMLParseException(XMLLocator paramXMLLocator, String paramString, Exception paramException) {
    super(paramString, paramException);
    if (paramXMLLocator != null) {
      this.fPublicId = paramXMLLocator.getPublicId();
      this.fLiteralSystemId = paramXMLLocator.getLiteralSystemId();
      this.fExpandedSystemId = paramXMLLocator.getExpandedSystemId();
      this.fBaseSystemId = paramXMLLocator.getBaseSystemId();
      this.fLineNumber = paramXMLLocator.getLineNumber();
      this.fColumnNumber = paramXMLLocator.getColumnNumber();
      this.fCharacterOffset = paramXMLLocator.getCharacterOffset();
    } 
  }
  
  public String getPublicId() { return this.fPublicId; }
  
  public String getExpandedSystemId() { return this.fExpandedSystemId; }
  
  public String getLiteralSystemId() { return this.fLiteralSystemId; }
  
  public String getBaseSystemId() { return this.fBaseSystemId; }
  
  public int getLineNumber() { return this.fLineNumber; }
  
  public int getColumnNumber() { return this.fColumnNumber; }
  
  public int getCharacterOffset() { return this.fCharacterOffset; }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.fPublicId != null)
      stringBuffer.append(this.fPublicId); 
    stringBuffer.append(':');
    if (this.fLiteralSystemId != null)
      stringBuffer.append(this.fLiteralSystemId); 
    stringBuffer.append(':');
    if (this.fExpandedSystemId != null)
      stringBuffer.append(this.fExpandedSystemId); 
    stringBuffer.append(':');
    if (this.fBaseSystemId != null)
      stringBuffer.append(this.fBaseSystemId); 
    stringBuffer.append(':');
    stringBuffer.append(this.fLineNumber);
    stringBuffer.append(':');
    stringBuffer.append(this.fColumnNumber);
    stringBuffer.append(':');
    stringBuffer.append(this.fCharacterOffset);
    stringBuffer.append(':');
    String str = getMessage();
    if (str == null) {
      Exception exception = getException();
      if (exception != null)
        str = exception.getMessage(); 
    } 
    if (str != null)
      stringBuffer.append(str); 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xni\parser\XMLParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */