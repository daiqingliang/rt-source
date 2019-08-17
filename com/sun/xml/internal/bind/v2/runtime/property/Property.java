package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public interface Property<BeanT> extends StructureLoaderBuilder {
  void reset(BeanT paramBeanT) throws AccessorException;
  
  void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject) throws SAXException, AccessorException, IOException, XMLStreamException;
  
  void serializeURIs(BeanT paramBeanT, XMLSerializer paramXMLSerializer) throws SAXException, AccessorException;
  
  boolean hasSerializeURIAction();
  
  String getIdValue(BeanT paramBeanT) throws AccessorException, SAXException;
  
  PropertyKind getKind();
  
  Accessor getElementPropertyAccessor(String paramString1, String paramString2);
  
  void wrapUp();
  
  RuntimePropertyInfo getInfo();
  
  boolean isHiddenByOverride();
  
  void setHiddenByOverride(boolean paramBoolean);
  
  String getFieldName();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\Property.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */