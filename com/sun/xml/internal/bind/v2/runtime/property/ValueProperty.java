package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeValuePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ValuePropertyLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class ValueProperty<BeanT> extends PropertyImpl<BeanT> {
  private final TransducedAccessor<BeanT> xacc;
  
  private final Accessor<BeanT, ?> acc;
  
  public ValueProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeValuePropertyInfo paramRuntimeValuePropertyInfo) {
    super(paramJAXBContextImpl, paramRuntimeValuePropertyInfo);
    this.xacc = TransducedAccessor.get(paramJAXBContextImpl, paramRuntimeValuePropertyInfo);
    this.acc = paramRuntimeValuePropertyInfo.getAccessor();
  }
  
  public final void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject) throws SAXException, AccessorException, IOException, XMLStreamException {
    if (this.xacc.hasValue(paramBeanT))
      this.xacc.writeText(paramXMLSerializer, paramBeanT, this.fieldName); 
  }
  
  public void serializeURIs(BeanT paramBeanT, XMLSerializer paramXMLSerializer) throws SAXException, AccessorException { this.xacc.declareNamespace(paramBeanT, paramXMLSerializer); }
  
  public boolean hasSerializeURIAction() { return this.xacc.useNamespace(); }
  
  public void buildChildElementUnmarshallers(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap) { paramQNameMap.put(StructureLoaderBuilder.TEXT_HANDLER, new ChildLoader(new ValuePropertyLoader(this.xacc), null)); }
  
  public PropertyKind getKind() { return PropertyKind.VALUE; }
  
  public void reset(BeanT paramBeanT) throws AccessorException { this.acc.set(paramBeanT, null); }
  
  public String getIdValue(BeanT paramBeanT) throws AccessorException, SAXException { return this.xacc.print(paramBeanT).toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\ValueProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */