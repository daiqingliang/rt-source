package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import javax.activation.DataHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

public final class SwaRefAdapter extends XmlAdapter<String, DataHandler> {
  public DataHandler unmarshal(String paramString) {
    AttachmentUnmarshaller attachmentUnmarshaller = (UnmarshallingContext.getInstance()).parent.getAttachmentUnmarshaller();
    return attachmentUnmarshaller.getAttachmentAsDataHandler(paramString);
  }
  
  public String marshal(DataHandler paramDataHandler) {
    if (paramDataHandler == null)
      return null; 
    AttachmentMarshaller attachmentMarshaller = (XMLSerializer.getInstance()).attachmentMarshaller;
    return attachmentMarshaller.addSwaRefAttachment(paramDataHandler);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\SwaRefAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */