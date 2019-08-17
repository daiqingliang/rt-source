package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.WildcardLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

class ArrayReferenceNodeProperty<BeanT, ListT, ItemT> extends ArrayERProperty<BeanT, ListT, ItemT> {
  private final QNameMap<JaxBeanInfo> expectedElements = new QNameMap();
  
  private final boolean isMixed;
  
  private final DomHandler domHandler;
  
  private final WildcardMode wcMode;
  
  public ArrayReferenceNodeProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeReferencePropertyInfo paramRuntimeReferencePropertyInfo) {
    super(paramJAXBContextImpl, paramRuntimeReferencePropertyInfo, paramRuntimeReferencePropertyInfo.getXmlName(), paramRuntimeReferencePropertyInfo.isCollectionNillable());
    for (RuntimeElement runtimeElement : paramRuntimeReferencePropertyInfo.getElements()) {
      JaxBeanInfo jaxBeanInfo = paramJAXBContextImpl.getOrCreate(runtimeElement);
      this.expectedElements.put(runtimeElement.getElementName().getNamespaceURI(), runtimeElement.getElementName().getLocalPart(), jaxBeanInfo);
    } 
    this.isMixed = paramRuntimeReferencePropertyInfo.isMixed();
    if (paramRuntimeReferencePropertyInfo.getWildcard() != null) {
      this.domHandler = (DomHandler)ClassFactory.create((Class)paramRuntimeReferencePropertyInfo.getDOMHandler());
      this.wcMode = paramRuntimeReferencePropertyInfo.getWildcard();
    } else {
      this.domHandler = null;
      this.wcMode = null;
    } 
  }
  
  protected final void serializeListBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, ListT paramListT) throws IOException, XMLStreamException, SAXException {
    ListIterator listIterator = this.lister.iterator(paramListT, paramXMLSerializer);
    while (listIterator.hasNext()) {
      try {
        Object object = listIterator.next();
        if (object != null) {
          if (this.isMixed && object.getClass() == String.class) {
            paramXMLSerializer.text((String)object, null);
            continue;
          } 
          JaxBeanInfo jaxBeanInfo = paramXMLSerializer.grammar.getBeanInfo(object, true);
          if (jaxBeanInfo.jaxbType == Object.class && this.domHandler != null) {
            paramXMLSerializer.writeDom(object, this.domHandler, paramBeanT, this.fieldName);
            continue;
          } 
          jaxBeanInfo.serializeRoot(object, paramXMLSerializer);
        } 
      } catch (JAXBException jAXBException) {
        paramXMLSerializer.reportError(this.fieldName, jAXBException);
      } 
    } 
  }
  
  public void createBodyUnmarshaller(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap) {
    int i = paramUnmarshallerChain.allocateOffset();
    ArrayERProperty.ReceiverImpl receiverImpl = new ArrayERProperty.ReceiverImpl(this, i);
    for (QNameMap.Entry entry : this.expectedElements.entrySet()) {
      JaxBeanInfo jaxBeanInfo = (JaxBeanInfo)entry.getValue();
      paramQNameMap.put(entry.nsUri, entry.localName, new ChildLoader(jaxBeanInfo.getLoader(paramUnmarshallerChain.context, true), receiverImpl));
    } 
    if (this.isMixed)
      paramQNameMap.put(TEXT_HANDLER, new ChildLoader(new MixedTextLoader(receiverImpl), null)); 
    if (this.domHandler != null)
      paramQNameMap.put(CATCH_ALL, new ChildLoader(new WildcardLoader(this.domHandler, this.wcMode), receiverImpl)); 
  }
  
  public PropertyKind getKind() { return PropertyKind.REFERENCE; }
  
  public Accessor getElementPropertyAccessor(String paramString1, String paramString2) {
    if (this.wrapperTagName != null) {
      if (this.wrapperTagName.equals(paramString1, paramString2))
        return this.acc; 
    } else if (this.expectedElements.containsKey(paramString1, paramString2)) {
      return this.acc;
    } 
    return null;
  }
  
  private static final class MixedTextLoader extends Loader {
    private final Receiver recv;
    
    public MixedTextLoader(Receiver param1Receiver) {
      super(true);
      this.recv = param1Receiver;
    }
    
    public void text(UnmarshallingContext.State param1State, CharSequence param1CharSequence) throws SAXException {
      if (param1CharSequence.length() != 0)
        this.recv.receive(param1State, param1CharSequence.toString()); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\ArrayReferenceNodeProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */