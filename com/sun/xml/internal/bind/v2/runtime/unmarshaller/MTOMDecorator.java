package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import org.xml.sax.SAXException;

final class MTOMDecorator implements XmlVisitor {
  private final XmlVisitor next;
  
  private final AttachmentUnmarshaller au;
  
  private UnmarshallerImpl parent;
  
  private final Base64Data base64data = new Base64Data();
  
  private boolean inXopInclude;
  
  private boolean followXop;
  
  public MTOMDecorator(UnmarshallerImpl paramUnmarshallerImpl, XmlVisitor paramXmlVisitor, AttachmentUnmarshaller paramAttachmentUnmarshaller) {
    this.parent = paramUnmarshallerImpl;
    this.next = paramXmlVisitor;
    this.au = paramAttachmentUnmarshaller;
  }
  
  public void startDocument(LocatorEx paramLocatorEx, NamespaceContext paramNamespaceContext) throws SAXException { this.next.startDocument(paramLocatorEx, paramNamespaceContext); }
  
  public void endDocument() throws SAXException { this.next.endDocument(); }
  
  public void startElement(TagName paramTagName) throws SAXException {
    if (paramTagName.local.equals("Include") && paramTagName.uri.equals("http://www.w3.org/2004/08/xop/include")) {
      String str = paramTagName.atts.getValue("href");
      DataHandler dataHandler = this.au.getAttachmentAsDataHandler(str);
      if (dataHandler == null)
        this.parent.getEventHandler().handleEvent(null); 
      this.base64data.set(dataHandler);
      this.next.text(this.base64data);
      this.inXopInclude = true;
      this.followXop = true;
    } else {
      this.next.startElement(paramTagName);
    } 
  }
  
  public void endElement(TagName paramTagName) throws SAXException {
    if (this.inXopInclude) {
      this.inXopInclude = false;
      this.followXop = true;
      return;
    } 
    this.next.endElement(paramTagName);
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException { this.next.startPrefixMapping(paramString1, paramString2); }
  
  public void endPrefixMapping(String paramString) throws SAXException { this.next.endPrefixMapping(paramString); }
  
  public void text(CharSequence paramCharSequence) throws SAXException {
    if (!this.followXop) {
      this.next.text(paramCharSequence);
    } else {
      this.followXop = false;
    } 
  }
  
  public UnmarshallingContext getContext() { return this.next.getContext(); }
  
  public XmlVisitor.TextPredictor getPredictor() { return this.next.getPredictor(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\MTOMDecorator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */