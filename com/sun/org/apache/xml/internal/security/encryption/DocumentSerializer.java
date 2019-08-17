package com.sun.org.apache.xml.internal.security.encryption;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DocumentSerializer extends AbstractSerializer {
  protected DocumentBuilderFactory dbf;
  
  public Node deserialize(byte[] paramArrayOfByte, Node paramNode) throws XMLEncryptionException {
    byte[] arrayOfByte = createContext(paramArrayOfByte, paramNode);
    return deserialize(paramNode, new InputSource(new ByteArrayInputStream(arrayOfByte)));
  }
  
  public Node deserialize(String paramString, Node paramNode) throws XMLEncryptionException {
    String str = createContext(paramString, paramNode);
    return deserialize(paramNode, new InputSource(new StringReader(str)));
  }
  
  private Node deserialize(Node paramNode, InputSource paramInputSource) throws XMLEncryptionException {
    try {
      if (this.dbf == null) {
        this.dbf = DocumentBuilderFactory.newInstance();
        this.dbf.setNamespaceAware(true);
        this.dbf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
        this.dbf.setAttribute("http://xml.org/sax/features/namespaces", Boolean.TRUE);
        this.dbf.setValidating(false);
      } 
      DocumentBuilder documentBuilder = this.dbf.newDocumentBuilder();
      Document document1 = documentBuilder.parse(paramInputSource);
      Document document2 = null;
      if (9 == paramNode.getNodeType()) {
        document2 = (Document)paramNode;
      } else {
        document2 = paramNode.getOwnerDocument();
      } 
      Element element = (Element)document2.importNode(document1.getDocumentElement(), true);
      DocumentFragment documentFragment = document2.createDocumentFragment();
      for (Node node = element.getFirstChild(); node != null; node = element.getFirstChild()) {
        element.removeChild(node);
        documentFragment.appendChild(node);
      } 
      return documentFragment;
    } catch (SAXException sAXException) {
      throw new XMLEncryptionException("empty", sAXException);
    } catch (ParserConfigurationException parserConfigurationException) {
      throw new XMLEncryptionException("empty", parserConfigurationException);
    } catch (IOException iOException) {
      throw new XMLEncryptionException("empty", iOException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\DocumentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */