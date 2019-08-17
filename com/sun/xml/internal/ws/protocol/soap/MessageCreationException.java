package com.sun.xml.internal.ws.protocol.soap;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import javax.xml.namespace.QName;

public class MessageCreationException extends ExceptionHasMessage {
  private final SOAPVersion soapVersion;
  
  public MessageCreationException(SOAPVersion paramSOAPVersion, Object... paramVarArgs) {
    super("soap.msg.create.err", paramVarArgs);
    this.soapVersion = paramSOAPVersion;
  }
  
  public String getDefaultResourceBundleName() { return "com.sun.xml.internal.ws.resources.soap"; }
  
  public Message getFaultMessage() {
    QName qName = this.soapVersion.faultCodeClient;
    return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, getLocalizedMessage(), qName);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\protocol\soap\MessageCreationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */