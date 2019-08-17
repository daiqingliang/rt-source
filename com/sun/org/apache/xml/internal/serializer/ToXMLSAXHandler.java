package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public final class ToXMLSAXHandler extends ToSAXHandler {
  protected boolean m_escapeSetting = true;
  
  public ToXMLSAXHandler() {
    this.m_prefixMap = new NamespaceMappings();
    initCDATA();
  }
  
  public Properties getOutputFormat() { return null; }
  
  public OutputStream getOutputStream() { return null; }
  
  public Writer getWriter() { return null; }
  
  public void indent(int paramInt) throws SAXException {}
  
  public void serialize(Node paramNode) throws IOException {}
  
  public boolean setEscaping(boolean paramBoolean) throws SAXException {
    boolean bool = this.m_escapeSetting;
    this.m_escapeSetting = paramBoolean;
    if (paramBoolean) {
      processingInstruction("javax.xml.transform.enable-output-escaping", "");
    } else {
      processingInstruction("javax.xml.transform.disable-output-escaping", "");
    } 
    return bool;
  }
  
  public void setOutputFormat(Properties paramProperties) {}
  
  public void setOutputStream(OutputStream paramOutputStream) {}
  
  public void setWriter(Writer paramWriter) {}
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws SAXException {}
  
  public void elementDecl(String paramString1, String paramString2) throws SAXException {}
  
  public void externalEntityDecl(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void internalEntityDecl(String paramString1, String paramString2) throws SAXException {}
  
  public void endDocument() {
    flushPending();
    this.m_saxHandler.endDocument();
    if (this.m_tracer != null)
      fireEndDoc(); 
  }
  
  protected void closeStartTag() {
    this.m_elemContext.m_startTagOpen = false;
    String str1 = getLocalName(this.m_elemContext.m_elementName);
    String str2 = getNamespaceURI(this.m_elemContext.m_elementName, true);
    if (this.m_needToCallStartDocument)
      startDocumentInternal(); 
    this.m_saxHandler.startElement(str2, str1, this.m_elemContext.m_elementName, this.m_attributes);
    this.m_attributes.clear();
    if (this.m_state != null)
      this.m_state.setCurrentNode(null); 
  }
  
  public void closeCDATA() {
    if (this.m_lexHandler != null && this.m_cdataTagOpen)
      this.m_lexHandler.endCDATA(); 
    this.m_cdataTagOpen = false;
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    flushPending();
    if (paramString1 == null)
      if (this.m_elemContext.m_elementURI != null) {
        paramString1 = this.m_elemContext.m_elementURI;
      } else {
        paramString1 = getNamespaceURI(paramString3, true);
      }  
    if (paramString2 == null)
      if (this.m_elemContext.m_elementLocalName != null) {
        paramString2 = this.m_elemContext.m_elementLocalName;
      } else {
        paramString2 = getLocalName(paramString3);
      }  
    this.m_saxHandler.endElement(paramString1, paramString2, paramString3);
    if (this.m_tracer != null)
      fireEndElem(paramString3); 
    this.m_prefixMap.popNamespaces(this.m_elemContext.m_currentElemDepth, this.m_saxHandler);
    this.m_elemContext = this.m_elemContext.m_prev;
  }
  
  public void endPrefixMapping(String paramString) throws SAXException {}
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException { this.m_saxHandler.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void setDocumentLocator(Locator paramLocator) {
    super.setDocumentLocator(paramLocator);
    this.m_saxHandler.setDocumentLocator(paramLocator);
  }
  
  public void skippedEntity(String paramString) throws SAXException { this.m_saxHandler.skippedEntity(paramString); }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException { startPrefixMapping(paramString1, paramString2, true); }
  
  public boolean startPrefixMapping(String paramString1, String paramString2, boolean paramBoolean) throws SAXException {
    int i;
    if (paramBoolean) {
      flushPending();
      i = this.m_elemContext.m_currentElemDepth + 1;
    } else {
      i = this.m_elemContext.m_currentElemDepth;
    } 
    boolean bool = this.m_prefixMap.pushNamespace(paramString1, paramString2, i);
    if (bool) {
      this.m_saxHandler.startPrefixMapping(paramString1, paramString2);
      if (getShouldOutputNSAttr())
        if ("".equals(paramString1)) {
          String str = "xmlns";
          addAttributeAlways("http://www.w3.org/2000/xmlns/", str, str, "CDATA", paramString2, false);
        } else if (!"".equals(paramString2)) {
          String str = "xmlns:" + paramString1;
          addAttributeAlways("http://www.w3.org/2000/xmlns/", paramString1, str, "CDATA", paramString2, false);
        }  
    } 
    return bool;
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    flushPending();
    if (this.m_lexHandler != null)
      this.m_lexHandler.comment(paramArrayOfChar, paramInt1, paramInt2); 
    if (this.m_tracer != null)
      fireCommentEvent(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public void endCDATA() {}
  
  public void endDTD() {
    if (this.m_lexHandler != null)
      this.m_lexHandler.endDTD(); 
  }
  
  public void startEntity(String paramString) throws SAXException {
    if (this.m_lexHandler != null)
      this.m_lexHandler.startEntity(paramString); 
  }
  
  public void characters(String paramString) throws SAXException {
    int i = paramString.length();
    if (i > this.m_charsBuff.length)
      this.m_charsBuff = new char[i * 2 + 1]; 
    paramString.getChars(0, i, this.m_charsBuff, 0);
    characters(this.m_charsBuff, 0, i);
  }
  
  public ToXMLSAXHandler(ContentHandler paramContentHandler, String paramString) {
    super(paramContentHandler, paramString);
    initCDATA();
    this.m_prefixMap = new NamespaceMappings();
  }
  
  public ToXMLSAXHandler(ContentHandler paramContentHandler, LexicalHandler paramLexicalHandler, String paramString) {
    super(paramContentHandler, paramLexicalHandler, paramString);
    initCDATA();
    this.m_prefixMap = new NamespaceMappings();
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3) throws SAXException { startElement(paramString1, paramString2, paramString3, null); }
  
  public void startElement(String paramString) throws SAXException { startElement(null, null, paramString, null); }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.m_needToCallStartDocument) {
      startDocumentInternal();
      this.m_needToCallStartDocument = false;
    } 
    if (this.m_elemContext.m_startTagOpen) {
      closeStartTag();
      this.m_elemContext.m_startTagOpen = false;
    } 
    if (this.m_elemContext.m_isCdataSection && !this.m_cdataTagOpen && this.m_lexHandler != null) {
      this.m_lexHandler.startCDATA();
      this.m_cdataTagOpen = true;
    } 
    this.m_saxHandler.characters(paramArrayOfChar, paramInt1, paramInt2);
    if (this.m_tracer != null)
      fireCharEvent(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public void endElement(String paramString) throws SAXException { endElement(null, null, paramString); }
  
  public void namespaceAfterStartElement(String paramString1, String paramString2) throws SAXException { startPrefixMapping(paramString1, paramString2, false); }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    flushPending();
    this.m_saxHandler.processingInstruction(paramString1, paramString2);
    if (this.m_tracer != null)
      fireEscapingEvent(paramString1, paramString2); 
  }
  
  protected boolean popNamespace(String paramString) {
    try {
      if (this.m_prefixMap.popNamespace(paramString)) {
        this.m_saxHandler.endPrefixMapping(paramString);
        return true;
      } 
    } catch (SAXException sAXException) {}
    return false;
  }
  
  public void startCDATA() {
    if (!this.m_cdataTagOpen) {
      flushPending();
      if (this.m_lexHandler != null) {
        this.m_lexHandler.startCDATA();
        this.m_cdataTagOpen = true;
      } 
    } 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    flushPending();
    super.startElement(paramString1, paramString2, paramString3, paramAttributes);
    if (this.m_needToOutputDocTypeDecl) {
      String str = getDoctypeSystem();
      if (str != null && this.m_lexHandler != null) {
        String str1 = getDoctypePublic();
        if (str != null)
          this.m_lexHandler.startDTD(paramString3, str1, str); 
      } 
      this.m_needToOutputDocTypeDecl = false;
    } 
    this.m_elemContext = this.m_elemContext.push(paramString1, paramString2, paramString3);
    if (paramString1 != null)
      ensurePrefixIsDeclared(paramString1, paramString3); 
    if (paramAttributes != null)
      addAttributes(paramAttributes); 
    this.m_elemContext.m_isCdataSection = isCdataSection();
  }
  
  private void ensurePrefixIsDeclared(String paramString1, String paramString2) throws SAXException {
    if (paramString1 != null && paramString1.length() > 0) {
      int i;
      boolean bool = ((i = paramString2.indexOf(":")) < 0) ? 1 : 0;
      String str = bool ? "" : paramString2.substring(0, i);
      if (null != str) {
        String str1 = this.m_prefixMap.lookupNamespace(str);
        if (null == str1 || !str1.equals(paramString1)) {
          startPrefixMapping(str, paramString1, false);
          if (getShouldOutputNSAttr())
            addAttributeAlways("http://www.w3.org/2000/xmlns/", bool ? "xmlns" : str, bool ? "xmlns" : ("xmlns:" + str), "CDATA", paramString1, false); 
        } 
      } 
    } 
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean) throws SAXException {
    if (this.m_elemContext.m_startTagOpen) {
      ensurePrefixIsDeclared(paramString1, paramString3);
      addAttributeAlways(paramString1, paramString2, paramString3, paramString4, paramString5, false);
    } 
  }
  
  public boolean reset() {
    boolean bool = false;
    if (super.reset()) {
      resetToXMLSAXHandler();
      bool = true;
    } 
    return bool;
  }
  
  private void resetToXMLSAXHandler() { this.m_escapeSetting = true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\ToXMLSAXHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */