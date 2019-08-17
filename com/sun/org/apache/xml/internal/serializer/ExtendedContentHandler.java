package com.sun.org.apache.xml.internal.serializer;

import javax.xml.transform.SourceLocator;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

interface ExtendedContentHandler extends ContentHandler {
  public static final int NO_BAD_CHARS = 1;
  
  public static final int HTML_ATTREMPTY = 2;
  
  public static final int HTML_ATTRURL = 4;
  
  void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean) throws SAXException;
  
  void addAttributes(Attributes paramAttributes) throws SAXException;
  
  void addAttribute(String paramString1, String paramString2);
  
  void characters(String paramString) throws SAXException;
  
  void characters(Node paramNode) throws SAXException;
  
  void endElement(String paramString) throws SAXException;
  
  void startElement(String paramString1, String paramString2, String paramString3) throws SAXException;
  
  void startElement(String paramString) throws SAXException;
  
  void namespaceAfterStartElement(String paramString1, String paramString2);
  
  boolean startPrefixMapping(String paramString1, String paramString2, boolean paramBoolean) throws SAXException;
  
  void entityReference(String paramString) throws SAXException;
  
  NamespaceMappings getNamespaceMappings();
  
  String getPrefix(String paramString);
  
  String getNamespaceURI(String paramString, boolean paramBoolean);
  
  String getNamespaceURIFromPrefix(String paramString);
  
  void setSourceLocator(SourceLocator paramSourceLocator);
  
  void addUniqueAttribute(String paramString1, String paramString2, int paramInt) throws SAXException;
  
  void addXSLAttribute(String paramString1, String paramString2, String paramString3) throws SAXException;
  
  void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws SAXException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\ExtendedContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */