package com.sun.org.apache.xml.internal.serializer;

import java.util.Vector;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;

public abstract class ToSAXHandler extends SerializerBase {
  protected ContentHandler m_saxHandler;
  
  protected LexicalHandler m_lexHandler;
  
  private boolean m_shouldGenerateNSAttribute = true;
  
  protected TransformStateSetter m_state = null;
  
  public ToSAXHandler() {}
  
  public ToSAXHandler(ContentHandler paramContentHandler, LexicalHandler paramLexicalHandler, String paramString) {
    setContentHandler(paramContentHandler);
    setLexHandler(paramLexicalHandler);
    setEncoding(paramString);
  }
  
  public ToSAXHandler(ContentHandler paramContentHandler, String paramString) {
    setContentHandler(paramContentHandler);
    setEncoding(paramString);
  }
  
  protected void startDocumentInternal() {
    if (this.m_needToCallStartDocument) {
      super.startDocumentInternal();
      this.m_saxHandler.startDocument();
      this.m_needToCallStartDocument = false;
    } 
  }
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void characters(String paramString) throws SAXException {
    int i = paramString.length();
    if (i > this.m_charsBuff.length)
      this.m_charsBuff = new char[i * 2 + 1]; 
    paramString.getChars(0, i, this.m_charsBuff, 0);
    characters(this.m_charsBuff, 0, i);
  }
  
  public void comment(String paramString) throws SAXException {
    flushPending();
    if (this.m_lexHandler != null) {
      int i = paramString.length();
      if (i > this.m_charsBuff.length)
        this.m_charsBuff = new char[i * 2 + 1]; 
      paramString.getChars(0, i, this.m_charsBuff, 0);
      this.m_lexHandler.comment(this.m_charsBuff, 0, i);
      if (this.m_tracer != null)
        fireCommentEvent(this.m_charsBuff, 0, i); 
    } 
  }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {}
  
  protected void closeStartTag() {}
  
  protected void closeCDATA() {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    if (this.m_state != null)
      this.m_state.resetState(getTransformer()); 
    if (this.m_tracer != null)
      fireStartElem(paramString3); 
  }
  
  public void setLexHandler(LexicalHandler paramLexicalHandler) { this.m_lexHandler = paramLexicalHandler; }
  
  public void setContentHandler(ContentHandler paramContentHandler) {
    this.m_saxHandler = paramContentHandler;
    if (this.m_lexHandler == null && paramContentHandler instanceof LexicalHandler)
      this.m_lexHandler = (LexicalHandler)paramContentHandler; 
  }
  
  public void setCdataSectionElements(Vector paramVector) {}
  
  public void setShouldOutputNSAttr(boolean paramBoolean) { this.m_shouldGenerateNSAttribute = paramBoolean; }
  
  boolean getShouldOutputNSAttr() { return this.m_shouldGenerateNSAttribute; }
  
  public void flushPending() {
    if (this.m_needToCallStartDocument) {
      startDocumentInternal();
      this.m_needToCallStartDocument = false;
    } 
    if (this.m_elemContext.m_startTagOpen) {
      closeStartTag();
      this.m_elemContext.m_startTagOpen = false;
    } 
    if (this.m_cdataTagOpen) {
      closeCDATA();
      this.m_cdataTagOpen = false;
    } 
  }
  
  public void setTransformState(TransformStateSetter paramTransformStateSetter) { this.m_state = paramTransformStateSetter; }
  
  public void startElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (this.m_state != null)
      this.m_state.resetState(getTransformer()); 
    if (this.m_tracer != null)
      fireStartElem(paramString3); 
  }
  
  public void startElement(String paramString) throws SAXException {
    if (this.m_state != null)
      this.m_state.resetState(getTransformer()); 
    if (this.m_tracer != null)
      fireStartElem(paramString); 
  }
  
  public void characters(Node paramNode) throws SAXException {
    if (this.m_state != null)
      this.m_state.setCurrentNode(paramNode); 
    String str = paramNode.getNodeValue();
    if (str != null)
      characters(str); 
  }
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException {
    super.fatalError(paramSAXParseException);
    this.m_needToCallStartDocument = false;
    if (this.m_saxHandler instanceof ErrorHandler)
      ((ErrorHandler)this.m_saxHandler).fatalError(paramSAXParseException); 
  }
  
  public void error(SAXParseException paramSAXParseException) throws SAXException {
    super.error(paramSAXParseException);
    if (this.m_saxHandler instanceof ErrorHandler)
      ((ErrorHandler)this.m_saxHandler).error(paramSAXParseException); 
  }
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException {
    super.warning(paramSAXParseException);
    if (this.m_saxHandler instanceof ErrorHandler)
      ((ErrorHandler)this.m_saxHandler).warning(paramSAXParseException); 
  }
  
  public boolean reset() {
    boolean bool = false;
    if (super.reset()) {
      resetToSAXHandler();
      bool = true;
    } 
    return bool;
  }
  
  private void resetToSAXHandler() {
    this.m_lexHandler = null;
    this.m_saxHandler = null;
    this.m_state = null;
    this.m_shouldGenerateNSAttribute = false;
  }
  
  public void addUniqueAttribute(String paramString1, String paramString2, int paramInt) throws SAXException { addAttribute(paramString1, paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\ToSAXHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */