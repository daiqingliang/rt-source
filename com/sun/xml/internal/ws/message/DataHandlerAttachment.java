package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Attachment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;

public final class DataHandlerAttachment implements Attachment {
  private final DataHandler dh;
  
  private final String contentId;
  
  String contentIdNoAngleBracket;
  
  public DataHandlerAttachment(@NotNull String paramString, @NotNull DataHandler paramDataHandler) {
    this.dh = paramDataHandler;
    this.contentId = paramString;
  }
  
  public String getContentId() {
    if (this.contentIdNoAngleBracket == null) {
      this.contentIdNoAngleBracket = this.contentId;
      if (this.contentIdNoAngleBracket != null && this.contentIdNoAngleBracket.charAt(0) == '<')
        this.contentIdNoAngleBracket = this.contentIdNoAngleBracket.substring(1, this.contentIdNoAngleBracket.length() - 1); 
    } 
    return this.contentIdNoAngleBracket;
  }
  
  public String getContentType() { return this.dh.getContentType(); }
  
  public byte[] asByteArray() {
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      this.dh.writeTo(byteArrayOutputStream);
      return byteArrayOutputStream.toByteArray();
    } catch (IOException iOException) {
      throw new WebServiceException(iOException);
    } 
  }
  
  public DataHandler asDataHandler() { return this.dh; }
  
  public Source asSource() {
    try {
      return new StreamSource(this.dh.getInputStream());
    } catch (IOException iOException) {
      throw new WebServiceException(iOException);
    } 
  }
  
  public InputStream asInputStream() {
    try {
      return this.dh.getInputStream();
    } catch (IOException iOException) {
      throw new WebServiceException(iOException);
    } 
  }
  
  public void writeTo(OutputStream paramOutputStream) throws IOException { this.dh.writeTo(paramOutputStream); }
  
  public void writeTo(SOAPMessage paramSOAPMessage) throws SOAPException {
    AttachmentPart attachmentPart = paramSOAPMessage.createAttachmentPart();
    attachmentPart.setDataHandler(this.dh);
    attachmentPart.setContentId(this.contentId);
    paramSOAPMessage.addAttachmentPart(attachmentPart);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\DataHandlerAttachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */