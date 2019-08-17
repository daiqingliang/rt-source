package com.oracle.webservices.internal.api.message;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public interface MessageContext extends DistributedPropertySet {
  SOAPMessage getAsSOAPMessage() throws SOAPException;
  
  SOAPMessage getSOAPMessage() throws SOAPException;
  
  ContentType writeTo(OutputStream paramOutputStream) throws IOException;
  
  ContentType getContentType();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\webservices\internal\api\message\MessageContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */