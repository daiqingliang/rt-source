package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownServiceException;
import javax.activation.DataSource;

public final class MimePartDataSource implements DataSource {
  private final MimeBodyPart part;
  
  public MimePartDataSource(MimeBodyPart paramMimeBodyPart) { this.part = paramMimeBodyPart; }
  
  public InputStream getInputStream() throws IOException {
    try {
      InputStream inputStream = this.part.getContentStream();
      String str = this.part.getEncoding();
      return (str != null) ? MimeUtility.decode(inputStream, str) : inputStream;
    } catch (MessagingException messagingException) {
      throw new IOException(messagingException.getMessage());
    } 
  }
  
  public OutputStream getOutputStream() throws IOException { throw new UnknownServiceException(); }
  
  public String getContentType() { return this.part.getContentType(); }
  
  public String getName() {
    try {
      return this.part.getFileName();
    } catch (MessagingException messagingException) {
      return "";
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\MimePartDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */