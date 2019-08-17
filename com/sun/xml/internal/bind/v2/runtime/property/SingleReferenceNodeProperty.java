package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.ElementBeanInfoImpl;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.WildcardLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class SingleReferenceNodeProperty<BeanT, ValueT> extends PropertyImpl<BeanT> {
  private final Accessor<BeanT, ValueT> acc;
  
  private final QNameMap<JaxBeanInfo> expectedElements = new QNameMap();
  
  private final DomHandler domHandler;
  
  private final WildcardMode wcMode;
  
  public SingleReferenceNodeProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeReferencePropertyInfo paramRuntimeReferencePropertyInfo) {
    super(paramJAXBContextImpl, paramRuntimeReferencePropertyInfo);
    this.acc = paramRuntimeReferencePropertyInfo.getAccessor().optimize(paramJAXBContextImpl);
    for (RuntimeElement runtimeElement : paramRuntimeReferencePropertyInfo.getElements())
      this.expectedElements.put(runtimeElement.getElementName(), paramJAXBContextImpl.getOrCreate(runtimeElement)); 
    if (paramRuntimeReferencePropertyInfo.getWildcard() != null) {
      this.domHandler = (DomHandler)ClassFactory.create((Class)paramRuntimeReferencePropertyInfo.getDOMHandler());
      this.wcMode = paramRuntimeReferencePropertyInfo.getWildcard();
    } else {
      this.domHandler = null;
      this.wcMode = null;
    } 
  }
  
  public void reset(BeanT paramBeanT) throws AccessorException { this.acc.set(paramBeanT, null); }
  
  public String getIdValue(BeanT paramBeanT) { return null; }
  
  public void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject) throws SAXException, AccessorException, IOException, XMLStreamException {
    Object object = this.acc.get(paramBeanT);
    if (object != null)
      try {
        JaxBeanInfo jaxBeanInfo = paramXMLSerializer.grammar.getBeanInfo(object, true);
        if (jaxBeanInfo.jaxbType == Object.class && this.domHandler != null) {
          paramXMLSerializer.writeDom(object, this.domHandler, paramBeanT, this.fieldName);
        } else {
          jaxBeanInfo.serializeRoot(object, paramXMLSerializer);
        } 
      } catch (JAXBException jAXBException) {
        paramXMLSerializer.reportError(this.fieldName, jAXBException);
      }  
  }
  
  public void buildChildElementUnmarshallers(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap) {
    for (QNameMap.Entry entry : this.expectedElements.entrySet())
      paramQNameMap.put(entry.nsUri, entry.localName, new ChildLoader(((JaxBeanInfo)entry.getValue()).getLoader(paramUnmarshallerChain.context, true), this.acc)); 
    if (this.domHandler != null)
      paramQNameMap.put(CATCH_ALL, new ChildLoader(new WildcardLoader(this.domHandler, this.wcMode), this.acc)); 
  }
  
  public PropertyKind getKind() { return PropertyKind.REFERENCE; }
  
  public Accessor getElementPropertyAccessor(String paramString1, String paramString2) {
    JaxBeanInfo jaxBeanInfo = (JaxBeanInfo)this.expectedElements.get(paramString1, paramString2);
    if (jaxBeanInfo != null) {
      if (jaxBeanInfo instanceof ElementBeanInfoImpl) {
        final ElementBeanInfoImpl ebi = (ElementBeanInfoImpl)jaxBeanInfo;
        return new Accessor<BeanT, Object>(elementBeanInfoImpl.expectedType) {
            public Object get(BeanT param1BeanT) throws AccessorException {
              Object object = SingleReferenceNodeProperty.this.acc.get(param1BeanT);
              return (object instanceof JAXBElement) ? ((JAXBElement)object).getValue() : object;
            }
            
            public void set(BeanT param1BeanT, Object param1Object) throws AccessorException {
              if (param1Object != null)
                try {
                  param1Object = ebi.createInstanceFromValue(param1Object);
                } catch (IllegalAccessException illegalAccessException) {
                  throw new AccessorException(illegalAccessException);
                } catch (InvocationTargetException invocationTargetException) {
                  throw new AccessorException(invocationTargetException);
                } catch (InstantiationException instantiationException) {
                  throw new AccessorException(instantiationException);
                }  
              SingleReferenceNodeProperty.this.acc.set(param1BeanT, param1Object);
            }
          };
      } 
      return this.acc;
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\SingleReferenceNodeProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */