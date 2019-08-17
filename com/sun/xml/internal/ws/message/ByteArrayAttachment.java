package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.encoding.DataSourceStreamingDataHandler;
import com.sun.xml.internal.ws.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public final class ByteArrayAttachment implements Attachment {
  private final String contentId;
  
  private byte[] data;
  
  private int start;
  
  private final int len;
  
  private final String mimeType;
  
  public ByteArrayAttachment(@NotNull String paramString1, byte[] paramArrayOfByte, int paramInt1, int paramInt2, String paramString2) {
    this.contentId = paramString1;
    this.data = paramArrayOfByte;
    this.start = paramInt1;
    this.len = paramInt2;
    this.mimeType = paramString2;
  }
  
  public ByteArrayAttachment(@NotNull String paramString1, byte[] paramArrayOfByte, String paramString2) { this(paramString1, paramArrayOfByte, 0, paramArrayOfByte.length, paramString2); }
  
  public String getContentId() { return this.contentId; }
  
  public String getContentType() { return this.mimeType; }
  
  public byte[] asByteArray() {
    if (this.start != 0 || this.len != this.data.length) {
      byte[] arrayOfByte = new byte[this.len];
      System.arraycopy(this.data, this.start, arrayOfByte, 0, this.len);
      this.start = 0;
      this.data = arrayOfByte;
    } 
    return this.data;
  }
  
  public DataHandler asDataHandler() { return new DataSourceStreamingDataHandler(new ByteArrayDataSource(this.data, this.start, this.len, getContentType())); }
  
  public Source asSource() { return new StreamSource(asInputStream()); }
  
  public InputStream asInputStream() { return new ByteArrayInputStream(this.data, this.start, this.len); }
  
  public void writeTo(OutputStream paramOutputStream) throws IOException { paramOutputStream.write(asByteArray()); }
  
  public void writeTo(SOAPMessage paramSOAPMessage) throws SOAPException {
    AttachmentPart attachmentPart = paramSOAPMessage.createAttachmentPart();
    attachmentPart.setDataHandler(asDataHandler());
    attachmentPart.setContentId(this.contentId);
    paramSOAPMessage.addAttachmentPart(attachmentPart);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\ByteArrayAttachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */