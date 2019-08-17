package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.property.PropertyFactory;
import com.sun.xml.internal.bind.v2.runtime.property.UnmarshallerChain;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Discarder;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Intercepter;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class ElementBeanInfoImpl extends JaxBeanInfo<JAXBElement> {
  private Loader loader;
  
  private final Property property;
  
  private final QName tagName;
  
  public final Class expectedType;
  
  private final Class scope;
  
  private final Constructor<? extends JAXBElement> constructor;
  
  ElementBeanInfoImpl(JAXBContextImpl paramJAXBContextImpl, RuntimeElementInfo paramRuntimeElementInfo) {
    super(paramJAXBContextImpl, paramRuntimeElementInfo, paramRuntimeElementInfo.getType(), true, false, true);
    this.property = PropertyFactory.create(paramJAXBContextImpl, paramRuntimeElementInfo.getProperty());
    this.tagName = paramRuntimeElementInfo.getElementName();
    this.expectedType = (Class)Utils.REFLECTION_NAVIGATOR.erasure(paramRuntimeElementInfo.getContentInMemoryType());
    this.scope = (paramRuntimeElementInfo.getScope() == null) ? JAXBElement.GlobalScope.class : (Class)paramRuntimeElementInfo.getScope().getClazz();
    Class clazz = (Class)Utils.REFLECTION_NAVIGATOR.erasure(paramRuntimeElementInfo.getType());
    if (clazz == JAXBElement.class) {
      this.constructor = null;
    } else {
      try {
        this.constructor = clazz.getConstructor(new Class[] { this.expectedType });
      } catch (NoSuchMethodException noSuchMethodException) {
        NoSuchMethodError noSuchMethodError = new NoSuchMethodError("Failed to find the constructor for " + clazz + " with " + this.expectedType);
        noSuchMethodError.initCause(noSuchMethodException);
        throw noSuchMethodError;
      } 
    } 
  }
  
  protected ElementBeanInfoImpl(final JAXBContextImpl grammar) {
    super(paramJAXBContextImpl, null, JAXBElement.class, true, false, true);
    this.tagName = null;
    this.expectedType = null;
    this.scope = null;
    this.constructor = null;
    this.property = new Property<JAXBElement>() {
        public void reset(JAXBElement param1JAXBElement) { throw new UnsupportedOperationException(); }
        
        public void serializeBody(JAXBElement param1JAXBElement, XMLSerializer param1XMLSerializer, Object param1Object) throws SAXException, IOException, XMLStreamException {
          Class clazz = param1JAXBElement.getScope();
          if (param1JAXBElement.isGlobalScope())
            clazz = null; 
          QName qName = param1JAXBElement.getName();
          ElementBeanInfoImpl elementBeanInfoImpl = grammar.getElement(clazz, qName);
          if (elementBeanInfoImpl == null) {
            JaxBeanInfo jaxBeanInfo;
            try {
              jaxBeanInfo = grammar.getBeanInfo(param1JAXBElement.getDeclaredType(), true);
            } catch (JAXBException jAXBException) {
              param1XMLSerializer.reportError(null, jAXBException);
              return;
            } 
            Object object = param1JAXBElement.getValue();
            param1XMLSerializer.startElement(qName.getNamespaceURI(), qName.getLocalPart(), qName.getPrefix(), null);
            if (object == null) {
              param1XMLSerializer.writeXsiNilTrue();
            } else {
              param1XMLSerializer.childAsXsiType(object, "value", jaxBeanInfo, false);
            } 
            param1XMLSerializer.endElement();
          } else {
            try {
              elementBeanInfoImpl.property.serializeBody(param1JAXBElement, param1XMLSerializer, param1JAXBElement);
            } catch (AccessorException accessorException) {
              param1XMLSerializer.reportError(null, accessorException);
            } 
          } 
        }
        
        public void serializeURIs(JAXBElement param1JAXBElement, XMLSerializer param1XMLSerializer) throws SAXException, IOException, XMLStreamException {}
        
        public boolean hasSerializeURIAction() { return false; }
        
        public String getIdValue(JAXBElement param1JAXBElement) { return null; }
        
        public PropertyKind getKind() { return PropertyKind.ELEMENT; }
        
        public void buildChildElementUnmarshallers(UnmarshallerChain param1UnmarshallerChain, QNameMap<ChildLoader> param1QNameMap) {}
        
        public Accessor getElementPropertyAccessor(String param1String1, String param1String2) { throw new UnsupportedOperationException(); }
        
        public void wrapUp() {}
        
        public RuntimePropertyInfo getInfo() { return ElementBeanInfoImpl.this.property.getInfo(); }
        
        public boolean isHiddenByOverride() { return false; }
        
        public void setHiddenByOverride(boolean param1Boolean) { throw new UnsupportedOperationException("Not supported on jaxbelements."); }
        
        public String getFieldName() { return null; }
      };
  }
  
  public String getElementNamespaceURI(JAXBElement paramJAXBElement) { return paramJAXBElement.getName().getNamespaceURI(); }
  
  public String getElementLocalName(JAXBElement paramJAXBElement) { return paramJAXBElement.getName().getLocalPart(); }
  
  public Loader getLoader(JAXBContextImpl paramJAXBContextImpl, boolean paramBoolean) {
    if (this.loader == null) {
      UnmarshallerChain unmarshallerChain = new UnmarshallerChain(paramJAXBContextImpl);
      QNameMap qNameMap = new QNameMap();
      this.property.buildChildElementUnmarshallers(unmarshallerChain, qNameMap);
      if (qNameMap.size() == 1) {
        this.loader = new IntercepterLoader(((ChildLoader)qNameMap.getOne().getValue()).loader);
      } else {
        this.loader = Discarder.INSTANCE;
      } 
    } 
    return this.loader;
  }
  
  public final JAXBElement createInstance(UnmarshallingContext paramUnmarshallingContext) throws IllegalAccessException, InvocationTargetException, InstantiationException { return createInstanceFromValue(null); }
  
  public final JAXBElement createInstanceFromValue(Object paramObject) throws IllegalAccessException, InvocationTargetException, InstantiationException { return (this.constructor == null) ? new JAXBElement(this.tagName, this.expectedType, this.scope, paramObject) : (JAXBElement)this.constructor.newInstance(new Object[] { paramObject }); }
  
  public boolean reset(JAXBElement paramJAXBElement, UnmarshallingContext paramUnmarshallingContext) {
    paramJAXBElement.setValue(null);
    return true;
  }
  
  public String getId(JAXBElement paramJAXBElement, XMLSerializer paramXMLSerializer) {
    Object object = paramJAXBElement.getValue();
    return (object instanceof String) ? (String)object : null;
  }
  
  public void serializeBody(JAXBElement paramJAXBElement, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {
    try {
      this.property.serializeBody(paramJAXBElement, paramXMLSerializer, null);
    } catch (AccessorException accessorException) {
      paramXMLSerializer.reportError(null, accessorException);
    } 
  }
  
  public void serializeRoot(JAXBElement paramJAXBElement, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException { serializeBody(paramJAXBElement, paramXMLSerializer); }
  
  public void serializeAttributes(JAXBElement paramJAXBElement, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {}
  
  public void serializeURIs(JAXBElement paramJAXBElement, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {}
  
  public final Transducer<JAXBElement> getTransducer() { return null; }
  
  public void wrapUp() {
    super.wrapUp();
    this.property.wrapUp();
  }
  
  public void link(JAXBContextImpl paramJAXBContextImpl) {
    super.link(paramJAXBContextImpl);
    getLoader(paramJAXBContextImpl, true);
  }
  
  private final class IntercepterLoader extends Loader implements Intercepter {
    private final Loader core;
    
    public IntercepterLoader(Loader param1Loader) { this.core = param1Loader; }
    
    public final void startElement(UnmarshallingContext.State param1State, TagName param1TagName) throws SAXException {
      param1State.setLoader(this.core);
      param1State.setIntercepter(this);
      UnmarshallingContext unmarshallingContext = param1State.getContext();
      Object object = unmarshallingContext.getOuterPeer();
      if (object != null && ElementBeanInfoImpl.this.jaxbType != object.getClass())
        object = null; 
      if (object != null)
        ElementBeanInfoImpl.this.reset((JAXBElement)object, unmarshallingContext); 
      if (object == null)
        object = unmarshallingContext.createInstance(ElementBeanInfoImpl.this); 
      fireBeforeUnmarshal(ElementBeanInfoImpl.this, object, param1State);
      unmarshallingContext.recordOuterPeer(object);
      UnmarshallingContext.State state = param1State.getPrev();
      state.setBackup(state.getTarget());
      state.setTarget(object);
      this.core.startElement(param1State, param1TagName);
    }
    
    public Object intercept(UnmarshallingContext.State param1State, Object param1Object) throws SAXException {
      JAXBElement jAXBElement = (JAXBElement)param1State.getTarget();
      param1State.setTarget(param1State.getBackup());
      param1State.setBackup(null);
      if (param1State.isNil()) {
        jAXBElement.setNil(true);
        param1State.setNil(false);
      } 
      if (param1Object != null)
        jAXBElement.setValue(param1Object); 
      fireAfterUnmarshal(ElementBeanInfoImpl.this, jAXBElement, param1State);
      return jAXBElement;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\ElementBeanInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */