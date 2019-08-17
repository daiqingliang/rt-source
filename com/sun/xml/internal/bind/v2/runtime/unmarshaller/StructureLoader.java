package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.runtime.ClassBeanInfoImpl;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.property.AttributeProperty;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.property.StructureLoaderBuilder;
import com.sun.xml.internal.bind.v2.runtime.property.UnmarshallerChain;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public final class StructureLoader extends Loader {
  private final QNameMap<ChildLoader> childUnmarshallers = new QNameMap();
  
  private ChildLoader catchAll;
  
  private ChildLoader textHandler;
  
  private QNameMap<TransducedAccessor> attUnmarshallers;
  
  private Accessor<Object, Map<QName, String>> attCatchAll;
  
  private final JaxBeanInfo beanInfo;
  
  private int frameSize;
  
  private static final QNameMap<TransducedAccessor> EMPTY = new QNameMap();
  
  public StructureLoader(ClassBeanInfoImpl paramClassBeanInfoImpl) {
    super(true);
    this.beanInfo = paramClassBeanInfoImpl;
  }
  
  public void init(JAXBContextImpl paramJAXBContextImpl, ClassBeanInfoImpl paramClassBeanInfoImpl, Accessor<?, Map<QName, String>> paramAccessor) {
    UnmarshallerChain unmarshallerChain = new UnmarshallerChain(paramJAXBContextImpl);
    for (ClassBeanInfoImpl classBeanInfoImpl = paramClassBeanInfoImpl; classBeanInfoImpl != null; classBeanInfoImpl = classBeanInfoImpl.superClazz) {
      for (int i = classBeanInfoImpl.properties.length - 1; i >= 0; i--) {
        AttributeProperty attributeProperty;
        Property property = classBeanInfoImpl.properties[i];
        switch (property.getKind()) {
          case ATTRIBUTE:
            if (this.attUnmarshallers == null)
              this.attUnmarshallers = new QNameMap(); 
            attributeProperty = (AttributeProperty)property;
            this.attUnmarshallers.put(attributeProperty.attName.toQName(), attributeProperty.xacc);
            break;
          case ELEMENT:
          case REFERENCE:
          case MAP:
          case VALUE:
            property.buildChildElementUnmarshallers(unmarshallerChain, this.childUnmarshallers);
            break;
        } 
      } 
    } 
    this.frameSize = unmarshallerChain.getScopeSize();
    this.textHandler = (ChildLoader)this.childUnmarshallers.get(StructureLoaderBuilder.TEXT_HANDLER);
    this.catchAll = (ChildLoader)this.childUnmarshallers.get(StructureLoaderBuilder.CATCH_ALL);
    if (paramAccessor != null) {
      this.attCatchAll = paramAccessor;
      if (this.attUnmarshallers == null)
        this.attUnmarshallers = EMPTY; 
    } else {
      this.attCatchAll = null;
    } 
  }
  
  public void startElement(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException {
    UnmarshallingContext unmarshallingContext = paramState.getContext();
    assert !this.beanInfo.isImmutable();
    Object object = unmarshallingContext.getInnerPeer();
    if (object != null && this.beanInfo.jaxbType != object.getClass())
      object = null; 
    if (object != null)
      this.beanInfo.reset(object, unmarshallingContext); 
    if (object == null)
      object = unmarshallingContext.createInstance(this.beanInfo); 
    unmarshallingContext.recordInnerPeer(object);
    paramState.setTarget(object);
    fireBeforeUnmarshal(this.beanInfo, object, paramState);
    unmarshallingContext.startScope(this.frameSize);
    if (this.attUnmarshallers != null) {
      Attributes attributes = paramTagName.atts;
      for (byte b = 0; b < attributes.getLength(); b++) {
        String str1 = attributes.getURI(b);
        String str2 = attributes.getLocalName(b);
        if ("".equals(str2))
          str2 = attributes.getQName(b); 
        String str3 = attributes.getValue(b);
        TransducedAccessor transducedAccessor = (TransducedAccessor)this.attUnmarshallers.get(str1, str2);
        try {
          if (transducedAccessor != null) {
            transducedAccessor.parse(object, str3);
          } else if (this.attCatchAll != null) {
            String str = attributes.getQName(b);
            if (!attributes.getURI(b).equals("http://www.w3.org/2001/XMLSchema-instance")) {
              String str4;
              Object object1 = paramState.getTarget();
              Map map = (Map)this.attCatchAll.get(object1);
              if (map == null) {
                if (this.attCatchAll.valueType.isAssignableFrom(HashMap.class)) {
                  map = new HashMap();
                } else {
                  unmarshallingContext.handleError(Messages.UNABLE_TO_CREATE_MAP.format(new Object[] { this.attCatchAll.valueType }));
                  return;
                } 
                this.attCatchAll.set(object1, map);
              } 
              int i = str.indexOf(':');
              if (i < 0) {
                str4 = "";
              } else {
                str4 = str.substring(0, i);
              } 
              map.put(new QName(str1, str2, str4), str3);
            } 
          } 
        } catch (AccessorException accessorException) {
          handleGenericException(accessorException, true);
        } 
      } 
    } 
  }
  
  public void childElement(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException {
    ChildLoader childLoader = (ChildLoader)this.childUnmarshallers.get(paramTagName.uri, paramTagName.local);
    if (childLoader == null) {
      childLoader = this.catchAll;
      if (childLoader == null) {
        super.childElement(paramState, paramTagName);
        return;
      } 
    } 
    paramState.setLoader(childLoader.loader);
    paramState.setReceiver(childLoader.receiver);
  }
  
  public Collection<QName> getExpectedChildElements() { return this.childUnmarshallers.keySet(); }
  
  public Collection<QName> getExpectedAttributes() { return this.attUnmarshallers.keySet(); }
  
  public void text(UnmarshallingContext.State paramState, CharSequence paramCharSequence) throws SAXException {
    if (this.textHandler != null)
      this.textHandler.loader.text(paramState, paramCharSequence); 
  }
  
  public void leaveElement(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException {
    paramState.getContext().endScope(this.frameSize);
    fireAfterUnmarshal(this.beanInfo, paramState.getTarget(), paramState.getPrev());
  }
  
  public JaxBeanInfo getBeanInfo() { return this.beanInfo; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\StructureLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */