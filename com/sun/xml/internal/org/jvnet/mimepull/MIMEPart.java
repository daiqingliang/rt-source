package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MIMEPart {
  private static final Logger LOGGER = Logger.getLogger(MIMEPart.class.getName());
  
  private String contentType;
  
  private String contentTransferEncoding;
  
  final MIMEMessage msg;
  
  private final DataHead dataHead;
  
  MIMEPart(MIMEMessage paramMIMEMessage) {
    this.msg = paramMIMEMessage;
    this.dataHead = new DataHead(this);
  }
  
  MIMEPart(MIMEMessage paramMIMEMessage, String paramString) {
    this(paramMIMEMessage);
    this.contentId = paramString;
  }
  
  public InputStream read() {
    InputStream inputStream = null;
    try {
      inputStream = MimeUtility.decode(this.dataHead.read(), this.contentTransferEncoding);
    } catch (DecodingException decodingException) {
      if (LOGGER.isLoggable(Level.WARNING))
        LOGGER.log(Level.WARNING, null, decodingException); 
    } 
    return inputStream;
  }
  
  public void close() { this.dataHead.close(); }
  
  public InputStream readOnce() {
    InputStream inputStream = null;
    try {
      inputStream = MimeUtility.decode(this.dataHead.readOnce(), this.contentTransferEncoding);
    } catch (DecodingException decodingException) {
      if (LOGGER.isLoggable(Level.WARNING))
        LOGGER.log(Level.WARNING, null, decodingException); 
    } 
    return inputStream;
  }
  
  public void moveTo(File paramFile) { this.dataHead.moveTo(paramFile); }
  
  public String getContentId() {
    if (this.contentId == null)
      getHeaders(); 
    return this.contentId;
  }
  
  public String getContentTransferEncoding() {
    if (this.contentTransferEncoding == null)
      getHeaders(); 
    return this.contentTransferEncoding;
  }
  
  public String getContentType() {
    if (this.contentType == null)
      getHeaders(); 
    return this.contentType;
  }
  
  private void getHeaders() {
    while (this.headers == null) {
      if (!this.msg.makeProgress() && this.headers == null)
        throw new IllegalStateException("Internal Error. Didn't get Headers even after complete parsing."); 
    } 
  }
  
  public List<String> getHeader(String paramString) {
    getHeaders();
    assert this.headers != null;
    return this.headers.getHeader(paramString);
  }
  
  public List<? extends Header> getAllHeaders() {
    getHeaders();
    assert this.headers != null;
    return this.headers.getAllHeaders();
  }
  
  void setHeaders(InternetHeaders paramInternetHeaders) {
    this.headers = paramInternetHeaders;
    List list1 = getHeader("Content-Type");
    this.contentType = (list1 == null) ? "application/octet-stream" : (String)list1.get(0);
    List list2 = getHeader("Content-Transfer-Encoding");
    this.contentTransferEncoding = (list2 == null) ? "binary" : (String)list2.get(0);
  }
  
  void addBody(ByteBuffer paramByteBuffer) { this.dataHead.addBody(paramByteBuffer); }
  
  void doneParsing() {
    this.parsed = true;
    this.dataHead.doneParsing();
  }
  
  void setContentId(String paramString) { this.contentId = paramString; }
  
  void setContentTransferEncoding(String paramString) { this.contentTransferEncoding = paramString; }
  
  public String toString() { return "Part=" + this.contentId + ":" + this.contentTransferEncoding; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\MIMEPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */