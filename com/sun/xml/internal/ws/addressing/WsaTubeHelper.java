package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;

public abstract class WsaTubeHelper {
  protected SEIModel seiModel;
  
  protected WSDLPort wsdlPort;
  
  protected WSBinding binding;
  
  protected final SOAPVersion soapVer;
  
  protected final AddressingVersion addVer;
  
  public WsaTubeHelper(WSBinding paramWSBinding, SEIModel paramSEIModel, WSDLPort paramWSDLPort) {
    this.binding = paramWSBinding;
    this.wsdlPort = paramWSDLPort;
    this.seiModel = paramSEIModel;
    this.soapVer = paramWSBinding.getSOAPVersion();
    this.addVer = paramWSBinding.getAddressingVersion();
  }
  
  public String getFaultAction(Packet paramPacket1, Packet paramPacket2) {
    String str = null;
    if (this.seiModel != null)
      str = getFaultActionFromSEIModel(paramPacket1, paramPacket2); 
    if (str != null)
      return str; 
    str = this.addVer.getDefaultFaultAction();
    if (this.wsdlPort != null) {
      WSDLOperationMapping wSDLOperationMapping = paramPacket1.getWSDLOperationMapping();
      if (wSDLOperationMapping != null) {
        WSDLBoundOperation wSDLBoundOperation = wSDLOperationMapping.getWSDLBoundOperation();
        return getFaultAction(wSDLBoundOperation, paramPacket2);
      } 
    } 
    return str;
  }
  
  String getFaultActionFromSEIModel(Packet paramPacket1, Packet paramPacket2) {
    String str = null;
    if (this.seiModel == null || this.wsdlPort == null)
      return str; 
    try {
      SOAPMessage sOAPMessage = paramPacket2.getMessage().copy().readAsSOAPMessage();
      if (sOAPMessage == null)
        return str; 
      if (sOAPMessage.getSOAPBody() == null)
        return str; 
      if (sOAPMessage.getSOAPBody().getFault() == null)
        return str; 
      Detail detail = sOAPMessage.getSOAPBody().getFault().getDetail();
      if (detail == null)
        return str; 
      String str1 = detail.getFirstChild().getNamespaceURI();
      String str2 = detail.getFirstChild().getLocalName();
      WSDLOperationMapping wSDLOperationMapping = paramPacket1.getWSDLOperationMapping();
      JavaMethodImpl javaMethodImpl = (wSDLOperationMapping != null) ? (JavaMethodImpl)wSDLOperationMapping.getJavaMethod() : null;
      if (javaMethodImpl != null)
        for (CheckedExceptionImpl checkedExceptionImpl : javaMethodImpl.getCheckedExceptions()) {
          if ((checkedExceptionImpl.getDetailType()).tagName.getLocalPart().equals(str2) && (checkedExceptionImpl.getDetailType()).tagName.getNamespaceURI().equals(str1))
            return checkedExceptionImpl.getFaultAction(); 
        }  
      return str;
    } catch (SOAPException sOAPException) {
      throw new WebServiceException(sOAPException);
    } 
  }
  
  String getFaultAction(@Nullable WSDLBoundOperation paramWSDLBoundOperation, Packet paramPacket) {
    String str = AddressingUtils.getAction(paramPacket.getMessage().getHeaders(), this.addVer, this.soapVer);
    if (str != null)
      return str; 
    str = this.addVer.getDefaultFaultAction();
    if (paramWSDLBoundOperation == null)
      return str; 
    try {
      SOAPMessage sOAPMessage = paramPacket.getMessage().copy().readAsSOAPMessage();
      if (sOAPMessage == null)
        return str; 
      if (sOAPMessage.getSOAPBody() == null)
        return str; 
      if (sOAPMessage.getSOAPBody().getFault() == null)
        return str; 
      Detail detail = sOAPMessage.getSOAPBody().getFault().getDetail();
      if (detail == null)
        return str; 
      String str1 = detail.getFirstChild().getNamespaceURI();
      String str2 = detail.getFirstChild().getLocalName();
      WSDLOperation wSDLOperation = paramWSDLBoundOperation.getOperation();
      WSDLFault wSDLFault = wSDLOperation.getFault(new QName(str1, str2));
      return (wSDLFault == null) ? str : wSDLFault.getAction();
    } catch (SOAPException sOAPException) {
      throw new WebServiceException(sOAPException);
    } 
  }
  
  public String getInputAction(Packet paramPacket) {
    String str = null;
    if (this.wsdlPort != null) {
      WSDLOperationMapping wSDLOperationMapping = paramPacket.getWSDLOperationMapping();
      if (wSDLOperationMapping != null) {
        WSDLBoundOperation wSDLBoundOperation = wSDLOperationMapping.getWSDLBoundOperation();
        WSDLOperation wSDLOperation = wSDLBoundOperation.getOperation();
        str = wSDLOperation.getInput().getAction();
      } 
    } 
    return str;
  }
  
  public String getEffectiveInputAction(Packet paramPacket) {
    String str;
    if (paramPacket.soapAction != null && !paramPacket.soapAction.equals(""))
      return paramPacket.soapAction; 
    if (this.wsdlPort != null) {
      WSDLOperationMapping wSDLOperationMapping = paramPacket.getWSDLOperationMapping();
      if (wSDLOperationMapping != null) {
        WSDLBoundOperation wSDLBoundOperation = wSDLOperationMapping.getWSDLBoundOperation();
        WSDLOperation wSDLOperation = wSDLBoundOperation.getOperation();
        str = wSDLOperation.getInput().getAction();
      } else {
        str = paramPacket.soapAction;
      } 
    } else {
      str = paramPacket.soapAction;
    } 
    return str;
  }
  
  public boolean isInputActionDefault(Packet paramPacket) {
    if (this.wsdlPort == null)
      return false; 
    WSDLOperationMapping wSDLOperationMapping = paramPacket.getWSDLOperationMapping();
    if (wSDLOperationMapping == null)
      return false; 
    WSDLBoundOperation wSDLBoundOperation = wSDLOperationMapping.getWSDLBoundOperation();
    WSDLOperation wSDLOperation = wSDLBoundOperation.getOperation();
    return wSDLOperation.getInput().isDefaultAction();
  }
  
  public String getSOAPAction(Packet paramPacket) {
    null = "";
    if (paramPacket == null || paramPacket.getMessage() == null)
      return null; 
    if (this.wsdlPort == null)
      return null; 
    WSDLOperationMapping wSDLOperationMapping = paramPacket.getWSDLOperationMapping();
    if (wSDLOperationMapping == null)
      return null; 
    WSDLBoundOperation wSDLBoundOperation = wSDLOperationMapping.getWSDLBoundOperation();
    return wSDLBoundOperation.getSOAPAction();
  }
  
  public String getOutputAction(Packet paramPacket) {
    String str = null;
    WSDLOperationMapping wSDLOperationMapping = paramPacket.getWSDLOperationMapping();
    if (wSDLOperationMapping != null) {
      JavaMethod javaMethod = wSDLOperationMapping.getJavaMethod();
      if (javaMethod != null) {
        JavaMethodImpl javaMethodImpl = (JavaMethodImpl)javaMethod;
        if (javaMethodImpl != null && javaMethodImpl.getOutputAction() != null && !javaMethodImpl.getOutputAction().equals(""))
          return javaMethodImpl.getOutputAction(); 
      } 
      WSDLBoundOperation wSDLBoundOperation = wSDLOperationMapping.getWSDLBoundOperation();
      if (wSDLBoundOperation != null)
        return getOutputAction(wSDLBoundOperation); 
    } 
    return str;
  }
  
  String getOutputAction(@Nullable WSDLBoundOperation paramWSDLBoundOperation) {
    String str = "http://jax-ws.dev.java.net/addressing/output-action-not-set";
    if (paramWSDLBoundOperation != null) {
      WSDLOutput wSDLOutput = paramWSDLBoundOperation.getOperation().getOutput();
      if (wSDLOutput != null)
        str = wSDLOutput.getAction(); 
    } 
    return str;
  }
  
  public SOAPFault createInvalidAddressingHeaderFault(InvalidAddressingHeaderException paramInvalidAddressingHeaderException, AddressingVersion paramAddressingVersion) {
    QName qName1 = paramInvalidAddressingHeaderException.getProblemHeader();
    QName qName2 = paramInvalidAddressingHeaderException.getSubsubcode();
    QName qName3 = paramAddressingVersion.invalidMapTag;
    String str = String.format(paramAddressingVersion.getInvalidMapText(), new Object[] { qName1, qName2 });
    try {
      SOAPFault sOAPFault;
      if (this.soapVer == SOAPVersion.SOAP_12) {
        SOAPFactory sOAPFactory = SOAPVersion.SOAP_12.getSOAPFactory();
        sOAPFault = sOAPFactory.createFault();
        sOAPFault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
        sOAPFault.appendFaultSubcode(qName3);
        sOAPFault.appendFaultSubcode(qName2);
        getInvalidMapDetail(qName1, sOAPFault.addDetail());
      } else {
        SOAPFactory sOAPFactory = SOAPVersion.SOAP_11.getSOAPFactory();
        sOAPFault = sOAPFactory.createFault();
        sOAPFault.setFaultCode(qName2);
      } 
      sOAPFault.setFaultString(str);
      return sOAPFault;
    } catch (SOAPException sOAPException) {
      throw new WebServiceException(sOAPException);
    } 
  }
  
  public SOAPFault newMapRequiredFault(MissingAddressingHeaderException paramMissingAddressingHeaderException) {
    QName qName1 = this.addVer.mapRequiredTag;
    QName qName2 = this.addVer.mapRequiredTag;
    String str = this.addVer.getMapRequiredText();
    try {
      SOAPFault sOAPFault;
      if (this.soapVer == SOAPVersion.SOAP_12) {
        SOAPFactory sOAPFactory = SOAPVersion.SOAP_12.getSOAPFactory();
        sOAPFault = sOAPFactory.createFault();
        sOAPFault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
        sOAPFault.appendFaultSubcode(qName1);
        sOAPFault.appendFaultSubcode(qName2);
        getMapRequiredDetail(paramMissingAddressingHeaderException.getMissingHeaderQName(), sOAPFault.addDetail());
      } else {
        SOAPFactory sOAPFactory = SOAPVersion.SOAP_11.getSOAPFactory();
        sOAPFault = sOAPFactory.createFault();
        sOAPFault.setFaultCode(qName2);
      } 
      sOAPFault.setFaultString(str);
      return sOAPFault;
    } catch (SOAPException sOAPException) {
      throw new WebServiceException(sOAPException);
    } 
  }
  
  public abstract void getProblemActionDetail(String paramString, Element paramElement);
  
  public abstract void getInvalidMapDetail(QName paramQName, Element paramElement);
  
  public abstract void getMapRequiredDetail(QName paramQName, Element paramElement);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\addressing\WsaTubeHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */