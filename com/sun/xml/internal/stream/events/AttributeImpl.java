package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;

public class AttributeImpl extends DummyEvent implements Attribute {
  private String fValue;
  
  private String fNonNormalizedvalue;
  
  private QName fQName;
  
  private String fAttributeType = "CDATA";
  
  private boolean fIsSpecified;
  
  public AttributeImpl() { init(); }
  
  public AttributeImpl(String paramString1, String paramString2) {
    init();
    this.fQName = new QName(paramString1);
    this.fValue = paramString2;
  }
  
  public AttributeImpl(String paramString1, String paramString2, String paramString3) { this(paramString1, null, paramString2, paramString3, null, null, false); }
  
  public AttributeImpl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) { this(paramString1, paramString2, paramString3, paramString4, null, paramString5, false); }
  
  public AttributeImpl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, boolean paramBoolean) { this(new QName(paramString2, paramString3, paramString1), paramString4, paramString5, paramString6, paramBoolean); }
  
  public AttributeImpl(QName paramQName, String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    init();
    this.fQName = paramQName;
    this.fValue = paramString1;
    if (paramString3 != null && !paramString3.equals(""))
      this.fAttributeType = paramString3; 
    this.fNonNormalizedvalue = paramString2;
    this.fIsSpecified = paramBoolean;
  }
  
  public String toString() { return (this.fQName.getPrefix() != null && this.fQName.getPrefix().length() > 0) ? (this.fQName.getPrefix() + ":" + this.fQName.getLocalPart() + "='" + this.fValue + "'") : (this.fQName.getLocalPart() + "='" + this.fValue + "'"); }
  
  public void setName(QName paramQName) { this.fQName = paramQName; }
  
  public QName getName() { return this.fQName; }
  
  public void setValue(String paramString) { this.fValue = paramString; }
  
  public String getValue() { return this.fValue; }
  
  public void setNonNormalizedValue(String paramString) { this.fNonNormalizedvalue = paramString; }
  
  public String getNonNormalizedValue() { return this.fNonNormalizedvalue; }
  
  public void setAttributeType(String paramString) { this.fAttributeType = paramString; }
  
  public String getDTDType() { return this.fAttributeType; }
  
  public void setSpecified(boolean paramBoolean) { this.fIsSpecified = paramBoolean; }
  
  public boolean isSpecified() { return this.fIsSpecified; }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter) throws IOException { paramWriter.write(toString()); }
  
  protected void init() { setEventType(10); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\events\AttributeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */