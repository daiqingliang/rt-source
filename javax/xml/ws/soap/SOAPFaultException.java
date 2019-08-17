package javax.xml.ws.soap;

import javax.xml.soap.SOAPFault;
import javax.xml.ws.ProtocolException;

public class SOAPFaultException extends ProtocolException {
  private SOAPFault fault;
  
  public SOAPFaultException(SOAPFault paramSOAPFault) {
    super(paramSOAPFault.getFaultString());
    this.fault = paramSOAPFault;
  }
  
  public SOAPFault getFault() { return this.fault; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\soap\SOAPFaultException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */