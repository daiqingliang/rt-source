package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LeafPropertyLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LeafPropertyXsiLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.lang.reflect.Modifier;
import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class SingleElementLeafProperty<BeanT> extends PropertyImpl<BeanT> {
  private final Name tagName;
  
  private final boolean nillable;
  
  private final Accessor acc;
  
  private final String defaultValue;
  
  private final TransducedAccessor<BeanT> xacc;
  
  private final boolean improvedXsiTypeHandling;
  
  private final boolean idRef;
  
  public SingleElementLeafProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeElementPropertyInfo paramRuntimeElementPropertyInfo) {
    super(paramJAXBContextImpl, paramRuntimeElementPropertyInfo);
    RuntimeTypeRef runtimeTypeRef = (RuntimeTypeRef)paramRuntimeElementPropertyInfo.getTypes().get(0);
    this.tagName = paramJAXBContextImpl.nameBuilder.createElementName(runtimeTypeRef.getTagName());
    assert this.tagName != null;
    this.nillable = runtimeTypeRef.isNillable();
    this.defaultValue = runtimeTypeRef.getDefaultValue();
    this.acc = paramRuntimeElementPropertyInfo.getAccessor().optimize(paramJAXBContextImpl);
    this.xacc = TransducedAccessor.get(paramJAXBContextImpl, runtimeTypeRef);
    assert this.xacc != null;
    this.improvedXsiTypeHandling = paramJAXBContextImpl.improvedXsiTypeHandling;
    this.idRef = (runtimeTypeRef.getSource().id() == ID.IDREF);
  }
  
  public void reset(BeanT paramBeanT) throws AccessorException { this.acc.set(paramBeanT, null); }
  
  public String getIdValue(BeanT paramBeanT) throws AccessorException, SAXException { return this.xacc.print(paramBeanT).toString(); }
  
  public void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject) throws SAXException, AccessorException, IOException, XMLStreamException {
    boolean bool = this.xacc.hasValue(paramBeanT);
    Object object = null;
    try {
      object = this.acc.getUnadapted(paramBeanT);
    } catch (AccessorException accessorException) {}
    Class clazz = this.acc.getValueType();
    if (xsiTypeNeeded(paramBeanT, paramXMLSerializer, object, clazz)) {
      paramXMLSerializer.startElement(this.tagName, paramObject);
      paramXMLSerializer.childAsXsiType(object, this.fieldName, paramXMLSerializer.grammar.getBeanInfo(clazz), false);
      paramXMLSerializer.endElement();
    } else if (bool) {
      this.xacc.writeLeafElement(paramXMLSerializer, this.tagName, paramBeanT, this.fieldName);
    } else if (this.nillable) {
      paramXMLSerializer.startElement(this.tagName, null);
      paramXMLSerializer.writeXsiNilTrue();
      paramXMLSerializer.endElement();
    } 
  }
  
  private boolean xsiTypeNeeded(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject, Class paramClass) { return !this.improvedXsiTypeHandling ? false : (this.acc.isAdapted() ? false : ((paramObject == null) ? false : (paramObject.getClass().equals(paramClass) ? false : (this.idRef ? false : (paramClass.isPrimitive() ? false : ((this.acc.isValueTypeAbstractable() || isNillableAbstract(paramBeanT, paramXMLSerializer.grammar, paramObject, paramClass)))))))); }
  
  private boolean isNillableAbstract(BeanT paramBeanT, JAXBContextImpl paramJAXBContextImpl, Object paramObject, Class paramClass) {
    if (!this.nillable)
      return false; 
    if (paramClass != Object.class)
      return false; 
    if (paramBeanT.getClass() != JAXBElement.class)
      return false; 
    JAXBElement jAXBElement = (JAXBElement)paramBeanT;
    Class clazz1 = paramObject.getClass();
    Class clazz2 = jAXBElement.getDeclaredType();
    return clazz2.equals(clazz1) ? false : (!clazz2.isAssignableFrom(clazz1) ? false : (!Modifier.isAbstract(clazz2.getModifiers()) ? false : this.acc.isAbstractable(clazz2)));
  }
  
  public void buildChildElementUnmarshallers(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap) {
    LeafPropertyXsiLoader leafPropertyXsiLoader = new LeafPropertyLoader(this.xacc);
    if (this.defaultValue != null)
      leafPropertyXsiLoader = new DefaultValueLoaderDecorator(leafPropertyXsiLoader, this.defaultValue); 
    if (this.nillable || paramUnmarshallerChain.context.allNillable)
      leafPropertyXsiLoader = new XsiNilLoader.Single(leafPropertyXsiLoader, this.acc); 
    if (this.improvedXsiTypeHandling)
      leafPropertyXsiLoader = new LeafPropertyXsiLoader(leafPropertyXsiLoader, this.xacc, this.acc); 
    paramQNameMap.put(this.tagName, new ChildLoader(leafPropertyXsiLoader, null));
  }
  
  public PropertyKind getKind() { return PropertyKind.ELEMENT; }
  
  public Accessor getElementPropertyAccessor(String paramString1, String paramString2) { return this.tagName.equals(paramString1, paramString2) ? this.acc : null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\SingleElementLeafProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */