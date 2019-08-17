package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMUtil {
  private static DocumentBuilder db;
  
  public static Document createDom() {
    synchronized (DOMUtil.class) {
      if (db == null)
        try {
          DocumentBuilderFactory documentBuilderFactory = XmlUtil.newDocumentBuilderFactory();
          db = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException parserConfigurationException) {
          throw new FactoryConfigurationError(parserConfigurationException);
        }  
      return db.newDocument();
    } 
  }
  
  public static void serializeNode(Element paramElement, XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    writeTagWithAttributes(paramElement, paramXMLStreamWriter);
    if (paramElement.hasChildNodes()) {
      NodeList nodeList = paramElement.getChildNodes();
      for (byte b = 0; b < nodeList.getLength(); b++) {
        Node node = nodeList.item(b);
        switch (node.getNodeType()) {
          case 7:
            paramXMLStreamWriter.writeProcessingInstruction(node.getNodeValue());
            break;
          case 4:
            paramXMLStreamWriter.writeCData(node.getNodeValue());
            break;
          case 8:
            paramXMLStreamWriter.writeComment(node.getNodeValue());
            break;
          case 3:
            paramXMLStreamWriter.writeCharacters(node.getNodeValue());
            break;
          case 1:
            serializeNode((Element)node, paramXMLStreamWriter);
            break;
        } 
      } 
    } 
    paramXMLStreamWriter.writeEndElement();
  }
  
  public static void writeTagWithAttributes(Element paramElement, XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    String str1 = fixNull(paramElement.getPrefix());
    String str2 = fixNull(paramElement.getNamespaceURI());
    String str3 = (paramElement.getLocalName() == null) ? paramElement.getNodeName() : paramElement.getLocalName();
    boolean bool = isPrefixDeclared(paramXMLStreamWriter, str2, str1);
    paramXMLStreamWriter.writeStartElement(str1, str3, str2);
    if (paramElement.hasAttributes()) {
      NamedNodeMap namedNodeMap = paramElement.getAttributes();
      int i = namedNodeMap.getLength();
      for (byte b = 0; b < i; b++) {
        Node node = namedNodeMap.item(b);
        String str = fixNull(node.getNamespaceURI());
        if (str.equals("http://www.w3.org/2000/xmlns/")) {
          String str4 = node.getLocalName().equals("xmlns") ? "" : node.getLocalName();
          if (str4.equals(str1) && node.getNodeValue().equals(str2))
            bool = true; 
          if (str4.equals("")) {
            paramXMLStreamWriter.writeDefaultNamespace(node.getNodeValue());
          } else {
            paramXMLStreamWriter.setPrefix(node.getLocalName(), node.getNodeValue());
            paramXMLStreamWriter.writeNamespace(node.getLocalName(), node.getNodeValue());
          } 
        } 
      } 
    } 
    if (!bool)
      paramXMLStreamWriter.writeNamespace(str1, str2); 
    if (paramElement.hasAttributes()) {
      NamedNodeMap namedNodeMap = paramElement.getAttributes();
      int i = namedNodeMap.getLength();
      for (byte b = 0; b < i; b++) {
        Node node = namedNodeMap.item(b);
        String str4 = fixNull(node.getPrefix());
        String str5 = fixNull(node.getNamespaceURI());
        if (!str5.equals("http://www.w3.org/2000/xmlns/")) {
          String str = node.getLocalName();
          if (str == null)
            str = node.getNodeName(); 
          boolean bool1 = isPrefixDeclared(paramXMLStreamWriter, str5, str4);
          if (!str4.equals("") && !bool1) {
            paramXMLStreamWriter.setPrefix(node.getLocalName(), node.getNodeValue());
            paramXMLStreamWriter.writeNamespace(str4, str5);
          } 
          paramXMLStreamWriter.writeAttribute(str4, str5, str, node.getNodeValue());
        } 
      } 
    } 
  }
  
  private static boolean isPrefixDeclared(XMLStreamWriter paramXMLStreamWriter, String paramString1, String paramString2) {
    boolean bool = false;
    NamespaceContext namespaceContext = paramXMLStreamWriter.getNamespaceContext();
    Iterator iterator = namespaceContext.getPrefixes(paramString1);
    while (iterator.hasNext()) {
      if (paramString2.equals(iterator.next())) {
        bool = true;
        break;
      } 
    } 
    return bool;
  }
  
  public static Element getFirstChild(Element paramElement, String paramString1, String paramString2) {
    for (Node node = paramElement.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node.getNodeType() == 1) {
        Element element = (Element)node;
        if (element.getLocalName().equals(paramString2) && element.getNamespaceURI().equals(paramString1))
          return element; 
      } 
    } 
    return null;
  }
  
  @NotNull
  private static String fixNull(@Nullable String paramString) { return (paramString == null) ? "" : paramString; }
  
  @Nullable
  public static Element getFirstElementChild(Node paramNode) {
    for (Node node = paramNode.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node.getNodeType() == 1)
        return (Element)node; 
    } 
    return null;
  }
  
  @NotNull
  public static List<Element> getChildElements(Node paramNode) {
    ArrayList arrayList = new ArrayList();
    for (Node node = paramNode.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node.getNodeType() == 1)
        arrayList.add((Element)node); 
    } 
    return arrayList;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\DOMUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */