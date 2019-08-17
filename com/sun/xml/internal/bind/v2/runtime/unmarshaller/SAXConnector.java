package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshallerHandler;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public final class SAXConnector implements UnmarshallerHandler {
  private LocatorEx loc;
  
  private static final Logger logger = Util.getClassLogger();
  
  private final StringBuilder buffer = new StringBuilder();
  
  private final XmlVisitor next;
  
  private final UnmarshallingContext context;
  
  private final XmlVisitor.TextPredictor predictor;
  
  private final TagNameImpl tagName = new TagNameImpl(null);
  
  public SAXConnector(XmlVisitor paramXmlVisitor, LocatorEx paramLocatorEx) {
    this.next = paramXmlVisitor;
    this.context = paramXmlVisitor.getContext();
    this.predictor = paramXmlVisitor.getPredictor();
    this.loc = paramLocatorEx;
  }
  
  public Object getResult() throws JAXBException, IllegalStateException { return this.context.getResult(); }
  
  public UnmarshallingContext getContext() { return this.context; }
  
  public void setDocumentLocator(Locator paramLocator) {
    if (this.loc != null)
      return; 
    this.loc = new LocatorExWrapper(paramLocator);
  }
  
  public void startDocument() throws SAXException {
    if (logger.isLoggable(Level.FINER))
      logger.log(Level.FINER, "SAXConnector.startDocument"); 
    this.next.startDocument(this.loc, null);
  }
  
  public void endDocument() throws SAXException {
    if (logger.isLoggable(Level.FINER))
      logger.log(Level.FINER, "SAXConnector.endDocument"); 
    this.next.endDocument();
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    if (logger.isLoggable(Level.FINER))
      logger.log(Level.FINER, "SAXConnector.startPrefixMapping: {0}:{1}", new Object[] { paramString1, paramString2 }); 
    this.next.startPrefixMapping(paramString1, paramString2);
  }
  
  public void endPrefixMapping(String paramString) throws SAXException {
    if (logger.isLoggable(Level.FINER))
      logger.log(Level.FINER, "SAXConnector.endPrefixMapping: {0}", new Object[] { paramString }); 
    this.next.endPrefixMapping(paramString);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    if (logger.isLoggable(Level.FINER))
      logger.log(Level.FINER, "SAXConnector.startElement: {0}:{1}:{2}, attrs: {3}", new Object[] { paramString1, paramString2, paramString3, paramAttributes }); 
    if (paramString1 == null || paramString1.length() == 0)
      paramString1 = ""; 
    if (paramString2 == null || paramString2.length() == 0)
      paramString2 = paramString3; 
    if (paramString3 == null || paramString3.length() == 0)
      paramString3 = paramString2; 
    processText(!this.context.getCurrentState().isMixed());
    this.tagName.uri = paramString1;
    this.tagName.local = paramString2;
    this.tagName.qname = paramString3;
    this.tagName.atts = paramAttributes;
    this.next.startElement(this.tagName);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (logger.isLoggable(Level.FINER))
      logger.log(Level.FINER, "SAXConnector.startElement: {0}:{1}:{2}", new Object[] { paramString1, paramString2, paramString3 }); 
    processText(false);
    this.tagName.uri = paramString1;
    this.tagName.local = paramString2;
    this.tagName.qname = paramString3;
    this.next.endElement(this.tagName);
  }
  
  public final void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (logger.isLoggable(Level.FINEST))
      logger.log(Level.FINEST, "SAXConnector.characters: {0}", paramArrayOfChar); 
    if (this.predictor.expectText())
      this.buffer.append(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public final void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (logger.isLoggable(Level.FINEST))
      logger.log(Level.FINEST, "SAXConnector.characters{0}", paramArrayOfChar); 
    characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {}
  
  public void skippedEntity(String paramString) throws SAXException {}
  
  private void processText(boolean paramBoolean) throws SAXException {
    if (this.predictor.expectText() && (!paramBoolean || !WhiteSpaceProcessor.isWhiteSpace(this.buffer)))
      this.next.text(this.buffer); 
    this.buffer.setLength(0);
  }
  
  private static final class TagNameImpl extends TagName {
    String qname;
    
    private TagNameImpl() throws SAXException {}
    
    public String getQname() { return this.qname; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\SAXConnector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */