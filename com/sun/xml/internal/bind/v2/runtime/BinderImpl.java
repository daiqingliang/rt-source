package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.unmarshaller.InfosetScanner;
import com.sun.xml.internal.bind.v2.runtime.output.DOMOutput;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.InterningXmlVisitor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.SAXConnector;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import javax.xml.bind.Binder;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class BinderImpl<XmlNode> extends Binder<XmlNode> {
  private final JAXBContextImpl context;
  
  private UnmarshallerImpl unmarshaller;
  
  private MarshallerImpl marshaller;
  
  private final InfosetScanner<XmlNode> scanner;
  
  private final AssociationMap<XmlNode> assoc = new AssociationMap();
  
  BinderImpl(JAXBContextImpl paramJAXBContextImpl, InfosetScanner<XmlNode> paramInfosetScanner) {
    this.context = paramJAXBContextImpl;
    this.scanner = paramInfosetScanner;
  }
  
  private UnmarshallerImpl getUnmarshaller() {
    if (this.unmarshaller == null)
      this.unmarshaller = new UnmarshallerImpl(this.context, this.assoc); 
    return this.unmarshaller;
  }
  
  private MarshallerImpl getMarshaller() {
    if (this.marshaller == null)
      this.marshaller = new MarshallerImpl(this.context, this.assoc); 
    return this.marshaller;
  }
  
  public void marshal(Object paramObject, XmlNode paramXmlNode) throws JAXBException {
    if (paramXmlNode == null || paramObject == null)
      throw new IllegalArgumentException(); 
    getMarshaller().marshal(paramObject, createOutput(paramXmlNode));
  }
  
  private DOMOutput createOutput(XmlNode paramXmlNode) { return new DOMOutput((Node)paramXmlNode, this.assoc); }
  
  public Object updateJAXB(XmlNode paramXmlNode) throws JAXBException { return associativeUnmarshal(paramXmlNode, true, null); }
  
  public Object unmarshal(XmlNode paramXmlNode) throws JAXBException { return associativeUnmarshal(paramXmlNode, false, null); }
  
  public <T> JAXBElement<T> unmarshal(XmlNode paramXmlNode, Class<T> paramClass) throws JAXBException {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    return (JAXBElement)associativeUnmarshal(paramXmlNode, true, paramClass);
  }
  
  public void setSchema(Schema paramSchema) {
    getMarshaller().setSchema(paramSchema);
    getUnmarshaller().setSchema(paramSchema);
  }
  
  public Schema getSchema() { return getUnmarshaller().getSchema(); }
  
  private Object associativeUnmarshal(XmlNode paramXmlNode, boolean paramBoolean, Class paramClass) throws JAXBException {
    if (paramXmlNode == null)
      throw new IllegalArgumentException(); 
    JaxBeanInfo jaxBeanInfo = null;
    if (paramClass != null)
      jaxBeanInfo = this.context.getBeanInfo(paramClass, true); 
    InterningXmlVisitor interningXmlVisitor = new InterningXmlVisitor(getUnmarshaller().createUnmarshallerHandler(this.scanner, paramBoolean, jaxBeanInfo));
    this.scanner.setContentHandler(new SAXConnector(interningXmlVisitor, this.scanner.getLocator()));
    try {
      this.scanner.scan(paramXmlNode);
    } catch (SAXException sAXException) {
      throw this.unmarshaller.createUnmarshalException(sAXException);
    } 
    return interningXmlVisitor.getContext().getResult();
  }
  
  public XmlNode getXMLNode(Object paramObject) {
    if (paramObject == null)
      throw new IllegalArgumentException(); 
    AssociationMap.Entry entry = this.assoc.byPeer(paramObject);
    return (entry == null) ? null : (XmlNode)entry.element();
  }
  
  public Object getJAXBNode(XmlNode paramXmlNode) throws JAXBException {
    if (paramXmlNode == null)
      throw new IllegalArgumentException(); 
    AssociationMap.Entry entry = this.assoc.byElement(paramXmlNode);
    return (entry == null) ? null : ((entry.outer() != null) ? entry.outer() : entry.inner());
  }
  
  public XmlNode updateXML(Object paramObject) { return (XmlNode)updateXML(paramObject, getXMLNode(paramObject)); }
  
  public XmlNode updateXML(Object paramObject, XmlNode paramXmlNode) throws JAXBException {
    if (paramObject == null || paramXmlNode == null)
      throw new IllegalArgumentException(); 
    Element element = (Element)paramXmlNode;
    Node node1 = element.getNextSibling();
    Node node2 = element.getParentNode();
    node2.removeChild(element);
    JaxBeanInfo jaxBeanInfo = this.context.getBeanInfo(paramObject, true);
    if (!jaxBeanInfo.isElement())
      paramObject = new JAXBElement(new QName(element.getNamespaceURI(), element.getLocalName()), jaxBeanInfo.jaxbType, paramObject); 
    getMarshaller().marshal(paramObject, node2);
    Node node3 = node2.getLastChild();
    node2.removeChild(node3);
    node2.insertBefore(node3, node1);
    return (XmlNode)node3;
  }
  
  public void setEventHandler(ValidationEventHandler paramValidationEventHandler) throws JAXBException {
    getUnmarshaller().setEventHandler(paramValidationEventHandler);
    getMarshaller().setEventHandler(paramValidationEventHandler);
  }
  
  public ValidationEventHandler getEventHandler() { return getUnmarshaller().getEventHandler(); }
  
  public Object getProperty(String paramString) throws PropertyException {
    if (paramString == null)
      throw new IllegalArgumentException(Messages.NULL_PROPERTY_NAME.format(new Object[0])); 
    if (excludeProperty(paramString))
      throw new PropertyException(paramString); 
    Object object = null;
    PropertyException propertyException = null;
    try {
      return getMarshaller().getProperty(paramString);
    } catch (PropertyException propertyException1) {
      propertyException = propertyException1;
      try {
        return getUnmarshaller().getProperty(paramString);
      } catch (PropertyException propertyException1) {
        propertyException = propertyException1;
        propertyException.setStackTrace(Thread.currentThread().getStackTrace());
        throw propertyException;
      } 
    } 
  }
  
  public void setProperty(String paramString, Object paramObject) throws PropertyException {
    if (paramString == null)
      throw new IllegalArgumentException(Messages.NULL_PROPERTY_NAME.format(new Object[0])); 
    if (excludeProperty(paramString))
      throw new PropertyException(paramString, paramObject); 
    PropertyException propertyException = null;
    try {
      getMarshaller().setProperty(paramString, paramObject);
      return;
    } catch (PropertyException propertyException1) {
      propertyException = propertyException1;
      try {
        getUnmarshaller().setProperty(paramString, paramObject);
        return;
      } catch (PropertyException propertyException1) {
        propertyException = propertyException1;
        propertyException.setStackTrace(Thread.currentThread().getStackTrace());
        throw propertyException;
      } 
    } 
  }
  
  private boolean excludeProperty(String paramString) { return (paramString.equals("com.sun.xml.internal.bind.characterEscapeHandler") || paramString.equals("com.sun.xml.internal.bind.xmlDeclaration") || paramString.equals("com.sun.xml.internal.bind.xmlHeaders")); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\BinderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */