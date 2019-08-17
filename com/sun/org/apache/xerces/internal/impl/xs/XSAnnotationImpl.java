package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import java.io.IOException;
import java.io.StringReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XSAnnotationImpl implements XSAnnotation {
  private String fData = null;
  
  private SchemaGrammar fGrammar = null;
  
  public XSAnnotationImpl(String paramString, SchemaGrammar paramSchemaGrammar) {
    this.fData = paramString;
    this.fGrammar = paramSchemaGrammar;
  }
  
  public boolean writeAnnotation(Object paramObject, short paramShort) {
    if (paramShort == 1 || paramShort == 3) {
      writeToDOM((Node)paramObject, paramShort);
      return true;
    } 
    if (paramShort == 2) {
      writeToSAX((ContentHandler)paramObject);
      return true;
    } 
    return false;
  }
  
  public String getAnnotationString() { return this.fData; }
  
  public short getType() { return 12; }
  
  public String getName() { return null; }
  
  public String getNamespace() { return null; }
  
  public XSNamespaceItem getNamespaceItem() { return null; }
  
  private void writeToSAX(ContentHandler paramContentHandler) {
    SAXParser sAXParser = this.fGrammar.getSAXParser();
    StringReader stringReader = new StringReader(this.fData);
    InputSource inputSource = new InputSource(stringReader);
    sAXParser.setContentHandler(paramContentHandler);
    try {
      sAXParser.parse(inputSource);
    } catch (SAXException sAXException) {
    
    } catch (IOException iOException) {}
    sAXParser.setContentHandler(null);
  }
  
  private void writeToDOM(Node paramNode, short paramShort) {
    Document document1 = (paramShort == 1) ? paramNode.getOwnerDocument() : (Document)paramNode;
    DOMParser dOMParser = this.fGrammar.getDOMParser();
    StringReader stringReader = new StringReader(this.fData);
    InputSource inputSource = new InputSource(stringReader);
    try {
      dOMParser.parse(inputSource);
    } catch (SAXException sAXException) {
    
    } catch (IOException iOException) {}
    Document document2 = dOMParser.getDocument();
    dOMParser.dropDocumentReferences();
    Element element = document2.getDocumentElement();
    Node node = null;
    if (document1 instanceof com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl) {
      node = document1.adoptNode(element);
      if (node == null)
        node = document1.importNode(element, true); 
    } else {
      node = document1.importNode(element, true);
    } 
    paramNode.insertBefore(node, paramNode.getFirstChild());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSAnnotationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */