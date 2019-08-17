package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParseException;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.Message1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.Message1_2Impl;
import com.sun.xml.internal.messaging.saaj.util.TeeInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public class MessageFactoryImpl extends MessageFactory {
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
  
  protected OutputStream listener;
  
  protected boolean lazyAttachments = false;
  
  public OutputStream listen(OutputStream paramOutputStream) {
    OutputStream outputStream = this.listener;
    this.listener = paramOutputStream;
    return outputStream;
  }
  
  public SOAPMessage createMessage() throws SOAPException { throw new UnsupportedOperationException(); }
  
  public SOAPMessage createMessage(boolean paramBoolean1, boolean paramBoolean2) throws SOAPException { throw new UnsupportedOperationException(); }
  
  public SOAPMessage createMessage(MimeHeaders paramMimeHeaders, InputStream paramInputStream) throws SOAPException, IOException {
    String str = MessageImpl.getContentType(paramMimeHeaders);
    if (this.listener != null)
      paramInputStream = new TeeInputStream(paramInputStream, this.listener); 
    try {
      ContentType contentType = new ContentType(str);
      int i = MessageImpl.identifyContentType(contentType);
      if (MessageImpl.isSoap1_1Content(i))
        return new Message1_1Impl(paramMimeHeaders, contentType, i, paramInputStream); 
      if (MessageImpl.isSoap1_2Content(i))
        return new Message1_2Impl(paramMimeHeaders, contentType, i, paramInputStream); 
      log.severe("SAAJ0530.soap.unknown.Content-Type");
      throw new SOAPExceptionImpl("Unrecognized Content-Type");
    } catch (ParseException parseException) {
      log.severe("SAAJ0531.soap.cannot.parse.Content-Type");
      throw new SOAPExceptionImpl("Unable to parse content type: " + parseException.getMessage());
    } 
  }
  
  protected static final String getContentType(MimeHeaders paramMimeHeaders) {
    String[] arrayOfString = paramMimeHeaders.getHeader("Content-Type");
    return (arrayOfString == null) ? null : arrayOfString[0];
  }
  
  public void setLazyAttachmentOptimization(boolean paramBoolean) { this.lazyAttachments = paramBoolean; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\MessageFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */