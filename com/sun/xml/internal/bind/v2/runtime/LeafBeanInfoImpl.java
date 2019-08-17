package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeLeafInfo;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TextLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiTypeLoader;
import java.io.IOException;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class LeafBeanInfoImpl<BeanT> extends JaxBeanInfo<BeanT> {
  private final Loader loader;
  
  private final Loader loaderWithSubst;
  
  private final Transducer<BeanT> xducer;
  
  private final Name tagName;
  
  public LeafBeanInfoImpl(JAXBContextImpl paramJAXBContextImpl, RuntimeLeafInfo paramRuntimeLeafInfo) {
    super(paramJAXBContextImpl, paramRuntimeLeafInfo, paramRuntimeLeafInfo.getClazz(), paramRuntimeLeafInfo.getTypeNames(), paramRuntimeLeafInfo.isElement(), true, false);
    this.xducer = paramRuntimeLeafInfo.getTransducer();
    this.loader = new TextLoader(this.xducer);
    this.loaderWithSubst = new XsiTypeLoader(this);
    if (isElement()) {
      this.tagName = paramJAXBContextImpl.nameBuilder.createElementName(paramRuntimeLeafInfo.getElementName());
    } else {
      this.tagName = null;
    } 
  }
  
  public QName getTypeName(BeanT paramBeanT) {
    QName qName = this.xducer.getTypeName(paramBeanT);
    return (qName != null) ? qName : super.getTypeName(paramBeanT);
  }
  
  public final String getElementNamespaceURI(BeanT paramBeanT) { return this.tagName.nsUri; }
  
  public final String getElementLocalName(BeanT paramBeanT) { return this.tagName.localName; }
  
  public BeanT createInstance(UnmarshallingContext paramUnmarshallingContext) { throw new UnsupportedOperationException(); }
  
  public final boolean reset(BeanT paramBeanT, UnmarshallingContext paramUnmarshallingContext) { return false; }
  
  public final String getId(BeanT paramBeanT, XMLSerializer paramXMLSerializer) { return null; }
  
  public final void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {
    try {
      this.xducer.writeText(paramXMLSerializer, paramBeanT, null);
    } catch (AccessorException accessorException) {
      paramXMLSerializer.reportError(null, accessorException);
    } 
  }
  
  public final void serializeAttributes(BeanT paramBeanT, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {}
  
  public final void serializeRoot(BeanT paramBeanT, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {
    if (this.tagName == null) {
      paramXMLSerializer.reportError(new ValidationEventImpl(1, Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(new Object[] { paramBeanT.getClass().getName() }, ), null, null));
    } else {
      paramXMLSerializer.startElement(this.tagName, paramBeanT);
      paramXMLSerializer.childAsSoleContent(paramBeanT, null);
      paramXMLSerializer.endElement();
    } 
  }
  
  public final void serializeURIs(BeanT paramBeanT, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {
    if (this.xducer.useNamespace())
      try {
        this.xducer.declareNamespace(paramBeanT, paramXMLSerializer);
      } catch (AccessorException accessorException) {
        paramXMLSerializer.reportError(null, accessorException);
      }  
  }
  
  public final Loader getLoader(JAXBContextImpl paramJAXBContextImpl, boolean paramBoolean) { return paramBoolean ? this.loaderWithSubst : this.loader; }
  
  public Transducer<BeanT> getTransducer() { return this.xducer; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\LeafBeanInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */