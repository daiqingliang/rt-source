package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SOAPMessageContextImpl extends MessageUpdatableContext implements SOAPMessageContext {
  private Set<String> roles;
  
  private SOAPMessage soapMsg = null;
  
  private WSBinding binding;
  
  public SOAPMessageContextImpl(WSBinding paramWSBinding, Packet paramPacket, Set<String> paramSet) {
    super(paramPacket);
    this.binding = paramWSBinding;
    this.roles = paramSet;
  }
  
  public SOAPMessage getMessage() {
    if (this.soapMsg == null)
      try {
        Message message = this.packet.getMessage();
        this.soapMsg = (message != null) ? message.readAsSOAPMessage() : null;
      } catch (SOAPException sOAPException) {
        throw new WebServiceException(sOAPException);
      }  
    return this.soapMsg;
  }
  
  public void setMessage(SOAPMessage paramSOAPMessage) {
    try {
      this.soapMsg = paramSOAPMessage;
    } catch (Exception exception) {
      throw new WebServiceException(exception);
    } 
  }
  
  void setPacketMessage(Message paramMessage) {
    if (paramMessage != null) {
      this.packet.setMessage(paramMessage);
      this.soapMsg = null;
    } 
  }
  
  protected void updateMessage() {
    if (this.soapMsg != null) {
      this.packet.setMessage(SAAJFactory.create(this.soapMsg));
      this.soapMsg = null;
    } 
  }
  
  public Object[] getHeaders(QName paramQName, JAXBContext paramJAXBContext, boolean paramBoolean) {
    SOAPVersion sOAPVersion = this.binding.getSOAPVersion();
    ArrayList arrayList = new ArrayList();
    try {
      Iterator iterator = this.packet.getMessage().getHeaders().getHeaders(paramQName, false);
      if (paramBoolean) {
        while (iterator.hasNext())
          arrayList.add(((Header)iterator.next()).readAsJAXB(paramJAXBContext.createUnmarshaller())); 
      } else {
        while (iterator.hasNext()) {
          Header header = (Header)iterator.next();
          String str = header.getRole(sOAPVersion);
          if (getRoles().contains(str))
            arrayList.add(header.readAsJAXB(paramJAXBContext.createUnmarshaller())); 
        } 
      } 
      return arrayList.toArray();
    } catch (Exception exception) {
      throw new WebServiceException(exception);
    } 
  }
  
  public Set<String> getRoles() { return this.roles; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\SOAPMessageContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */