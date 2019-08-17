package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xml.internal.serializer.NamespaceMappings;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.Locator2;

public class DOM2TO implements XMLReader, Locator2 {
  private static final String EMPTYSTRING = "";
  
  private static final String XMLNS_PREFIX = "xmlns";
  
  private Node _dom;
  
  private SerializationHandler _handler;
  
  private String xmlVersion = null;
  
  private String xmlEncoding = null;
  
  public DOM2TO(Node paramNode, SerializationHandler paramSerializationHandler) {
    this._dom = paramNode;
    this._handler = paramSerializationHandler;
  }
  
  public ContentHandler getContentHandler() { return null; }
  
  public void setContentHandler(ContentHandler paramContentHandler) {}
  
  public void parse(InputSource paramInputSource) throws IOException, SAXException { parse(this._dom); }
  
  public void parse() throws IOException, SAXException {
    if (this._dom != null) {
      boolean bool = (this._dom.getNodeType() != 9) ? 1 : 0;
      if (bool) {
        this._handler.startDocument();
        parse(this._dom);
        this._handler.endDocument();
      } else {
        parse(this._dom);
      } 
    } 
  }
  
  private void parse(Node paramNode) throws IOException, SAXException {
    String str3;
    String str2;
    byte b2;
    byte b1;
    NamespaceMappings namespaceMappings;
    int i;
    NamedNodeMap namedNodeMap;
    String str1;
    Node node;
    if (paramNode == null)
      return; 
    switch (paramNode.getNodeType()) {
      case 4:
        this._handler.startCDATA();
        this._handler.characters(paramNode.getNodeValue());
        this._handler.endCDATA();
        break;
      case 8:
        this._handler.comment(paramNode.getNodeValue());
        break;
      case 9:
        setDocumentInfo((Document)paramNode);
        this._handler.setDocumentLocator(this);
        this._handler.startDocument();
        for (node = paramNode.getFirstChild(); node != null; node = node.getNextSibling())
          parse(node); 
        this._handler.endDocument();
        break;
      case 11:
        for (node = paramNode.getFirstChild(); node != null; node = node.getNextSibling())
          parse(node); 
        break;
      case 1:
        str1 = paramNode.getNodeName();
        this._handler.startElement(null, null, str1);
        namedNodeMap = paramNode.getAttributes();
        i = namedNodeMap.getLength();
        for (b1 = 0; b1 < i; b1++) {
          Node node1 = namedNodeMap.item(b1);
          String str = node1.getNodeName();
          if (str.startsWith("xmlns")) {
            String str5 = node1.getNodeValue();
            int j = str.lastIndexOf(':');
            String str4 = (j > 0) ? str.substring(j + 1) : "";
            this._handler.namespaceAfterStartElement(str4, str5);
          } 
        } 
        namespaceMappings = new NamespaceMappings();
        for (b2 = 0; b2 < i; b2++) {
          Node node1 = namedNodeMap.item(b2);
          String str = node1.getNodeName();
          if (!str.startsWith("xmlns")) {
            String str4 = node1.getNamespaceURI();
            if (str4 != null && !str4.equals("")) {
              int j = str.lastIndexOf(':');
              String str6 = namespaceMappings.lookupPrefix(str4);
              if (str6 == null)
                str6 = namespaceMappings.generateNextPrefix(); 
              String str5 = (j > 0) ? str.substring(0, j) : str6;
              this._handler.namespaceAfterStartElement(str5, str4);
              this._handler.addAttribute(str5 + ":" + str, node1.getNodeValue());
            } else {
              this._handler.addAttribute(str, node1.getNodeValue());
            } 
          } 
        } 
        str2 = paramNode.getNamespaceURI();
        str3 = paramNode.getLocalName();
        if (str2 != null) {
          int j = str1.lastIndexOf(':');
          String str = (j > 0) ? str1.substring(0, j) : "";
          this._handler.namespaceAfterStartElement(str, str2);
        } else if (str2 == null && str3 != null) {
          String str = "";
          this._handler.namespaceAfterStartElement(str, "");
        } 
        for (node = paramNode.getFirstChild(); node != null; node = node.getNextSibling())
          parse(node); 
        this._handler.endElement(str1);
        break;
      case 7:
        this._handler.processingInstruction(paramNode.getNodeName(), paramNode.getNodeValue());
        break;
      case 3:
        this._handler.characters(paramNode.getNodeValue());
        break;
    } 
  }
  
  public DTDHandler getDTDHandler() { return null; }
  
  public ErrorHandler getErrorHandler() { return null; }
  
  public boolean getFeature(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException { return false; }
  
  public void setFeature(String paramString, boolean paramBoolean) throws SAXNotRecognizedException, SAXNotSupportedException {}
  
  public void parse(String paramString) throws IOException, SAXException { throw new IOException("This method is not yet implemented."); }
  
  public void setDTDHandler(DTDHandler paramDTDHandler) throws NullPointerException {}
  
  public void setEntityResolver(EntityResolver paramEntityResolver) throws NullPointerException {}
  
  public EntityResolver getEntityResolver() { return null; }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler) throws NullPointerException {}
  
  public void setProperty(String paramString, Object paramObject) throws SAXNotRecognizedException, SAXNotSupportedException {}
  
  public Object getProperty(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException { return null; }
  
  public int getColumnNumber() { return 0; }
  
  public int getLineNumber() { return 0; }
  
  public String getPublicId() { return null; }
  
  public String getSystemId() { return null; }
  
  private void setDocumentInfo(Document paramDocument) {
    if (!paramDocument.getXmlStandalone())
      this._handler.setStandalone(Boolean.toString(paramDocument.getXmlStandalone())); 
    setXMLVersion(paramDocument.getXmlVersion());
    setEncoding(paramDocument.getXmlEncoding());
  }
  
  public String getXMLVersion() { return this.xmlVersion; }
  
  private void setXMLVersion(String paramString) throws IOException, SAXException {
    if (paramString != null) {
      this.xmlVersion = paramString;
      this._handler.setVersion(this.xmlVersion);
    } 
  }
  
  public String getEncoding() { return this.xmlEncoding; }
  
  private void setEncoding(String paramString) throws IOException, SAXException {
    if (paramString != null) {
      this.xmlEncoding = paramString;
      this._handler.setEncoding(paramString);
    } 
  }
  
  private String getNodeTypeFromCode(short paramShort) {
    String str = null;
    switch (paramShort) {
      case 2:
        str = "ATTRIBUTE_NODE";
        break;
      case 4:
        str = "CDATA_SECTION_NODE";
        break;
      case 8:
        str = "COMMENT_NODE";
        break;
      case 11:
        str = "DOCUMENT_FRAGMENT_NODE";
        break;
      case 9:
        str = "DOCUMENT_NODE";
        break;
      case 10:
        str = "DOCUMENT_TYPE_NODE";
        break;
      case 1:
        str = "ELEMENT_NODE";
        break;
      case 6:
        str = "ENTITY_NODE";
        break;
      case 5:
        str = "ENTITY_REFERENCE_NODE";
        break;
      case 12:
        str = "NOTATION_NODE";
        break;
      case 7:
        str = "PROCESSING_INSTRUCTION_NODE";
        break;
      case 3:
        str = "TEXT_NODE";
        break;
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\DOM2TO.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */