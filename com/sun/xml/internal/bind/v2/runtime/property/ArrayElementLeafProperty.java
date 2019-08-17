package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class ArrayElementLeafProperty<BeanT, ListT, ItemT> extends ArrayElementProperty<BeanT, ListT, ItemT> {
  private final Transducer<ItemT> xducer;
  
  public ArrayElementLeafProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeElementPropertyInfo paramRuntimeElementPropertyInfo) {
    super(paramJAXBContextImpl, paramRuntimeElementPropertyInfo);
    assert paramRuntimeElementPropertyInfo.getTypes().size() == 1;
    this.xducer = ((RuntimeTypeRef)paramRuntimeElementPropertyInfo.getTypes().get(0)).getTransducer();
    assert this.xducer != null;
  }
  
  public void serializeItem(JaxBeanInfo paramJaxBeanInfo, ItemT paramItemT, XMLSerializer paramXMLSerializer) throws SAXException, AccessorException, IOException, XMLStreamException {
    this.xducer.declareNamespace(paramItemT, paramXMLSerializer);
    paramXMLSerializer.endNamespaceDecls(paramItemT);
    paramXMLSerializer.endAttributes();
    this.xducer.writeText(paramXMLSerializer, paramItemT, this.fieldName);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\ArrayElementLeafProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */