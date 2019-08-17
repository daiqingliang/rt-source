package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;
import java.util.Vector;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class EmptySerializer implements SerializationHandler {
  protected static final String ERR = "EmptySerializer method not over-ridden";
  
  protected void couldThrowIOException() {}
  
  protected void couldThrowSAXException() {}
  
  protected void couldThrowSAXException(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {}
  
  protected void couldThrowSAXException(String paramString) throws SAXException {}
  
  void aMethodIsCalled() {}
  
  public ContentHandler asContentHandler() throws IOException {
    couldThrowIOException();
    return null;
  }
  
  public void setContentHandler(ContentHandler paramContentHandler) { aMethodIsCalled(); }
  
  public void close() { aMethodIsCalled(); }
  
  public Properties getOutputFormat() {
    aMethodIsCalled();
    return null;
  }
  
  public OutputStream getOutputStream() {
    aMethodIsCalled();
    return null;
  }
  
  public Writer getWriter() {
    aMethodIsCalled();
    return null;
  }
  
  public boolean reset() {
    aMethodIsCalled();
    return false;
  }
  
  public void serialize(Node paramNode) throws IOException { couldThrowIOException(); }
  
  public void setCdataSectionElements(Vector paramVector) { aMethodIsCalled(); }
  
  public boolean setEscaping(boolean paramBoolean) throws SAXException {
    couldThrowSAXException();
    return false;
  }
  
  public void setIndent(boolean paramBoolean) { aMethodIsCalled(); }
  
  public void setIndentAmount(int paramInt) { aMethodIsCalled(); }
  
  public void setIsStandalone(boolean paramBoolean) { aMethodIsCalled(); }
  
  public void setOutputFormat(Properties paramProperties) { aMethodIsCalled(); }
  
  public void setOutputStream(OutputStream paramOutputStream) { aMethodIsCalled(); }
  
  public void setVersion(String paramString) throws SAXException { aMethodIsCalled(); }
  
  public void setWriter(Writer paramWriter) { aMethodIsCalled(); }
  
  public void setTransformer(Transformer paramTransformer) { aMethodIsCalled(); }
  
  public Transformer getTransformer() {
    aMethodIsCalled();
    return null;
  }
  
  public void flushPending() { couldThrowSAXException(); }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean) throws SAXException { couldThrowSAXException(); }
  
  public void addAttributes(Attributes paramAttributes) throws SAXException { couldThrowSAXException(); }
  
  public void addAttribute(String paramString1, String paramString2) { aMethodIsCalled(); }
  
  public void characters(String paramString) throws SAXException { couldThrowSAXException(); }
  
  public void endElement(String paramString) throws SAXException { couldThrowSAXException(); }
  
  public void startDocument() { couldThrowSAXException(); }
  
  public void startElement(String paramString1, String paramString2, String paramString3) throws SAXException { couldThrowSAXException(paramString3); }
  
  public void startElement(String paramString) throws SAXException { couldThrowSAXException(paramString); }
  
  public void namespaceAfterStartElement(String paramString1, String paramString2) { couldThrowSAXException(); }
  
  public boolean startPrefixMapping(String paramString1, String paramString2, boolean paramBoolean) throws SAXException {
    couldThrowSAXException();
    return false;
  }
  
  public void entityReference(String paramString) throws SAXException { couldThrowSAXException(); }
  
  public NamespaceMappings getNamespaceMappings() {
    aMethodIsCalled();
    return null;
  }
  
  public String getPrefix(String paramString) {
    aMethodIsCalled();
    return null;
  }
  
  public String getNamespaceURI(String paramString, boolean paramBoolean) {
    aMethodIsCalled();
    return null;
  }
  
  public String getNamespaceURIFromPrefix(String paramString) {
    aMethodIsCalled();
    return null;
  }
  
  public void setDocumentLocator(Locator paramLocator) { aMethodIsCalled(); }
  
  public void endDocument() { couldThrowSAXException(); }
  
  public void startPrefixMapping(String paramString1, String paramString2) { couldThrowSAXException(); }
  
  public void endPrefixMapping(String paramString) throws SAXException { couldThrowSAXException(); }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException { couldThrowSAXException(); }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException { couldThrowSAXException(); }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException { couldThrowSAXException(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException { couldThrowSAXException(); }
  
  public void processingInstruction(String paramString1, String paramString2) { couldThrowSAXException(); }
  
  public void skippedEntity(String paramString) throws SAXException { couldThrowSAXException(); }
  
  public void comment(String paramString) throws SAXException { couldThrowSAXException(); }
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException { couldThrowSAXException(); }
  
  public void endDTD() { couldThrowSAXException(); }
  
  public void startEntity(String paramString) throws SAXException { couldThrowSAXException(); }
  
  public void endEntity(String paramString) throws SAXException { couldThrowSAXException(); }
  
  public void startCDATA() { couldThrowSAXException(); }
  
  public void endCDATA() { couldThrowSAXException(); }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException { couldThrowSAXException(); }
  
  public String getDoctypePublic() {
    aMethodIsCalled();
    return null;
  }
  
  public String getDoctypeSystem() {
    aMethodIsCalled();
    return null;
  }
  
  public String getEncoding() {
    aMethodIsCalled();
    return null;
  }
  
  public boolean getIndent() {
    aMethodIsCalled();
    return false;
  }
  
  public int getIndentAmount() {
    aMethodIsCalled();
    return 0;
  }
  
  public String getMediaType() {
    aMethodIsCalled();
    return null;
  }
  
  public boolean getOmitXMLDeclaration() {
    aMethodIsCalled();
    return false;
  }
  
  public String getStandalone() {
    aMethodIsCalled();
    return null;
  }
  
  public String getVersion() {
    aMethodIsCalled();
    return null;
  }
  
  public void setDoctype(String paramString1, String paramString2) { aMethodIsCalled(); }
  
  public void setDoctypePublic(String paramString) throws SAXException { aMethodIsCalled(); }
  
  public void setDoctypeSystem(String paramString) throws SAXException { aMethodIsCalled(); }
  
  public void setEncoding(String paramString) throws SAXException { aMethodIsCalled(); }
  
  public void setMediaType(String paramString) throws SAXException { aMethodIsCalled(); }
  
  public void setOmitXMLDeclaration(boolean paramBoolean) { aMethodIsCalled(); }
  
  public void setStandalone(String paramString) throws SAXException { aMethodIsCalled(); }
  
  public void elementDecl(String paramString1, String paramString2) { couldThrowSAXException(); }
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws SAXException { couldThrowSAXException(); }
  
  public void internalEntityDecl(String paramString1, String paramString2) { couldThrowSAXException(); }
  
  public void externalEntityDecl(String paramString1, String paramString2, String paramString3) throws SAXException { couldThrowSAXException(); }
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException { couldThrowSAXException(); }
  
  public void error(SAXParseException paramSAXParseException) throws SAXException { couldThrowSAXException(); }
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException { couldThrowSAXException(); }
  
  public DOMSerializer asDOMSerializer() throws IOException {
    couldThrowIOException();
    return null;
  }
  
  public void setNamespaceMappings(NamespaceMappings paramNamespaceMappings) { aMethodIsCalled(); }
  
  public void setSourceLocator(SourceLocator paramSourceLocator) { aMethodIsCalled(); }
  
  public void addUniqueAttribute(String paramString1, String paramString2, int paramInt) throws SAXException { couldThrowSAXException(); }
  
  public void characters(Node paramNode) throws IOException { couldThrowSAXException(); }
  
  public void addXSLAttribute(String paramString1, String paramString2, String paramString3) throws SAXException { aMethodIsCalled(); }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws SAXException { couldThrowSAXException(); }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3) throws SAXException { couldThrowSAXException(); }
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4) throws SAXException { couldThrowSAXException(); }
  
  public void setDTDEntityExpansion(boolean paramBoolean) { aMethodIsCalled(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\EmptySerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */