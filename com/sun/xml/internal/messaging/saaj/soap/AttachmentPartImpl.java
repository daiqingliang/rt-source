package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.packaging.mime.Header;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.InternetHeaders;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePartDataSource;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import com.sun.xml.internal.org.jvnet.mimepull.Header;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.CommandInfo;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.MailcapCommandMap;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;

public class AttachmentPartImpl extends AttachmentPart {
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
  
  private final MimeHeaders headers = new MimeHeaders();
  
  private MimeBodyPart rawContent = null;
  
  private DataHandler dataHandler = null;
  
  private MIMEPart mimePart = null;
  
  public AttachmentPartImpl() { initializeJavaActivationHandlers(); }
  
  public AttachmentPartImpl(MIMEPart paramMIMEPart) {
    this.mimePart = paramMIMEPart;
    List list = paramMIMEPart.getAllHeaders();
    for (Header header : list)
      this.headers.addHeader(header.getName(), header.getValue()); 
  }
  
  public int getSize() throws SOAPException {
    if (this.mimePart != null)
      try {
        return this.mimePart.read().available();
      } catch (IOException iOException) {
        return -1;
      }  
    if (this.rawContent == null && this.dataHandler == null)
      return 0; 
    if (this.rawContent != null)
      try {
        return this.rawContent.getSize();
      } catch (Exception exception) {
        log.log(Level.SEVERE, "SAAJ0573.soap.attachment.getrawbytes.ioexception", new String[] { exception.getLocalizedMessage() });
        throw new SOAPExceptionImpl("Raw InputStream Error: " + exception);
      }  
    ByteOutputStream byteOutputStream = new ByteOutputStream();
    try {
      this.dataHandler.writeTo(byteOutputStream);
    } catch (IOException iOException) {
      log.log(Level.SEVERE, "SAAJ0501.soap.data.handler.err", new String[] { iOException.getLocalizedMessage() });
      throw new SOAPExceptionImpl("Data handler error: " + iOException);
    } 
    return byteOutputStream.size();
  }
  
  public void clearContent() {
    if (this.mimePart != null) {
      this.mimePart.close();
      this.mimePart = null;
    } 
    this.dataHandler = null;
    this.rawContent = null;
  }
  
  public Object getContent() throws SOAPException {
    try {
      if (this.mimePart != null)
        return this.mimePart.read(); 
      if (this.dataHandler != null)
        return getDataHandler().getContent(); 
      if (this.rawContent != null)
        return this.rawContent.getContent(); 
      log.severe("SAAJ0572.soap.no.content.for.attachment");
      throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
    } catch (Exception exception) {
      log.log(Level.SEVERE, "SAAJ0575.soap.attachment.getcontent.exception", exception);
      throw new SOAPExceptionImpl(exception.getLocalizedMessage());
    } 
  }
  
  public void setContent(Object paramObject, String paramString) throws IllegalArgumentException {
    if (this.mimePart != null) {
      this.mimePart.close();
      this.mimePart = null;
    } 
    DataHandler dataHandler1 = new DataHandler(paramObject, paramString);
    setDataHandler(dataHandler1);
  }
  
  public DataHandler getDataHandler() throws SOAPException {
    if (this.mimePart != null)
      return new DataHandler(new DataSource(this) {
            public InputStream getInputStream() throws SOAPException { return AttachmentPartImpl.this.mimePart.read(); }
            
            public OutputStream getOutputStream() throws IOException { throw new UnsupportedOperationException("getOutputStream cannot be supported : You have enabled LazyAttachments Option"); }
            
            public String getContentType() { return AttachmentPartImpl.this.mimePart.getContentType(); }
            
            public String getName() { return "MIMEPart Wrapper DataSource"; }
          }); 
    if (this.dataHandler == null) {
      if (this.rawContent != null)
        return new DataHandler(new MimePartDataSource(this.rawContent)); 
      log.severe("SAAJ0502.soap.no.handler.for.attachment");
      throw new SOAPExceptionImpl("No data handler associated with this attachment");
    } 
    return this.dataHandler;
  }
  
  public void setDataHandler(DataHandler paramDataHandler) throws IllegalArgumentException {
    if (this.mimePart != null) {
      this.mimePart.close();
      this.mimePart = null;
    } 
    if (paramDataHandler == null) {
      log.severe("SAAJ0503.soap.no.null.to.dataHandler");
      throw new IllegalArgumentException("Null dataHandler argument to setDataHandler");
    } 
    this.dataHandler = paramDataHandler;
    this.rawContent = null;
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "SAAJ0580.soap.set.Content-Type", new String[] { paramDataHandler.getContentType() }); 
    setMimeHeader("Content-Type", paramDataHandler.getContentType());
  }
  
  public void removeAllMimeHeaders() { this.headers.removeAllHeaders(); }
  
  public void removeMimeHeader(String paramString) { this.headers.removeHeader(paramString); }
  
  public String[] getMimeHeader(String paramString) { return this.headers.getHeader(paramString); }
  
  public void setMimeHeader(String paramString1, String paramString2) { this.headers.setHeader(paramString1, paramString2); }
  
  public void addMimeHeader(String paramString1, String paramString2) { this.headers.addHeader(paramString1, paramString2); }
  
  public Iterator getAllMimeHeaders() { return this.headers.getAllHeaders(); }
  
  public Iterator getMatchingMimeHeaders(String[] paramArrayOfString) { return this.headers.getMatchingHeaders(paramArrayOfString); }
  
  public Iterator getNonMatchingMimeHeaders(String[] paramArrayOfString) { return this.headers.getNonMatchingHeaders(paramArrayOfString); }
  
  boolean hasAllHeaders(MimeHeaders paramMimeHeaders) {
    if (paramMimeHeaders != null) {
      Iterator iterator = paramMimeHeaders.getAllHeaders();
      while (iterator.hasNext()) {
        MimeHeader mimeHeader = (MimeHeader)iterator.next();
        String[] arrayOfString = this.headers.getHeader(mimeHeader.getName());
        boolean bool = false;
        if (arrayOfString != null)
          for (byte b = 0; b < arrayOfString.length; b++) {
            if (mimeHeader.getValue().equalsIgnoreCase(arrayOfString[b])) {
              bool = true;
              break;
            } 
          }  
        if (!bool)
          return false; 
      } 
    } 
    return true;
  }
  
  MimeBodyPart getMimePart() throws SOAPException {
    try {
      if (this.mimePart != null)
        return new MimeBodyPart(this.mimePart); 
      if (this.rawContent != null) {
        copyMimeHeaders(this.headers, this.rawContent);
        return this.rawContent;
      } 
      MimeBodyPart mimeBodyPart = new MimeBodyPart();
      mimeBodyPart.setDataHandler(this.dataHandler);
      copyMimeHeaders(this.headers, mimeBodyPart);
      return mimeBodyPart;
    } catch (Exception exception) {
      log.severe("SAAJ0504.soap.cannot.externalize.attachment");
      throw new SOAPExceptionImpl("Unable to externalize attachment", exception);
    } 
  }
  
  public static void copyMimeHeaders(MimeHeaders paramMimeHeaders, MimeBodyPart paramMimeBodyPart) throws SOAPException {
    Iterator iterator = paramMimeHeaders.getAllHeaders();
    while (iterator.hasNext()) {
      try {
        MimeHeader mimeHeader = (MimeHeader)iterator.next();
        paramMimeBodyPart.setHeader(mimeHeader.getName(), mimeHeader.getValue());
      } catch (Exception exception) {
        log.severe("SAAJ0505.soap.cannot.copy.mime.hdr");
        throw new SOAPExceptionImpl("Unable to copy MIME header", exception);
      } 
    } 
  }
  
  public static void copyMimeHeaders(MimeBodyPart paramMimeBodyPart, AttachmentPartImpl paramAttachmentPartImpl) throws SOAPException {
    try {
      FinalArrayList finalArrayList = paramMimeBodyPart.getAllHeaders();
      int i = finalArrayList.size();
      for (byte b = 0; b < i; b++) {
        Header header = (Header)finalArrayList.get(b);
        if (!header.getName().equalsIgnoreCase("Content-Type"))
          paramAttachmentPartImpl.addMimeHeader(header.getName(), header.getValue()); 
      } 
    } catch (Exception exception) {
      log.severe("SAAJ0506.soap.cannot.copy.mime.hdrs.into.attachment");
      throw new SOAPExceptionImpl("Unable to copy MIME headers into attachment", exception);
    } 
  }
  
  public void setBase64Content(InputStream paramInputStream, String paramString) throws SOAPException {
    if (this.mimePart != null) {
      this.mimePart.close();
      this.mimePart = null;
    } 
    this.dataHandler = null;
    inputStream = null;
    try {
      inputStream = MimeUtility.decode(paramInputStream, "base64");
      internetHeaders = new InternetHeaders();
      internetHeaders.setHeader("Content-Type", paramString);
      ByteOutputStream byteOutputStream = new ByteOutputStream();
      byteOutputStream.write(inputStream);
      this.rawContent = new MimeBodyPart(internetHeaders, byteOutputStream.getBytes(), byteOutputStream.getCount());
      setMimeHeader("Content-Type", paramString);
    } catch (Exception exception) {
      log.log(Level.SEVERE, "SAAJ0578.soap.attachment.setbase64content.exception", exception);
      throw new SOAPExceptionImpl(exception.getLocalizedMessage());
    } finally {
      try {
        inputStream.close();
      } catch (IOException iOException) {
        throw new SOAPException(iOException);
      } 
    } 
  }
  
  public InputStream getBase64Content() throws SOAPException {
    if (this.mimePart != null) {
      inputStream = this.mimePart.read();
    } else if (this.rawContent != null) {
      try {
        inputStream = this.rawContent.getInputStream();
      } catch (Exception exception) {
        log.log(Level.SEVERE, "SAAJ0579.soap.attachment.getbase64content.exception", exception);
        throw new SOAPExceptionImpl(exception.getLocalizedMessage());
      } 
    } else if (this.dataHandler != null) {
      try {
        inputStream = this.dataHandler.getInputStream();
      } catch (IOException iOException) {
        log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
        throw new SOAPExceptionImpl("DataHandler error" + iOException);
      } 
    } else {
      log.severe("SAAJ0572.soap.no.content.for.attachment");
      throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
    } 
    char c = 'Ѐ';
    if (inputStream != null)
      try {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(c);
        OutputStream outputStream = MimeUtility.encode(byteArrayOutputStream, "base64");
        byte[] arrayOfByte = new byte[c];
        int i;
        while ((i = inputStream.read(arrayOfByte, 0, c)) != -1)
          outputStream.write(arrayOfByte, 0, i); 
        outputStream.flush();
        arrayOfByte = byteArrayOutputStream.toByteArray();
        return new ByteArrayInputStream(arrayOfByte);
      } catch (Exception exception) {
        log.log(Level.SEVERE, "SAAJ0579.soap.attachment.getbase64content.exception", exception);
        throw new SOAPExceptionImpl(exception.getLocalizedMessage());
      } finally {
        try {
          inputStream.close();
        } catch (IOException iOException) {}
      }  
    log.log(Level.SEVERE, "SAAJ0572.soap.no.content.for.attachment");
    throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
  }
  
  public void setRawContent(InputStream paramInputStream, String paramString) throws SOAPException {
    if (this.mimePart != null) {
      this.mimePart.close();
      this.mimePart = null;
    } 
    this.dataHandler = null;
    try {
      internetHeaders = new InternetHeaders();
      internetHeaders.setHeader("Content-Type", paramString);
      ByteOutputStream byteOutputStream = new ByteOutputStream();
      byteOutputStream.write(paramInputStream);
      this.rawContent = new MimeBodyPart(internetHeaders, byteOutputStream.getBytes(), byteOutputStream.getCount());
      setMimeHeader("Content-Type", paramString);
    } catch (Exception exception) {
      log.log(Level.SEVERE, "SAAJ0576.soap.attachment.setrawcontent.exception", exception);
      throw new SOAPExceptionImpl(exception.getLocalizedMessage());
    } finally {
      try {
        paramInputStream.close();
      } catch (IOException iOException) {
        throw new SOAPException(iOException);
      } 
    } 
  }
  
  public void setRawContentBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, String paramString) throws SOAPException {
    if (this.mimePart != null) {
      this.mimePart.close();
      this.mimePart = null;
    } 
    if (paramArrayOfByte == null)
      throw new SOAPExceptionImpl("Null content passed to setRawContentBytes"); 
    this.dataHandler = null;
    try {
      InternetHeaders internetHeaders = new InternetHeaders();
      internetHeaders.setHeader("Content-Type", paramString);
      this.rawContent = new MimeBodyPart(internetHeaders, paramArrayOfByte, paramInt1, paramInt2);
      setMimeHeader("Content-Type", paramString);
    } catch (Exception exception) {
      log.log(Level.SEVERE, "SAAJ0576.soap.attachment.setrawcontent.exception", exception);
      throw new SOAPExceptionImpl(exception.getLocalizedMessage());
    } 
  }
  
  public InputStream getRawContent() throws SOAPException {
    if (this.mimePart != null)
      return this.mimePart.read(); 
    if (this.rawContent != null)
      try {
        return this.rawContent.getInputStream();
      } catch (Exception exception) {
        log.log(Level.SEVERE, "SAAJ0577.soap.attachment.getrawcontent.exception", exception);
        throw new SOAPExceptionImpl(exception.getLocalizedMessage());
      }  
    if (this.dataHandler != null)
      try {
        return this.dataHandler.getInputStream();
      } catch (IOException iOException) {
        log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
        throw new SOAPExceptionImpl("DataHandler error" + iOException);
      }  
    log.severe("SAAJ0572.soap.no.content.for.attachment");
    throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
  }
  
  public byte[] getRawContentBytes() throws SOAPException {
    if (this.mimePart != null)
      try {
        InputStream inputStream = this.mimePart.read();
        return ASCIIUtility.getBytes(inputStream);
      } catch (IOException iOException) {
        log.log(Level.SEVERE, "SAAJ0577.soap.attachment.getrawcontent.exception", iOException);
        throw new SOAPExceptionImpl(iOException);
      }  
    if (this.rawContent != null)
      try {
        InputStream inputStream = this.rawContent.getInputStream();
        return ASCIIUtility.getBytes(inputStream);
      } catch (Exception exception) {
        log.log(Level.SEVERE, "SAAJ0577.soap.attachment.getrawcontent.exception", exception);
        throw new SOAPExceptionImpl(exception);
      }  
    if (this.dataHandler != null)
      try {
        InputStream inputStream = this.dataHandler.getInputStream();
        return ASCIIUtility.getBytes(inputStream);
      } catch (IOException iOException) {
        log.severe("SAAJ0574.soap.attachment.datahandler.ioexception");
        throw new SOAPExceptionImpl("DataHandler error" + iOException);
      }  
    log.severe("SAAJ0572.soap.no.content.for.attachment");
    throw new SOAPExceptionImpl("No data handler/content associated with this attachment");
  }
  
  public boolean equals(Object paramObject) { return (this == paramObject); }
  
  public int hashCode() throws SOAPException { return super.hashCode(); }
  
  public MimeHeaders getMimeHeaders() { return this.headers; }
  
  public static void initializeJavaActivationHandlers() {
    try {
      CommandMap commandMap = CommandMap.getDefaultCommandMap();
      if (commandMap instanceof MailcapCommandMap) {
        MailcapCommandMap mailcapCommandMap = (MailcapCommandMap)commandMap;
        if (!cmdMapInitialized(mailcapCommandMap)) {
          mailcapCommandMap.addMailcap("text/xml;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.XmlDataContentHandler");
          mailcapCommandMap.addMailcap("application/xml;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.XmlDataContentHandler");
          mailcapCommandMap.addMailcap("application/fastinfoset;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.FastInfosetDataContentHandler");
          mailcapCommandMap.addMailcap("image/*;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.ImageDataContentHandler");
          mailcapCommandMap.addMailcap("text/plain;;x-java-content-handler=com.sun.xml.internal.messaging.saaj.soap.StringDataContentHandler");
        } 
      } 
    } catch (Throwable throwable) {}
  }
  
  private static boolean cmdMapInitialized(MailcapCommandMap paramMailcapCommandMap) {
    CommandInfo[] arrayOfCommandInfo = paramMailcapCommandMap.getAllCommands("application/fastinfoset");
    if (arrayOfCommandInfo == null || arrayOfCommandInfo.length == 0)
      return false; 
    String str = "com.sun.xml.internal.ws.binding.FastInfosetDataContentHandler";
    for (CommandInfo commandInfo : arrayOfCommandInfo) {
      String str1 = commandInfo.getCommandClass();
      if (str.equals(str1))
        return true; 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\AttachmentPartImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */