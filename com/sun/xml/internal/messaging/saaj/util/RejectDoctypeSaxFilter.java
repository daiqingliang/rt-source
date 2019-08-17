package com.sun.xml.internal.messaging.saaj.util;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.soap.SOAPException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

public class RejectDoctypeSaxFilter extends XMLFilterImpl implements XMLReader, LexicalHandler {
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.util", "com.sun.xml.internal.messaging.saaj.util.LocalStrings");
  
  static final String LEXICAL_HANDLER_PROP = "http://xml.org/sax/properties/lexical-handler";
  
  static final String WSU_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd".intern();
  
  static final String SIGNATURE_LNAME = "Signature".intern();
  
  static final String ENCRYPTED_DATA_LNAME = "EncryptedData".intern();
  
  static final String DSIG_NS = "http://www.w3.org/2000/09/xmldsig#".intern();
  
  static final String XENC_NS = "http://www.w3.org/2001/04/xmlenc#".intern();
  
  static final String ID_NAME = "ID".intern();
  
  private LexicalHandler lexicalHandler;
  
  public RejectDoctypeSaxFilter(SAXParser paramSAXParser) throws SOAPException {
    try {
      xMLReader = paramSAXParser.getXMLReader();
    } catch (Exception exception) {
      log.severe("SAAJ0602.util.getXMLReader.exception");
      throw new SOAPExceptionImpl("Couldn't get an XMLReader while constructing a RejectDoctypeSaxFilter", exception);
    } 
    try {
      xMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
    } catch (Exception exception) {
      log.severe("SAAJ0603.util.setProperty.exception");
      throw new SOAPExceptionImpl("Couldn't set the lexical handler property while constructing a RejectDoctypeSaxFilter", exception);
    } 
    setParent(xMLReader);
  }
  
  public void setProperty(String paramString, Object paramObject) throws SAXNotRecognizedException, SAXNotSupportedException {
    if ("http://xml.org/sax/properties/lexical-handler".equals(paramString)) {
      this.lexicalHandler = (LexicalHandler)paramObject;
    } else {
      super.setProperty(paramString, paramObject);
    } 
  }
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException { throw new SAXException("Document Type Declaration is not allowed"); }
  
  public void endDTD() throws SAXException {}
  
  public void startEntity(String paramString) throws SAXException {
    if (this.lexicalHandler != null)
      this.lexicalHandler.startEntity(paramString); 
  }
  
  public void endEntity(String paramString) throws SAXException {
    if (this.lexicalHandler != null)
      this.lexicalHandler.endEntity(paramString); 
  }
  
  public void startCDATA() throws SAXException {
    if (this.lexicalHandler != null)
      this.lexicalHandler.startCDATA(); 
  }
  
  public void endCDATA() throws SAXException {
    if (this.lexicalHandler != null)
      this.lexicalHandler.endCDATA(); 
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.lexicalHandler != null)
      this.lexicalHandler.comment(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    if (paramAttributes != null) {
      boolean bool = false;
      if (paramString1 == DSIG_NS || XENC_NS == paramString1)
        bool = true; 
      int i = paramAttributes.getLength();
      AttributesImpl attributesImpl = new AttributesImpl();
      for (byte b = 0; b < i; b++) {
        String str = paramAttributes.getLocalName(b);
        if (str != null && str.equals("Id")) {
          if (bool || paramAttributes.getURI(b) == WSU_NS) {
            attributesImpl.addAttribute(paramAttributes.getURI(b), paramAttributes.getLocalName(b), paramAttributes.getQName(b), ID_NAME, paramAttributes.getValue(b));
          } else {
            attributesImpl.addAttribute(paramAttributes.getURI(b), paramAttributes.getLocalName(b), paramAttributes.getQName(b), paramAttributes.getType(b), paramAttributes.getValue(b));
          } 
        } else {
          attributesImpl.addAttribute(paramAttributes.getURI(b), paramAttributes.getLocalName(b), paramAttributes.getQName(b), paramAttributes.getType(b), paramAttributes.getValue(b));
        } 
      } 
      super.startElement(paramString1, paramString2, paramString3, attributesImpl);
    } else {
      super.startElement(paramString1, paramString2, paramString3, null);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saa\\util\RejectDoctypeSaxFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */