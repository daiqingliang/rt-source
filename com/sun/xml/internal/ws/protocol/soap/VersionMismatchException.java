package com.sun.xml.internal.ws.protocol.soap;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.encoding.soap.SOAP12Constants;
import com.sun.xml.internal.ws.encoding.soap.SOAPConstants;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import javax.xml.namespace.QName;

public class VersionMismatchException extends ExceptionHasMessage {
  private final SOAPVersion soapVersion;
  
  public VersionMismatchException(SOAPVersion paramSOAPVersion, Object... paramVarArgs) {
    super("soap.version.mismatch.err", paramVarArgs);
    this.soapVersion = paramSOAPVersion;
  }
  
  public String getDefaultResourceBundleName() { return "com.sun.xml.internal.ws.resources.soap"; }
  
  public Message getFaultMessage() {
    QName qName = (this.soapVersion == SOAPVersion.SOAP_11) ? SOAPConstants.FAULT_CODE_VERSION_MISMATCH : SOAP12Constants.FAULT_CODE_VERSION_MISMATCH;
    return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, getLocalizedMessage(), qName);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\protocol\soap\VersionMismatchException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */