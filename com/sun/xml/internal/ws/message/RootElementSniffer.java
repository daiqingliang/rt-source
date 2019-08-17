package com.sun.xml.internal.ws.message;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public final class RootElementSniffer extends DefaultHandler {
  private String nsUri = "##error";
  
  private String localName = "##error";
  
  private Attributes atts;
  
  private final boolean parseAttributes;
  
  private static final SAXException aSAXException = new SAXException();
  
  private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();
  
  public RootElementSniffer(boolean paramBoolean) { this.parseAttributes = paramBoolean; }
  
  public RootElementSniffer() { this(true); }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    this.nsUri = paramString1;
    this.localName = paramString2;
    if (this.parseAttributes)
      if (paramAttributes.getLength() == 0) {
        this.atts = EMPTY_ATTRIBUTES;
      } else {
        this.atts = new AttributesImpl(paramAttributes);
      }  
    throw aSAXException;
  }
  
  public String getNsUri() { return this.nsUri; }
  
  public String getLocalName() { return this.localName; }
  
  public Attributes getAttributes() { return this.atts; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\RootElementSniffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */