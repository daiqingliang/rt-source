package com.sun.xml.internal.ws.message;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.resources.EncodingMessages;
import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.ws.WebServiceException;

public final class AttachmentUnmarshallerImpl extends AttachmentUnmarshaller {
  private final AttachmentSet attachments;
  
  public AttachmentUnmarshallerImpl(AttachmentSet paramAttachmentSet) { this.attachments = paramAttachmentSet; }
  
  public DataHandler getAttachmentAsDataHandler(String paramString) {
    Attachment attachment = this.attachments.get(stripScheme(paramString));
    if (attachment == null)
      throw new WebServiceException(EncodingMessages.NO_SUCH_CONTENT_ID(paramString)); 
    return attachment.asDataHandler();
  }
  
  public byte[] getAttachmentAsByteArray(String paramString) {
    Attachment attachment = this.attachments.get(stripScheme(paramString));
    if (attachment == null)
      throw new WebServiceException(EncodingMessages.NO_SUCH_CONTENT_ID(paramString)); 
    return attachment.asByteArray();
  }
  
  private String stripScheme(String paramString) {
    if (paramString.startsWith("cid:"))
      paramString = paramString.substring(4); 
    return paramString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\AttachmentUnmarshallerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */