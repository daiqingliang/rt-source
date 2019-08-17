package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.api.BridgeContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

public final class BridgeContextImpl extends BridgeContext {
  public final UnmarshallerImpl unmarshaller;
  
  public final MarshallerImpl marshaller;
  
  BridgeContextImpl(JAXBContextImpl paramJAXBContextImpl) {
    this.unmarshaller = paramJAXBContextImpl.createUnmarshaller();
    this.marshaller = paramJAXBContextImpl.createMarshaller();
  }
  
  public void setErrorHandler(ValidationEventHandler paramValidationEventHandler) {
    try {
      this.unmarshaller.setEventHandler(paramValidationEventHandler);
      this.marshaller.setEventHandler(paramValidationEventHandler);
    } catch (JAXBException jAXBException) {
      throw new Error(jAXBException);
    } 
  }
  
  public void setAttachmentMarshaller(AttachmentMarshaller paramAttachmentMarshaller) { this.marshaller.setAttachmentMarshaller(paramAttachmentMarshaller); }
  
  public void setAttachmentUnmarshaller(AttachmentUnmarshaller paramAttachmentUnmarshaller) { this.unmarshaller.setAttachmentUnmarshaller(paramAttachmentUnmarshaller); }
  
  public AttachmentMarshaller getAttachmentMarshaller() { return this.marshaller.getAttachmentMarshaller(); }
  
  public AttachmentUnmarshaller getAttachmentUnmarshaller() { return this.unmarshaller.getAttachmentUnmarshaller(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\BridgeContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */