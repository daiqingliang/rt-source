package com.sun.xml.internal.ws.protocol.soap;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.message.DOMHeader;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

abstract class MUTube extends AbstractFilterTubeImpl {
  private static final String MU_FAULT_DETAIL_LOCALPART = "NotUnderstood";
  
  private static final QName MU_HEADER_DETAIL = new QName(SOAPVersion.SOAP_12.nsUri, "NotUnderstood");
  
  protected static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.soap.decoder");
  
  private static final String MUST_UNDERSTAND_FAULT_MESSAGE_STRING = "One or more mandatory SOAP header blocks not understood";
  
  protected final SOAPVersion soapVersion;
  
  protected SOAPBindingImpl binding;
  
  protected MUTube(WSBinding paramWSBinding, Tube paramTube) {
    super(paramTube);
    if (!(paramWSBinding instanceof javax.xml.ws.soap.SOAPBinding))
      throw new WebServiceException("MUPipe should n't be used for bindings other than SOAP."); 
    this.binding = (SOAPBindingImpl)paramWSBinding;
    this.soapVersion = paramWSBinding.getSOAPVersion();
  }
  
  protected MUTube(MUTube paramMUTube, TubeCloner paramTubeCloner) {
    super(paramMUTube, paramTubeCloner);
    this.binding = paramMUTube.binding;
    this.soapVersion = paramMUTube.soapVersion;
  }
  
  public final Set<QName> getMisUnderstoodHeaders(MessageHeaders paramMessageHeaders, Set<String> paramSet1, Set<QName> paramSet2) { return paramMessageHeaders.getNotUnderstoodHeaders(paramSet1, paramSet2, this.binding); }
  
  final SOAPFaultException createMUSOAPFaultException(Set<QName> paramSet) {
    try {
      SOAPFault sOAPFault = this.soapVersion.getSOAPFactory().createFault("One or more mandatory SOAP header blocks not understood", this.soapVersion.faultCodeMustUnderstand);
      sOAPFault.setFaultString("MustUnderstand headers:" + paramSet + " are not understood");
      return new SOAPFaultException(sOAPFault);
    } catch (SOAPException sOAPException) {
      throw new WebServiceException(sOAPException);
    } 
  }
  
  final Message createMUSOAPFaultMessage(Set<QName> paramSet) {
    try {
      String str = "One or more mandatory SOAP header blocks not understood";
      if (this.soapVersion == SOAPVersion.SOAP_11)
        str = "MustUnderstand headers:" + paramSet + " are not understood"; 
      Message message = SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, str, this.soapVersion.faultCodeMustUnderstand);
      if (this.soapVersion == SOAPVersion.SOAP_12)
        addHeader(message, paramSet); 
      return message;
    } catch (SOAPException sOAPException) {
      throw new WebServiceException(sOAPException);
    } 
  }
  
  private static void addHeader(Message paramMessage, Set<QName> paramSet) throws SOAPException {
    for (QName qName : paramSet) {
      SOAPElement sOAPElement = SOAPVersion.SOAP_12.getSOAPFactory().createElement(MU_HEADER_DETAIL);
      sOAPElement.addNamespaceDeclaration("abc", qName.getNamespaceURI());
      sOAPElement.setAttribute("qname", "abc:" + qName.getLocalPart());
      DOMHeader dOMHeader = new DOMHeader(sOAPElement);
      paramMessage.getHeaders().add(dOMHeader);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\protocol\soap\MUTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */