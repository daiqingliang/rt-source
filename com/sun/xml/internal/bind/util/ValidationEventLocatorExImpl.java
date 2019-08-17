package com.sun.xml.internal.bind.util;

import com.sun.xml.internal.bind.ValidationEventLocatorEx;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;

public class ValidationEventLocatorExImpl extends ValidationEventLocatorImpl implements ValidationEventLocatorEx {
  private final String fieldName;
  
  public ValidationEventLocatorExImpl(Object paramObject, String paramString) {
    super(paramObject);
    this.fieldName = paramString;
  }
  
  public String getFieldName() { return this.fieldName; }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[url=");
    stringBuffer.append(getURL());
    stringBuffer.append(",line=");
    stringBuffer.append(getLineNumber());
    stringBuffer.append(",column=");
    stringBuffer.append(getColumnNumber());
    stringBuffer.append(",node=");
    stringBuffer.append(getNode());
    stringBuffer.append(",object=");
    stringBuffer.append(getObject());
    stringBuffer.append(",field=");
    stringBuffer.append(getFieldName());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bin\\util\ValidationEventLocatorExImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */