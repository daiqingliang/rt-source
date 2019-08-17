package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class SingleElementNodeProperty<BeanT, ValueT> extends PropertyImpl<BeanT> {
  private final Accessor<BeanT, ValueT> acc;
  
  private final boolean nillable;
  
  private final QName[] acceptedElements;
  
  private final Map<Class, TagAndType> typeNames = new HashMap();
  
  private RuntimeElementPropertyInfo prop;
  
  private final Name nullTagName;
  
  public SingleElementNodeProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeElementPropertyInfo paramRuntimeElementPropertyInfo) {
    super(paramJAXBContextImpl, paramRuntimeElementPropertyInfo);
    this.acc = paramRuntimeElementPropertyInfo.getAccessor().optimize(paramJAXBContextImpl);
    this.prop = paramRuntimeElementPropertyInfo;
    QName qName = null;
    boolean bool = false;
    this.acceptedElements = new QName[paramRuntimeElementPropertyInfo.getTypes().size()];
    for (byte b = 0; b < this.acceptedElements.length; b++)
      this.acceptedElements[b] = ((RuntimeTypeRef)paramRuntimeElementPropertyInfo.getTypes().get(b)).getTagName(); 
    for (RuntimeTypeRef runtimeTypeRef : paramRuntimeElementPropertyInfo.getTypes()) {
      JaxBeanInfo jaxBeanInfo = paramJAXBContextImpl.getOrCreate(runtimeTypeRef.getTarget());
      if (qName == null)
        qName = runtimeTypeRef.getTagName(); 
      this.typeNames.put(jaxBeanInfo.jaxbType, new TagAndType(paramJAXBContextImpl.nameBuilder.createElementName(runtimeTypeRef.getTagName()), jaxBeanInfo));
      bool |= runtimeTypeRef.isNillable();
    } 
    this.nullTagName = paramJAXBContextImpl.nameBuilder.createElementName(qName);
    this.nillable = bool;
  }
  
  public void wrapUp() {
    super.wrapUp();
    this.prop = null;
  }
  
  public void reset(BeanT paramBeanT) throws AccessorException { this.acc.set(paramBeanT, null); }
  
  public String getIdValue(BeanT paramBeanT) { return null; }
  
  public void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject) throws SAXException, AccessorException, IOException, XMLStreamException {
    Object object = this.acc.get(paramBeanT);
    if (object != null) {
      Class clazz = object.getClass();
      TagAndType tagAndType = (TagAndType)this.typeNames.get(clazz);
      if (tagAndType == null)
        for (Map.Entry entry : this.typeNames.entrySet()) {
          if (((Class)entry.getKey()).isAssignableFrom(clazz)) {
            tagAndType = (TagAndType)entry.getValue();
            break;
          } 
        }  
      boolean bool = (paramBeanT instanceof JAXBElement && ((JAXBElement)paramBeanT).isNil()) ? 1 : 0;
      if (tagAndType == null) {
        paramXMLSerializer.startElement(((TagAndType)this.typeNames.values().iterator().next()).tagName, null);
        paramXMLSerializer.childAsXsiType(object, this.fieldName, paramXMLSerializer.grammar.getBeanInfo(Object.class), (bool && this.nillable));
      } else {
        paramXMLSerializer.startElement(tagAndType.tagName, null);
        paramXMLSerializer.childAsXsiType(object, this.fieldName, tagAndType.beanInfo, (bool && this.nillable));
      } 
      paramXMLSerializer.endElement();
    } else if (this.nillable) {
      paramXMLSerializer.startElement(this.nullTagName, null);
      paramXMLSerializer.writeXsiNilTrue();
      paramXMLSerializer.endElement();
    } 
  }
  
  public void buildChildElementUnmarshallers(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap) {
    JAXBContextImpl jAXBContextImpl = paramUnmarshallerChain.context;
    for (TypeRef typeRef : this.prop.getTypes()) {
      JaxBeanInfo jaxBeanInfo = jAXBContextImpl.getOrCreate((RuntimeTypeInfo)typeRef.getTarget());
      Loader loader = jaxBeanInfo.getLoader(jAXBContextImpl, !Modifier.isFinal(jaxBeanInfo.jaxbType.getModifiers()));
      if (typeRef.getDefaultValue() != null)
        loader = new DefaultValueLoaderDecorator(loader, typeRef.getDefaultValue()); 
      if (this.nillable || paramUnmarshallerChain.context.allNillable)
        loader = new XsiNilLoader.Single(loader, this.acc); 
      paramQNameMap.put(typeRef.getTagName(), new ChildLoader(loader, this.acc));
    } 
  }
  
  public PropertyKind getKind() { return PropertyKind.ELEMENT; }
  
  public Accessor getElementPropertyAccessor(String paramString1, String paramString2) {
    for (QName qName : this.acceptedElements) {
      if (qName.getNamespaceURI().equals(paramString1) && qName.getLocalPart().equals(paramString2))
        return this.acc; 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\SingleElementNodeProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */