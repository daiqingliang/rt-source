package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class DOM2SAX implements XMLReader, Locator {
  private static final String EMPTYSTRING = "";
  
  private static final String XMLNS_PREFIX = "xmlns";
  
  private Node _dom = null;
  
  private ContentHandler _sax = null;
  
  private LexicalHandler _lex = null;
  
  private SAXImpl _saxImpl = null;
  
  private Map<String, Stack> _nsPrefixes = new HashMap();
  
  public DOM2SAX(Node paramNode) { this._dom = paramNode; }
  
  public ContentHandler getContentHandler() { return this._sax; }
  
  public void setContentHandler(ContentHandler paramContentHandler) throws NullPointerException {
    this._sax = paramContentHandler;
    if (paramContentHandler instanceof LexicalHandler)
      this._lex = (LexicalHandler)paramContentHandler; 
    if (paramContentHandler instanceof SAXImpl)
      this._saxImpl = (SAXImpl)paramContentHandler; 
  }
  
  private boolean startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    boolean bool = true;
    Stack stack = (Stack)this._nsPrefixes.get(paramString1);
    if (stack != null) {
      if (stack.isEmpty()) {
        this._sax.startPrefixMapping(paramString1, paramString2);
        stack.push(paramString2);
      } else {
        String str = (String)stack.peek();
        if (!str.equals(paramString2)) {
          this._sax.startPrefixMapping(paramString1, paramString2);
          stack.push(paramString2);
        } else {
          bool = false;
        } 
      } 
    } else {
      this._sax.startPrefixMapping(paramString1, paramString2);
      this._nsPrefixes.put(paramString1, stack = new Stack());
      stack.push(paramString2);
    } 
    return bool;
  }
  
  private void endPrefixMapping(String paramString) throws SAXException {
    Stack stack = (Stack)this._nsPrefixes.get(paramString);
    if (stack != null) {
      this._sax.endPrefixMapping(paramString);
      stack.pop();
    } 
  }
  
  private static String getLocalName(Node paramNode) {
    String str = paramNode.getLocalName();
    if (str == null) {
      String str1 = paramNode.getNodeName();
      int i = str1.lastIndexOf(':');
      return (i > 0) ? str1.substring(i + 1) : str1;
    } 
    return str;
  }
  
  public void parse(InputSource paramInputSource) throws IOException, SAXException { parse(this._dom); }
  
  public void parse() throws IOException, SAXException {
    if (this._dom != null) {
      boolean bool = (this._dom.getNodeType() != 9) ? 1 : 0;
      if (bool) {
        this._sax.startDocument();
        parse(this._dom);
        this._sax.endDocument();
      } else {
        parse(this._dom);
      } 
    } 
  }
  
  private void parse(Node paramNode) {
    String str5;
    byte b2;
    int j;
    String str4;
    String str3;
    String str2;
    byte b1;
    int i;
    NamedNodeMap namedNodeMap;
    AttributesImpl attributesImpl;
    Vector vector;
    Node node;
    String str1;
    Object object = null;
    if (paramNode == null)
      return; 
    switch (paramNode.getNodeType()) {
      case 4:
        str1 = paramNode.getNodeValue();
        if (this._lex != null) {
          this._lex.startCDATA();
          this._sax.characters(str1.toCharArray(), 0, str1.length());
          this._lex.endCDATA();
          break;
        } 
        this._sax.characters(str1.toCharArray(), 0, str1.length());
        break;
      case 8:
        if (this._lex != null) {
          String str = paramNode.getNodeValue();
          this._lex.comment(str.toCharArray(), 0, str.length());
        } 
        break;
      case 9:
        this._sax.setDocumentLocator(this);
        this._sax.startDocument();
        for (node = paramNode.getFirstChild(); node != null; node = node.getNextSibling())
          parse(node); 
        this._sax.endDocument();
        break;
      case 1:
        vector = new Vector();
        attributesImpl = new AttributesImpl();
        namedNodeMap = paramNode.getAttributes();
        i = namedNodeMap.getLength();
        for (b1 = 0; b1 < i; b1++) {
          Node node1 = namedNodeMap.item(b1);
          String str = node1.getNodeName();
          if (str.startsWith("xmlns")) {
            String str7 = node1.getNodeValue();
            int k = str.lastIndexOf(':');
            String str6 = (k > 0) ? str.substring(k + 1) : "";
            if (startPrefixMapping(str6, str7))
              vector.addElement(str6); 
          } 
        } 
        for (b1 = 0; b1 < i; b1++) {
          Node node1 = namedNodeMap.item(b1);
          String str = node1.getNodeName();
          if (!str.startsWith("xmlns")) {
            String str6 = node1.getNamespaceURI();
            String str7 = getLocalName(node1);
            if (str6 != null) {
              String str8;
              int k = str.lastIndexOf(':');
              if (k > 0) {
                str8 = str.substring(0, k);
              } else {
                str8 = BasisLibrary.generatePrefix();
                str = str8 + ':' + str;
              } 
              if (startPrefixMapping(str8, str6))
                vector.addElement(str8); 
            } 
            attributesImpl.addAttribute(node1.getNamespaceURI(), getLocalName(node1), str, "CDATA", node1.getNodeValue());
          } 
        } 
        str2 = paramNode.getNodeName();
        str3 = paramNode.getNamespaceURI();
        str4 = getLocalName(paramNode);
        if (str3 != null) {
          int k = str2.lastIndexOf(':');
          String str = (k > 0) ? str2.substring(0, k) : "";
          if (startPrefixMapping(str, str3))
            vector.addElement(str); 
        } 
        if (this._saxImpl != null) {
          this._saxImpl.startElement(str3, str4, str2, attributesImpl, paramNode);
        } else {
          this._sax.startElement(str3, str4, str2, attributesImpl);
        } 
        for (node = paramNode.getFirstChild(); node != null; node = node.getNextSibling())
          parse(node); 
        this._sax.endElement(str3, str4, str2);
        j = vector.size();
        for (b2 = 0; b2 < j; b2++)
          endPrefixMapping((String)vector.elementAt(b2)); 
        break;
      case 7:
        this._sax.processingInstruction(paramNode.getNodeName(), paramNode.getNodeValue());
        break;
      case 3:
        str5 = paramNode.getNodeValue();
        this._sax.characters(str5.toCharArray(), 0, str5.length());
        break;
    } 
  }
  
  public DTDHandler getDTDHandler() { return null; }
  
  public ErrorHandler getErrorHandler() { return null; }
  
  public boolean getFeature(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException { return false; }
  
  public void setFeature(String paramString, boolean paramBoolean) throws SAXNotRecognizedException, SAXNotSupportedException {}
  
  public void parse(String paramString) throws SAXException { throw new IOException("This method is not yet implemented."); }
  
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\DOM2SAX.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */