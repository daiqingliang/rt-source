package com.sun.xml.internal.ws.message.jaxb;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.logging.Level;
import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.ws.WebServiceException;

final class AttachmentMarshallerImpl extends AttachmentMarshaller {
  private static final Logger LOGGER = Logger.getLogger(AttachmentMarshallerImpl.class);
  
  private AttachmentSet attachments;
  
  public AttachmentMarshallerImpl(AttachmentSet paramAttachmentSet) { this.attachments = paramAttachmentSet; }
  
  void cleanup() { this.attachments = null; }
  
  public String addMtomAttachment(DataHandler paramDataHandler, String paramString1, String paramString2) { throw new IllegalStateException(); }
  
  public String addMtomAttachment(byte[] paramArrayOfByte, int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3) { throw new IllegalStateException(); }
  
  public String addSwaRefAttachment(DataHandler paramDataHandler) {
    null = encodeCid(null);
    DataHandlerAttachment dataHandlerAttachment = new DataHandlerAttachment(null, paramDataHandler);
    this.attachments.add(dataHandlerAttachment);
    return "cid:" + null;
  }
  
  private String encodeCid(String paramString) {
    String str1 = "example.jaxws.sun.com";
    String str2 = UUID.randomUUID() + "@";
    if (paramString != null && paramString.length() > 0)
      try {
        URI uRI = new URI(paramString);
        str1 = uRI.toURL().getHost();
      } catch (URISyntaxException uRISyntaxException) {
        if (LOGGER.isLoggable(Level.INFO))
          LOGGER.log(Level.INFO, null, uRISyntaxException); 
        return null;
      } catch (MalformedURLException malformedURLException) {
        try {
          str1 = URLEncoder.encode(paramString, "UTF-8");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
          throw new WebServiceException(malformedURLException);
        } 
      }  
    return str2 + str1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\jaxb\AttachmentMarshallerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */