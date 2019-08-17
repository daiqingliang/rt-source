package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.DomLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiTypeLoader;
import java.io.IOException;
import javax.xml.bind.annotation.W3CDomHandler;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

final class AnyTypeBeanInfo extends JaxBeanInfo<Object> implements AttributeAccessor {
  private boolean nilIncluded = false;
  
  private static final W3CDomHandler domHandler = new W3CDomHandler();
  
  private static final DomLoader domLoader = new DomLoader(domHandler);
  
  private final XsiTypeLoader substLoader = new XsiTypeLoader(this);
  
  public AnyTypeBeanInfo(JAXBContextImpl paramJAXBContextImpl, RuntimeTypeInfo paramRuntimeTypeInfo) { super(paramJAXBContextImpl, paramRuntimeTypeInfo, Object.class, new QName("http://www.w3.org/2001/XMLSchema", "anyType"), false, true, false); }
  
  public String getElementNamespaceURI(Object paramObject) { throw new UnsupportedOperationException(); }
  
  public String getElementLocalName(Object paramObject) { throw new UnsupportedOperationException(); }
  
  public Object createInstance(UnmarshallingContext paramUnmarshallingContext) { throw new UnsupportedOperationException(); }
  
  public boolean reset(Object paramObject, UnmarshallingContext paramUnmarshallingContext) { return false; }
  
  public String getId(Object paramObject, XMLSerializer paramXMLSerializer) { return null; }
  
  public void serializeBody(Object paramObject, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {
    NodeList nodeList = ((Element)paramObject).getChildNodes();
    int i = nodeList.getLength();
    for (byte b = 0; b < i; b++) {
      Node node = nodeList.item(b);
      switch (node.getNodeType()) {
        case 3:
        case 4:
          paramXMLSerializer.text(node.getNodeValue(), null);
          break;
        case 1:
          paramXMLSerializer.writeDom((Element)node, domHandler, null, null);
          break;
      } 
    } 
  }
  
  public void serializeAttributes(Object paramObject, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {
    NamedNodeMap namedNodeMap = ((Element)paramObject).getAttributes();
    int i = namedNodeMap.getLength();
    for (byte b = 0; b < i; b++) {
      Attr attr = (Attr)namedNodeMap.item(b);
      String str1 = attr.getNamespaceURI();
      if (str1 == null)
        str1 = ""; 
      String str2 = attr.getLocalName();
      String str3 = attr.getName();
      if (str2 == null)
        str2 = str3; 
      if (str1.equals("http://www.w3.org/2001/XMLSchema-instance") && "nil".equals(str2))
        this.isNilIncluded = true; 
      if (!str3.startsWith("xmlns"))
        paramXMLSerializer.attribute(str1, str2, attr.getValue()); 
    } 
  }
  
  public void serializeRoot(Object paramObject, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException { paramXMLSerializer.reportError(new ValidationEventImpl(1, Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(new Object[] { paramObject.getClass().getName() }, ), null, null)); }
  
  public void serializeURIs(Object paramObject, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {
    NamedNodeMap namedNodeMap = ((Element)paramObject).getAttributes();
    int i = namedNodeMap.getLength();
    NamespaceContext2 namespaceContext2 = paramXMLSerializer.getNamespaceContext();
    for (byte b = 0; b < i; b++) {
      Attr attr = (Attr)namedNodeMap.item(b);
      if ("xmlns".equals(attr.getPrefix())) {
        namespaceContext2.force(attr.getValue(), attr.getLocalName());
      } else if ("xmlns".equals(attr.getName())) {
        if (paramObject instanceof Element) {
          namespaceContext2.declareNamespace(attr.getValue(), null, false);
        } else {
          namespaceContext2.force(attr.getValue(), "");
        } 
      } else {
        String str = attr.getNamespaceURI();
        if (str != null && str.length() > 0)
          namespaceContext2.declareNamespace(str, attr.getPrefix(), true); 
      } 
    } 
  }
  
  public Transducer<Object> getTransducer() { return null; }
  
  public Loader getLoader(JAXBContextImpl paramJAXBContextImpl, boolean paramBoolean) { return paramBoolean ? this.substLoader : domLoader; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\AnyTypeBeanInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */