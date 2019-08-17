package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListTransducedAccessorImpl;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LeafPropertyLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class ListElementProperty<BeanT, ListT, ItemT> extends ArrayProperty<BeanT, ListT, ItemT> {
  private final Name tagName;
  
  private final String defaultValue;
  
  private final TransducedAccessor<BeanT> xacc;
  
  public ListElementProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeElementPropertyInfo paramRuntimeElementPropertyInfo) {
    super(paramJAXBContextImpl, paramRuntimeElementPropertyInfo);
    assert paramRuntimeElementPropertyInfo.isValueList();
    assert paramRuntimeElementPropertyInfo.getTypes().size() == 1;
    RuntimeTypeRef runtimeTypeRef = (RuntimeTypeRef)paramRuntimeElementPropertyInfo.getTypes().get(0);
    this.tagName = paramJAXBContextImpl.nameBuilder.createElementName(runtimeTypeRef.getTagName());
    this.defaultValue = runtimeTypeRef.getDefaultValue();
    Transducer transducer = runtimeTypeRef.getTransducer();
    this.xacc = new ListTransducedAccessorImpl(transducer, this.acc, this.lister);
  }
  
  public PropertyKind getKind() { return PropertyKind.ELEMENT; }
  
  public void buildChildElementUnmarshallers(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap) {
    LeafPropertyLoader leafPropertyLoader = new LeafPropertyLoader(this.xacc);
    DefaultValueLoaderDecorator defaultValueLoaderDecorator = new DefaultValueLoaderDecorator(leafPropertyLoader, this.defaultValue);
    paramQNameMap.put(this.tagName, new ChildLoader(defaultValueLoaderDecorator, null));
  }
  
  public void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject) throws SAXException, AccessorException, IOException, XMLStreamException {
    Object object = this.acc.get(paramBeanT);
    if (object != null)
      if (this.xacc.useNamespace()) {
        paramXMLSerializer.startElement(this.tagName, null);
        this.xacc.declareNamespace(paramBeanT, paramXMLSerializer);
        paramXMLSerializer.endNamespaceDecls(object);
        paramXMLSerializer.endAttributes();
        this.xacc.writeText(paramXMLSerializer, paramBeanT, this.fieldName);
        paramXMLSerializer.endElement();
      } else {
        this.xacc.writeLeafElement(paramXMLSerializer, this.tagName, paramBeanT, this.fieldName);
      }  
  }
  
  public Accessor getElementPropertyAccessor(String paramString1, String paramString2) { return (this.tagName != null && this.tagName.equals(paramString1, paramString2)) ? this.acc : null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\ListElementProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */