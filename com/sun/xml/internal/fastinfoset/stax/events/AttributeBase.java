package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;

public class AttributeBase extends EventBase implements Attribute {
  private QName _QName;
  
  private String _value;
  
  private String _attributeType = null;
  
  private boolean _specified = false;
  
  public AttributeBase() { super(10); }
  
  public AttributeBase(String paramString1, String paramString2) {
    super(10);
    this._QName = new QName(paramString1);
    this._value = paramString2;
  }
  
  public AttributeBase(QName paramQName, String paramString) {
    this._QName = paramQName;
    this._value = paramString;
  }
  
  public AttributeBase(String paramString1, String paramString2, String paramString3) { this(paramString1, null, paramString2, paramString3, null); }
  
  public AttributeBase(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    if (paramString1 == null)
      paramString1 = ""; 
    this._QName = new QName(paramString2, paramString3, paramString1);
    this._value = paramString4;
    this._attributeType = (paramString5 == null) ? "CDATA" : paramString5;
  }
  
  public void setName(QName paramQName) { this._QName = paramQName; }
  
  public QName getName() { return this._QName; }
  
  public void setValue(String paramString) { this._value = paramString; }
  
  public String getLocalName() { return this._QName.getLocalPart(); }
  
  public String getValue() { return this._value; }
  
  public void setAttributeType(String paramString) { this._attributeType = paramString; }
  
  public String getDTDType() { return this._attributeType; }
  
  public boolean isSpecified() { return this._specified; }
  
  public void setSpecified(boolean paramBoolean) { this._specified = paramBoolean; }
  
  public String toString() {
    String str = this._QName.getPrefix();
    return !Util.isEmptyString(str) ? (str + ":" + this._QName.getLocalPart() + "='" + this._value + "'") : (this._QName.getLocalPart() + "='" + this._value + "'");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\AttributeBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */