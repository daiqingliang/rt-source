package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.soap.AttachmentPartImpl;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEConfig;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.activation.DataSource;

public class MimePullMultipart extends MimeMultipart {
  private InputStream in = null;
  
  private String boundary = null;
  
  private MIMEMessage mm = null;
  
  private DataSource dataSource = null;
  
  private ContentType contType = null;
  
  private String startParam = null;
  
  private MIMEPart soapPart = null;
  
  public MimePullMultipart(DataSource paramDataSource, ContentType paramContentType) throws MessagingException {
    if (paramContentType == null) {
      this.contType = new ContentType(paramDataSource.getContentType());
    } else {
      this.contType = paramContentType;
    } 
    this.dataSource = paramDataSource;
    this.boundary = this.contType.getParameter("boundary");
  }
  
  public MIMEPart readAndReturnSOAPPart() throws MessagingException {
    if (this.soapPart != null)
      throw new MessagingException("Inputstream from datasource was already consumed"); 
    readSOAPPart();
    return this.soapPart;
  }
  
  protected void readSOAPPart() throws MessagingException {
    try {
      if (this.soapPart != null)
        return; 
      this.in = this.dataSource.getInputStream();
      MIMEConfig mIMEConfig = new MIMEConfig();
      this.mm = new MIMEMessage(this.in, this.boundary, mIMEConfig);
      String str = this.contType.getParameter("start");
      if (this.startParam == null) {
        this.soapPart = this.mm.getPart(0);
      } else {
        if (str != null && str.length() > 2 && str.charAt(0) == '<' && str.charAt(str.length() - 1) == '>')
          str = str.substring(1, str.length() - 1); 
        this.startParam = str;
        this.soapPart = this.mm.getPart(this.startParam);
      } 
    } catch (IOException iOException) {
      throw new MessagingException("No inputstream from datasource", iOException);
    } 
  }
  
  public void parseAll() throws MessagingException {
    if (this.parsed)
      return; 
    if (this.soapPart == null)
      readSOAPPart(); 
    List list = this.mm.getAttachments();
    for (MIMEPart mIMEPart : list) {
      if (mIMEPart != this.soapPart) {
        AttachmentPartImpl attachmentPartImpl = new AttachmentPartImpl(mIMEPart);
        addBodyPart(new MimeBodyPart(mIMEPart));
      } 
    } 
    this.parsed = true;
  }
  
  protected void parse() throws MessagingException { parseAll(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\MimePullMultipart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */