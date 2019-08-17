package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

abstract class PropertyImpl<BeanT> extends Object implements Property<BeanT> {
  protected final String fieldName;
  
  private RuntimePropertyInfo propertyInfo = null;
  
  private boolean hiddenByOverride = false;
  
  public PropertyImpl(JAXBContextImpl paramJAXBContextImpl, RuntimePropertyInfo paramRuntimePropertyInfo) {
    this.fieldName = paramRuntimePropertyInfo.getName();
    if (paramJAXBContextImpl.retainPropertyInfo)
      this.propertyInfo = paramRuntimePropertyInfo; 
  }
  
  public RuntimePropertyInfo getInfo() { return this.propertyInfo; }
  
  public void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject) throws SAXException, AccessorException, IOException, XMLStreamException {}
  
  public void serializeURIs(BeanT paramBeanT, XMLSerializer paramXMLSerializer) throws SAXException, AccessorException {}
  
  public boolean hasSerializeURIAction() { return false; }
  
  public Accessor getElementPropertyAccessor(String paramString1, String paramString2) { return null; }
  
  public void wrapUp() {}
  
  public boolean isHiddenByOverride() { return this.hiddenByOverride; }
  
  public void setHiddenByOverride(boolean paramBoolean) { this.hiddenByOverride = paramBoolean; }
  
  public String getFieldName() { return this.fieldName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\PropertyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */