package com.sun.org.apache.xml.internal.dtm;

import com.sun.org.apache.xml.internal.utils.XMLString;
import javax.xml.transform.SourceLocator;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public interface DTM {
  public static final int NULL = -1;
  
  public static final short ROOT_NODE = 0;
  
  public static final short ELEMENT_NODE = 1;
  
  public static final short ATTRIBUTE_NODE = 2;
  
  public static final short TEXT_NODE = 3;
  
  public static final short CDATA_SECTION_NODE = 4;
  
  public static final short ENTITY_REFERENCE_NODE = 5;
  
  public static final short ENTITY_NODE = 6;
  
  public static final short PROCESSING_INSTRUCTION_NODE = 7;
  
  public static final short COMMENT_NODE = 8;
  
  public static final short DOCUMENT_NODE = 9;
  
  public static final short DOCUMENT_TYPE_NODE = 10;
  
  public static final short DOCUMENT_FRAGMENT_NODE = 11;
  
  public static final short NOTATION_NODE = 12;
  
  public static final short NAMESPACE_NODE = 13;
  
  public static final short NTYPES = 14;
  
  void setFeature(String paramString, boolean paramBoolean);
  
  void setProperty(String paramString, Object paramObject);
  
  DTMAxisTraverser getAxisTraverser(int paramInt);
  
  DTMAxisIterator getAxisIterator(int paramInt);
  
  DTMAxisIterator getTypedAxisIterator(int paramInt1, int paramInt2);
  
  boolean hasChildNodes(int paramInt);
  
  int getFirstChild(int paramInt);
  
  int getLastChild(int paramInt);
  
  int getAttributeNode(int paramInt, String paramString1, String paramString2);
  
  int getFirstAttribute(int paramInt);
  
  int getFirstNamespaceNode(int paramInt, boolean paramBoolean);
  
  int getNextSibling(int paramInt);
  
  int getPreviousSibling(int paramInt);
  
  int getNextAttribute(int paramInt);
  
  int getNextNamespaceNode(int paramInt1, int paramInt2, boolean paramBoolean);
  
  int getParent(int paramInt);
  
  int getDocument();
  
  int getOwnerDocument(int paramInt);
  
  int getDocumentRoot(int paramInt);
  
  XMLString getStringValue(int paramInt);
  
  int getStringValueChunkCount(int paramInt);
  
  char[] getStringValueChunk(int paramInt1, int paramInt2, int[] paramArrayOfInt);
  
  int getExpandedTypeID(int paramInt);
  
  int getExpandedTypeID(String paramString1, String paramString2, int paramInt);
  
  String getLocalNameFromExpandedNameID(int paramInt);
  
  String getNamespaceFromExpandedNameID(int paramInt);
  
  String getNodeName(int paramInt);
  
  String getNodeNameX(int paramInt);
  
  String getLocalName(int paramInt);
  
  String getPrefix(int paramInt);
  
  String getNamespaceURI(int paramInt);
  
  String getNodeValue(int paramInt);
  
  short getNodeType(int paramInt);
  
  short getLevel(int paramInt);
  
  boolean isSupported(String paramString1, String paramString2);
  
  String getDocumentBaseURI();
  
  void setDocumentBaseURI(String paramString);
  
  String getDocumentSystemIdentifier(int paramInt);
  
  String getDocumentEncoding(int paramInt);
  
  String getDocumentStandalone(int paramInt);
  
  String getDocumentVersion(int paramInt);
  
  boolean getDocumentAllDeclarationsProcessed();
  
  String getDocumentTypeDeclarationSystemIdentifier();
  
  String getDocumentTypeDeclarationPublicIdentifier();
  
  int getElementById(String paramString);
  
  String getUnparsedEntityURI(String paramString);
  
  boolean supportsPreStripping();
  
  boolean isNodeAfter(int paramInt1, int paramInt2);
  
  boolean isCharacterElementContentWhitespace(int paramInt);
  
  boolean isDocumentAllDeclarationsProcessed(int paramInt);
  
  boolean isAttributeSpecified(int paramInt);
  
  void dispatchCharactersEvents(int paramInt, ContentHandler paramContentHandler, boolean paramBoolean) throws SAXException;
  
  void dispatchToEvents(int paramInt, ContentHandler paramContentHandler) throws SAXException;
  
  Node getNode(int paramInt);
  
  boolean needsTwoThreads();
  
  ContentHandler getContentHandler();
  
  LexicalHandler getLexicalHandler();
  
  EntityResolver getEntityResolver();
  
  DTDHandler getDTDHandler();
  
  ErrorHandler getErrorHandler();
  
  DeclHandler getDeclHandler();
  
  void appendChild(int paramInt, boolean paramBoolean1, boolean paramBoolean2);
  
  void appendTextChild(String paramString);
  
  SourceLocator getSourceLocatorFor(int paramInt);
  
  void documentRegistration();
  
  void documentRelease();
  
  void migrateTo(DTMManager paramDTMManager);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\DTM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */