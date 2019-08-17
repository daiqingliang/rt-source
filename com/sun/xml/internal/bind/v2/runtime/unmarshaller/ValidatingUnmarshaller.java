package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.util.FatalAdapter;
import javax.xml.namespace.NamespaceContext;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;
import org.xml.sax.SAXException;

final class ValidatingUnmarshaller implements XmlVisitor, XmlVisitor.TextPredictor {
  private final XmlVisitor next;
  
  private final ValidatorHandler validator;
  
  private NamespaceContext nsContext = null;
  
  private final XmlVisitor.TextPredictor predictor;
  
  private char[] buf = new char[256];
  
  public ValidatingUnmarshaller(Schema paramSchema, XmlVisitor paramXmlVisitor) {
    this.validator = paramSchema.newValidatorHandler();
    this.next = paramXmlVisitor;
    this.predictor = paramXmlVisitor.getPredictor();
    this.validator.setErrorHandler(new FatalAdapter(getContext()));
  }
  
  public void startDocument(LocatorEx paramLocatorEx, NamespaceContext paramNamespaceContext) throws SAXException {
    this.nsContext = paramNamespaceContext;
    this.validator.setDocumentLocator(paramLocatorEx);
    this.validator.startDocument();
    this.next.startDocument(paramLocatorEx, paramNamespaceContext);
  }
  
  public void endDocument() throws SAXException {
    this.nsContext = null;
    this.validator.endDocument();
    this.next.endDocument();
  }
  
  public void startElement(TagName paramTagName) throws SAXException {
    if (this.nsContext != null) {
      String str = paramTagName.getPrefix().intern();
      if (str != "")
        this.validator.startPrefixMapping(str, this.nsContext.getNamespaceURI(str)); 
    } 
    this.validator.startElement(paramTagName.uri, paramTagName.local, paramTagName.getQname(), paramTagName.atts);
    this.next.startElement(paramTagName);
  }
  
  public void endElement(TagName paramTagName) throws SAXException {
    this.validator.endElement(paramTagName.uri, paramTagName.local, paramTagName.getQname());
    this.next.endElement(paramTagName);
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    this.validator.startPrefixMapping(paramString1, paramString2);
    this.next.startPrefixMapping(paramString1, paramString2);
  }
  
  public void endPrefixMapping(String paramString) throws SAXException {
    this.validator.endPrefixMapping(paramString);
    this.next.endPrefixMapping(paramString);
  }
  
  public void text(CharSequence paramCharSequence) throws SAXException {
    int i = paramCharSequence.length();
    if (this.buf.length < i)
      this.buf = new char[i]; 
    for (byte b = 0; b < i; b++)
      this.buf[b] = paramCharSequence.charAt(b); 
    this.validator.characters(this.buf, 0, i);
    if (this.predictor.expectText())
      this.next.text(paramCharSequence); 
  }
  
  public UnmarshallingContext getContext() { return this.next.getContext(); }
  
  public XmlVisitor.TextPredictor getPredictor() { return this; }
  
  @Deprecated
  public boolean expectText() { return true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\ValidatingUnmarshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */