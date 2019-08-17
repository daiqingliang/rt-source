package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import com.sun.xml.internal.bind.v2.runtime.reflect.NullSafeAccessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DefaultValueLoaderDecorator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TextLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

abstract class ArrayElementProperty<BeanT, ListT, ItemT> extends ArrayERProperty<BeanT, ListT, ItemT> {
  private final Map<Class, TagAndType> typeMap = new HashMap();
  
  private Map<TypeRef<Type, Class>, JaxBeanInfo> refs = new HashMap();
  
  protected RuntimeElementPropertyInfo prop;
  
  private final Name nillableTagName;
  
  protected ArrayElementProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeElementPropertyInfo paramRuntimeElementPropertyInfo) {
    super(paramJAXBContextImpl, paramRuntimeElementPropertyInfo, paramRuntimeElementPropertyInfo.getXmlName(), paramRuntimeElementPropertyInfo.isCollectionNillable());
    this.prop = paramRuntimeElementPropertyInfo;
    List list = paramRuntimeElementPropertyInfo.getTypes();
    Name name = null;
    for (RuntimeTypeRef runtimeTypeRef : list) {
      Class clazz = (Class)runtimeTypeRef.getTarget().getType();
      if (clazz.isPrimitive())
        clazz = (Class)RuntimeUtil.primitiveToBox.get(clazz); 
      JaxBeanInfo jaxBeanInfo = paramJAXBContextImpl.getOrCreate(runtimeTypeRef.getTarget());
      TagAndType tagAndType = new TagAndType(paramJAXBContextImpl.nameBuilder.createElementName(runtimeTypeRef.getTagName()), jaxBeanInfo);
      this.typeMap.put(clazz, tagAndType);
      this.refs.put(runtimeTypeRef, jaxBeanInfo);
      if (runtimeTypeRef.isNillable() && name == null)
        name = tagAndType.tagName; 
    } 
    this.nillableTagName = name;
  }
  
  public void wrapUp() {
    super.wrapUp();
    this.refs = null;
    this.prop = null;
  }
  
  protected void serializeListBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, ListT paramListT) throws IOException, XMLStreamException, SAXException, AccessorException {
    ListIterator listIterator = this.lister.iterator(paramListT, paramXMLSerializer);
    boolean bool = listIterator instanceof Lister.IDREFSIterator;
    while (listIterator.hasNext()) {
      try {
        Object object = listIterator.next();
        if (object != null) {
          Class clazz = object.getClass();
          if (bool)
            clazz = ((Lister.IDREFSIterator)listIterator).last().getClass(); 
          TagAndType tagAndType;
          for (tagAndType = (TagAndType)this.typeMap.get(clazz); tagAndType == null && clazz != null; tagAndType = (TagAndType)this.typeMap.get(clazz))
            clazz = clazz.getSuperclass(); 
          if (tagAndType == null) {
            paramXMLSerializer.startElement(((TagAndType)this.typeMap.values().iterator().next()).tagName, null);
            paramXMLSerializer.childAsXsiType(object, this.fieldName, paramXMLSerializer.grammar.getBeanInfo(Object.class), false);
          } else {
            paramXMLSerializer.startElement(tagAndType.tagName, null);
            serializeItem(tagAndType.beanInfo, object, paramXMLSerializer);
          } 
          paramXMLSerializer.endElement();
          continue;
        } 
        if (this.nillableTagName != null) {
          paramXMLSerializer.startElement(this.nillableTagName, null);
          paramXMLSerializer.writeXsiNilTrue();
          paramXMLSerializer.endElement();
        } 
      } catch (JAXBException jAXBException) {
        paramXMLSerializer.reportError(this.fieldName, jAXBException);
      } 
    } 
  }
  
  protected abstract void serializeItem(JaxBeanInfo paramJaxBeanInfo, ItemT paramItemT, XMLSerializer paramXMLSerializer) throws SAXException, AccessorException, IOException, XMLStreamException;
  
  public void createBodyUnmarshaller(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap) {
    int i = paramUnmarshallerChain.allocateOffset();
    ArrayERProperty.ReceiverImpl receiverImpl = new ArrayERProperty.ReceiverImpl(this, i);
    for (RuntimeTypeRef runtimeTypeRef : this.prop.getTypes()) {
      Name name = paramUnmarshallerChain.context.nameBuilder.createElementName(runtimeTypeRef.getTagName());
      Loader loader = createItemUnmarshaller(paramUnmarshallerChain, runtimeTypeRef);
      if (runtimeTypeRef.isNillable() || paramUnmarshallerChain.context.allNillable)
        loader = new XsiNilLoader.Array(loader); 
      if (runtimeTypeRef.getDefaultValue() != null)
        loader = new DefaultValueLoaderDecorator(loader, runtimeTypeRef.getDefaultValue()); 
      paramQNameMap.put(name, new ChildLoader(loader, receiverImpl));
    } 
  }
  
  public final PropertyKind getKind() { return PropertyKind.ELEMENT; }
  
  private Loader createItemUnmarshaller(UnmarshallerChain paramUnmarshallerChain, RuntimeTypeRef paramRuntimeTypeRef) {
    if (PropertyFactory.isLeaf(paramRuntimeTypeRef.getSource())) {
      Transducer transducer = paramRuntimeTypeRef.getTransducer();
      return new TextLoader(transducer);
    } 
    return ((JaxBeanInfo)this.refs.get(paramRuntimeTypeRef)).getLoader(paramUnmarshallerChain.context, true);
  }
  
  public Accessor getElementPropertyAccessor(String paramString1, String paramString2) {
    if (this.wrapperTagName != null) {
      if (this.wrapperTagName.equals(paramString1, paramString2))
        return this.acc; 
    } else {
      for (TagAndType tagAndType : this.typeMap.values()) {
        if (tagAndType.tagName.equals(paramString1, paramString2))
          return new NullSafeAccessor(this.acc, this.lister); 
      } 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\ArrayElementProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */