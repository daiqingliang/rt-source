package com.sun.xml.internal.ws.message.stream;

import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.encoding.DataSourceStreamingDataHandler;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
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

public class StreamAttachment implements Attachment {
  private final String contentId;
  
  private final String contentType;
  
  private final ByteArrayBuffer byteArrayBuffer;
  
  private final byte[] data;
  
  private final int len;
  
  public StreamAttachment(ByteArrayBuffer paramByteArrayBuffer, String paramString1, String paramString2) {
    this.contentId = paramString1;
    this.contentType = paramString2;
    this.byteArrayBuffer = paramByteArrayBuffer;
    this.data = this.byteArrayBuffer.getRawData();
    this.len = this.byteArrayBuffer.size();
  }
  
  public String getContentId() { return this.contentId; }
  
  public String getContentType() { return this.contentType; }
  
  public byte[] asByteArray() { return this.byteArrayBuffer.toByteArray(); }
  
  public DataHandler asDataHandler() { return new DataSourceStreamingDataHandler(new ByteArrayDataSource(this.data, 0, this.len, getContentType())); }
  
  public Source asSource() { return new StreamSource(new ByteArrayInputStream(this.data, 0, this.len)); }
  
  public InputStream asInputStream() { return this.byteArrayBuffer.newInputStream(); }
  
  public Base64Data asBase64Data() {
    Base64Data base64Data = new Base64Data();
    base64Data.set(this.data, this.len, this.contentType);
    return base64Data;
  }
  
  public void writeTo(OutputStream paramOutputStream) throws IOException { this.byteArrayBuffer.writeTo(paramOutputStream); }
  
  public void writeTo(SOAPMessage paramSOAPMessage) throws SOAPException {
    AttachmentPart attachmentPart = paramSOAPMessage.createAttachmentPart();
    attachmentPart.setRawContentBytes(this.data, 0, this.len, getContentType());
    attachmentPart.setContentId(this.contentId);
    paramSOAPMessage.addAttachmentPart(attachmentPart);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\stream\StreamAttachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */