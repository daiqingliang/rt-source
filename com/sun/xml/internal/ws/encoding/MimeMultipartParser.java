package com.sun.xml.internal.ws.encoding;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.org.jvnet.mimepull.Header;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentEx;
import com.sun.xml.internal.ws.developer.StreamingAttachmentFeature;
import com.sun.xml.internal.ws.developer.StreamingDataHandler;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;

public final class MimeMultipartParser {
  private final String start;
  
  private final MIMEMessage message;
  
  private Attachment root;
  
  private ContentTypeImpl contentType;
  
  private final Map<String, Attachment> attachments = new HashMap();
  
  private boolean gotAll;
  
  public MimeMultipartParser(InputStream paramInputStream, String paramString, StreamingAttachmentFeature paramStreamingAttachmentFeature) {
    this.contentType = new ContentTypeImpl(paramString);
    String str1 = this.contentType.getBoundary();
    if (str1 == null || str1.equals(""))
      throw new WebServiceException("MIME boundary parameter not found" + this.contentType); 
    this.message = (paramStreamingAttachmentFeature != null) ? new MIMEMessage(paramInputStream, str1, paramStreamingAttachmentFeature.getConfig()) : new MIMEMessage(paramInputStream, str1);
    String str2 = this.contentType.getRootId();
    if (str2 != null && str2.length() > 2 && str2.charAt(0) == '<' && str2.charAt(str2.length() - 1) == '>')
      str2 = str2.substring(1, str2.length() - 1); 
    this.start = str2;
  }
  
  @Nullable
  public Attachment getRootPart() {
    if (this.root == null)
      this.root = new PartAttachment((this.start != null) ? this.message.getPart(this.start) : this.message.getPart(0)); 
    return this.root;
  }
  
  @NotNull
  public Map<String, Attachment> getAttachmentParts() {
    if (!this.gotAll) {
      MIMEPart mIMEPart = (this.start != null) ? this.message.getPart(this.start) : this.message.getPart(0);
      List list = this.message.getAttachments();
      for (MIMEPart mIMEPart1 : list) {
        if (mIMEPart1 != mIMEPart) {
          String str = mIMEPart1.getContentId();
          if (!this.attachments.containsKey(str)) {
            PartAttachment partAttachment = new PartAttachment(mIMEPart1);
            this.attachments.put(partAttachment.getContentId(), partAttachment);
          } 
        } 
      } 
      this.gotAll = true;
    } 
    return this.attachments;
  }
  
  @Nullable
  public Attachment getAttachmentPart(String paramString) throws IOException {
    Attachment attachment = (Attachment)this.attachments.get(paramString);
    if (attachment == null) {
      MIMEPart mIMEPart = this.message.getPart(paramString);
      attachment = new PartAttachment(mIMEPart);
      this.attachments.put(paramString, attachment);
    } 
    return attachment;
  }
  
  public ContentTypeImpl getContentType() { return this.contentType; }
  
  static class PartAttachment implements AttachmentEx {
    final MIMEPart part;
    
    byte[] buf;
    
    private StreamingDataHandler streamingDataHandler;
    
    PartAttachment(MIMEPart param1MIMEPart) { this.part = param1MIMEPart; }
    
    @NotNull
    public String getContentId() { return this.part.getContentId(); }
    
    @NotNull
    public String getContentType() { return this.part.getContentType(); }
    
    public byte[] asByteArray() {
      if (this.buf == null) {
        byteArrayBuffer = new ByteArrayBuffer();
        try {
          byteArrayBuffer.write(this.part.readOnce());
        } catch (IOException iOException) {
          throw new WebServiceException(iOException);
        } finally {
          if (byteArrayBuffer != null)
            try {
              byteArrayBuffer.close();
            } catch (IOException iOException) {
              Logger.getLogger(MimeMultipartParser.class.getName()).log(Level.FINE, null, iOException);
            }  
        } 
        this.buf = byteArrayBuffer.toByteArray();
      } 
      return this.buf;
    }
    
    public DataHandler asDataHandler() {
      if (this.streamingDataHandler == null)
        this.streamingDataHandler = (this.buf != null) ? new DataSourceStreamingDataHandler(new ByteArrayDataSource(this.buf, getContentType())) : new MIMEPartStreamingDataHandler(this.part); 
      return this.streamingDataHandler;
    }
    
    public Source asSource() { return (this.buf != null) ? new StreamSource(new ByteArrayInputStream(this.buf)) : new StreamSource(this.part.read()); }
    
    public InputStream asInputStream() { return (this.buf != null) ? new ByteArrayInputStream(this.buf) : this.part.read(); }
    
    public void writeTo(OutputStream param1OutputStream) throws IOException {
      if (this.buf != null) {
        param1OutputStream.write(this.buf);
      } else {
        InputStream inputStream = this.part.read();
        byte[] arrayOfByte = new byte[8192];
        int i;
        while ((i = inputStream.read(arrayOfByte)) != -1)
          param1OutputStream.write(arrayOfByte, 0, i); 
        inputStream.close();
      } 
    }
    
    public void writeTo(SOAPMessage param1SOAPMessage) throws SOAPException { param1SOAPMessage.createAttachmentPart().setDataHandler(asDataHandler()); }
    
    public Iterator<AttachmentEx.MimeHeader> getMimeHeaders() {
      final Iterator ih = this.part.getAllHeaders().iterator();
      return new Iterator<AttachmentEx.MimeHeader>() {
          public boolean hasNext() { return ih.hasNext(); }
          
          public AttachmentEx.MimeHeader next() {
            final Header hdr = (Header)ih.next();
            return new AttachmentEx.MimeHeader() {
                public String getValue() { return hdr.getValue(); }
                
                public String getName() { return hdr.getName(); }
              };
          }
          
          public void remove() { throw new UnsupportedOperationException(); }
        };
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\MimeMultipartParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */