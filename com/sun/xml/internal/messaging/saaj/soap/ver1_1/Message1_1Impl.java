package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.internal.messaging.saaj.soap.MessageImpl;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

public class Message1_1Impl extends MessageImpl implements SOAPConstants {
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_1", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.LocalStrings");
  
  public Message1_1Impl() {}
  
  public Message1_1Impl(boolean paramBoolean1, boolean paramBoolean2) { super(paramBoolean1, paramBoolean2); }
  
  public Message1_1Impl(SOAPMessage paramSOAPMessage) { super(paramSOAPMessage); }
  
  public Message1_1Impl(MimeHeaders paramMimeHeaders, InputStream paramInputStream) throws IOException, SOAPExceptionImpl { super(paramMimeHeaders, paramInputStream); }
  
  public Message1_1Impl(MimeHeaders paramMimeHeaders, ContentType paramContentType, int paramInt, InputStream paramInputStream) throws SOAPExceptionImpl { super(paramMimeHeaders, paramContentType, paramInt, paramInputStream); }
  
  public SOAPPart getSOAPPart() {
    if (this.soapPartImpl == null)
      this.soapPartImpl = new SOAPPart1_1Impl(this); 
    return this.soapPartImpl;
  }
  
  protected boolean isCorrectSoapVersion(int paramInt) { return ((paramInt & 0x4) != 0); }
  
  public String getAction() {
    log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", new String[] { "Action" });
    throw new UnsupportedOperationException("Operation not supported by SOAP 1.1");
  }
  
  public void setAction(String paramString) {
    log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", new String[] { "Action" });
    throw new UnsupportedOperationException("Operation not supported by SOAP 1.1");
  }
  
  public String getCharset() {
    log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", new String[] { "Charset" });
    throw new UnsupportedOperationException("Operation not supported by SOAP 1.1");
  }
  
  public void setCharset(String paramString) {
    log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", new String[] { "Charset" });
    throw new UnsupportedOperationException("Operation not supported by SOAP 1.1");
  }
  
  protected String getExpectedContentType() { return this.isFastInfoset ? "application/fastinfoset" : "text/xml"; }
  
  protected String getExpectedAcceptHeader() {
    String str = "text/xml, text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
    return this.acceptFastInfoset ? ("application/fastinfoset, " + str) : str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_1\Message1_1Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */