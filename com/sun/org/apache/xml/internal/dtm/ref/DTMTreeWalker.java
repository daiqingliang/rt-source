package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.utils.XMLString;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class DTMTreeWalker {
  private ContentHandler m_contentHandler = null;
  
  protected DTM m_dtm;
  
  boolean nextIsRaw = false;
  
  public void setDTM(DTM paramDTM) { this.m_dtm = paramDTM; }
  
  public ContentHandler getcontentHandler() { return this.m_contentHandler; }
  
  public void setcontentHandler(ContentHandler paramContentHandler) { this.m_contentHandler = paramContentHandler; }
  
  public DTMTreeWalker() {}
  
  public DTMTreeWalker(ContentHandler paramContentHandler, DTM paramDTM) {
    this.m_contentHandler = paramContentHandler;
    this.m_dtm = paramDTM;
  }
  
  public void traverse(int paramInt) throws SAXException {
    int i = paramInt;
    while (-1 != paramInt) {
      startNode(paramInt);
      int j = this.m_dtm.getFirstChild(paramInt);
      while (-1 == j) {
        endNode(paramInt);
        if (i == paramInt)
          break; 
        j = this.m_dtm.getNextSibling(paramInt);
        if (-1 == j) {
          paramInt = this.m_dtm.getParent(paramInt);
          if (-1 == paramInt || i == paramInt) {
            if (-1 != paramInt)
              endNode(paramInt); 
            j = -1;
            break;
          } 
        } 
      } 
      paramInt = j;
    } 
  }
  
  public void traverse(int paramInt1, int paramInt2) throws SAXException {
    while (-1 != paramInt1) {
      startNode(paramInt1);
      int i = this.m_dtm.getFirstChild(paramInt1);
      while (-1 == i) {
        endNode(paramInt1);
        if (-1 != paramInt2 && paramInt2 == paramInt1)
          break; 
        i = this.m_dtm.getNextSibling(paramInt1);
        if (-1 == i) {
          paramInt1 = this.m_dtm.getParent(paramInt1);
          if (-1 == paramInt1 || (-1 != paramInt2 && paramInt2 == paramInt1)) {
            i = -1;
            break;
          } 
        } 
      } 
      paramInt1 = i;
    } 
  }
  
  private final void dispatachChars(int paramInt) throws SAXException { this.m_dtm.dispatchCharactersEvents(paramInt, this.m_contentHandler, false); }
  
  protected void startNode(int paramInt) throws SAXException {
    LexicalHandler lexicalHandler;
    boolean bool;
    int j;
    String str2;
    AttributesImpl attributesImpl;
    int i;
    String str1;
    XMLString xMLString;
    DTM dTM;
    if (this.m_contentHandler instanceof com.sun.org.apache.xml.internal.utils.NodeConsumer);
    switch (this.m_dtm.getNodeType(paramInt)) {
      case 8:
        xMLString = this.m_dtm.getStringValue(paramInt);
        if (this.m_contentHandler instanceof LexicalHandler) {
          LexicalHandler lexicalHandler1 = (LexicalHandler)this.m_contentHandler;
          xMLString.dispatchAsComment(lexicalHandler1);
        } 
        break;
      case 9:
        this.m_contentHandler.startDocument();
        break;
      case 1:
        dTM = this.m_dtm;
        for (i = dTM.getFirstNamespaceNode(paramInt, true); -1 != i; i = dTM.getNextNamespaceNode(paramInt, i, true)) {
          String str = dTM.getNodeNameX(i);
          this.m_contentHandler.startPrefixMapping(str, dTM.getNodeValue(i));
        } 
        str1 = dTM.getNamespaceURI(paramInt);
        if (null == str1)
          str1 = ""; 
        attributesImpl = new AttributesImpl();
        for (j = dTM.getFirstAttribute(paramInt); j != -1; j = dTM.getNextAttribute(j))
          attributesImpl.addAttribute(dTM.getNamespaceURI(j), dTM.getLocalName(j), dTM.getNodeName(j), "CDATA", dTM.getNodeValue(j)); 
        this.m_contentHandler.startElement(str1, this.m_dtm.getLocalName(paramInt), this.m_dtm.getNodeName(paramInt), attributesImpl);
        break;
      case 7:
        str2 = this.m_dtm.getNodeName(paramInt);
        if (str2.equals("xslt-next-is-raw")) {
          this.nextIsRaw = true;
          break;
        } 
        this.m_contentHandler.processingInstruction(str2, this.m_dtm.getNodeValue(paramInt));
        break;
      case 4:
        bool = this.m_contentHandler instanceof LexicalHandler;
        lexicalHandler = bool ? (LexicalHandler)this.m_contentHandler : null;
        if (bool)
          lexicalHandler.startCDATA(); 
        dispatachChars(paramInt);
        if (bool)
          lexicalHandler.endCDATA(); 
        break;
      case 3:
        if (this.nextIsRaw) {
          this.nextIsRaw = false;
          this.m_contentHandler.processingInstruction("javax.xml.transform.disable-output-escaping", "");
          dispatachChars(paramInt);
          this.m_contentHandler.processingInstruction("javax.xml.transform.enable-output-escaping", "");
          break;
        } 
        dispatachChars(paramInt);
        break;
      case 5:
        if (this.m_contentHandler instanceof LexicalHandler)
          ((LexicalHandler)this.m_contentHandler).startEntity(this.m_dtm.getNodeName(paramInt)); 
        break;
    } 
  }
  
  protected void endNode(int paramInt) throws SAXException {
    int i;
    String str;
    switch (this.m_dtm.getNodeType(paramInt)) {
      case 9:
        this.m_contentHandler.endDocument();
        break;
      case 1:
        str = this.m_dtm.getNamespaceURI(paramInt);
        if (null == str)
          str = ""; 
        this.m_contentHandler.endElement(str, this.m_dtm.getLocalName(paramInt), this.m_dtm.getNodeName(paramInt));
        for (i = this.m_dtm.getFirstNamespaceNode(paramInt, true); -1 != i; i = this.m_dtm.getNextNamespaceNode(paramInt, i, true)) {
          String str1 = this.m_dtm.getNodeNameX(i);
          this.m_contentHandler.endPrefixMapping(str1);
        } 
        break;
      case 5:
        if (this.m_contentHandler instanceof LexicalHandler) {
          LexicalHandler lexicalHandler = (LexicalHandler)this.m_contentHandler;
          lexicalHandler.endEntity(this.m_dtm.getNodeName(paramInt));
        } 
        break;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMTreeWalker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */