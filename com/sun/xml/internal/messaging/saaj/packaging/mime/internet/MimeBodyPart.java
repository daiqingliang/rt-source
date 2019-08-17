package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.OutputUtil;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import com.sun.xml.internal.org.jvnet.mimepull.Header;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.DataSource;

public final class MimeBodyPart {
  public static final String ATTACHMENT = "attachment";
  
  public static final String INLINE = "inline";
  
  private static boolean setDefaultTextCharset = true;
  
  private DataHandler dh;
  
  private byte[] content;
  
  private int contentLength;
  
  private int start = 0;
  
  private InputStream contentStream;
  
  private final InternetHeaders headers;
  
  private MimeMultipart parent;
  
  private MIMEPart mimePart;
  
  public MimeBodyPart() { this.headers = new InternetHeaders(); }
  
  public MimeBodyPart(InputStream paramInputStream) throws MessagingException {
    if (!(paramInputStream instanceof ByteArrayInputStream) && !(paramInputStream instanceof BufferedInputStream) && !(paramInputStream instanceof SharedInputStream))
      paramInputStream = new BufferedInputStream(paramInputStream); 
    this.headers = new InternetHeaders(paramInputStream);
    if (paramInputStream instanceof SharedInputStream) {
      SharedInputStream sharedInputStream = (SharedInputStream)paramInputStream;
      this.contentStream = sharedInputStream.newStream(sharedInputStream.getPosition(), -1L);
    } else {
      try {
        ByteOutputStream byteOutputStream = new ByteOutputStream();
        byteOutputStream.write(paramInputStream);
        this.content = byteOutputStream.getBytes();
        this.contentLength = byteOutputStream.getCount();
      } catch (IOException iOException) {
        throw new MessagingException("Error reading input stream", iOException);
      } 
    } 
  }
  
  public MimeBodyPart(InternetHeaders paramInternetHeaders, byte[] paramArrayOfByte, int paramInt) {
    this.headers = paramInternetHeaders;
    this.content = paramArrayOfByte;
    this.contentLength = paramInt;
  }
  
  public MimeBodyPart(InternetHeaders paramInternetHeaders, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    this.headers = paramInternetHeaders;
    this.content = paramArrayOfByte;
    this.start = paramInt1;
    this.contentLength = paramInt2;
  }
  
  public MimeBodyPart(MIMEPart paramMIMEPart) {
    this.mimePart = paramMIMEPart;
    this.headers = new InternetHeaders();
    List list = this.mimePart.getAllHeaders();
    for (Header header : list)
      this.headers.addHeader(header.getName(), header.getValue()); 
  }
  
  public MimeMultipart getParent() { return this.parent; }
  
  public void setParent(MimeMultipart paramMimeMultipart) { this.parent = paramMimeMultipart; }
  
  public int getSize() {
    if (this.mimePart != null)
      try {
        return this.mimePart.read().available();
      } catch (IOException iOException) {
        return -1;
      }  
    if (this.content != null)
      return this.contentLength; 
    if (this.contentStream != null)
      try {
        int i = this.contentStream.available();
        if (i > 0)
          return i; 
      } catch (IOException iOException) {} 
    return -1;
  }
  
  public int getLineCount() { return -1; }
  
  public String getContentType() {
    if (this.mimePart != null)
      return this.mimePart.getContentType(); 
    String str = getHeader("Content-Type", null);
    if (str == null)
      str = "text/plain"; 
    return str;
  }
  
  public boolean isMimeType(String paramString) {
    boolean bool;
    try {
      ContentType contentType = new ContentType(getContentType());
      bool = contentType.match(paramString);
    } catch (ParseException parseException) {
      bool = getContentType().equalsIgnoreCase(paramString);
    } 
    return bool;
  }
  
  public String getDisposition() {
    String str = getHeader("Content-Disposition", null);
    if (str == null)
      return null; 
    ContentDisposition contentDisposition = new ContentDisposition(str);
    return contentDisposition.getDisposition();
  }
  
  public void setDisposition(String paramString) throws MessagingException {
    if (paramString == null) {
      removeHeader("Content-Disposition");
    } else {
      String str = getHeader("Content-Disposition", null);
      if (str != null) {
        ContentDisposition contentDisposition = new ContentDisposition(str);
        contentDisposition.setDisposition(paramString);
        paramString = contentDisposition.toString();
      } 
      setHeader("Content-Disposition", paramString);
    } 
  }
  
  public String getEncoding() {
    String str = getHeader("Content-Transfer-Encoding", null);
    if (str == null)
      return null; 
    str = str.trim();
    if (str.equalsIgnoreCase("7bit") || str.equalsIgnoreCase("8bit") || str.equalsIgnoreCase("quoted-printable") || str.equalsIgnoreCase("base64"))
      return str; 
    HeaderTokenizer headerTokenizer = new HeaderTokenizer(str, "()<>@,;:\\\"\t []/?=");
    while (true) {
      HeaderTokenizer.Token token = headerTokenizer.next();
      int i = token.getType();
      if (i == -4)
        break; 
      if (i == -1)
        return token.getValue(); 
    } 
    return str;
  }
  
  public String getContentID() { return getHeader("Content-ID", null); }
  
  public void setContentID(String paramString) throws MessagingException {
    if (paramString == null) {
      removeHeader("Content-ID");
    } else {
      setHeader("Content-ID", paramString);
    } 
  }
  
  public String getContentMD5() { return getHeader("Content-MD5", null); }
  
  public void setContentMD5(String paramString) throws MessagingException { setHeader("Content-MD5", paramString); }
  
  public String[] getContentLanguage() throws MessagingException {
    String str = getHeader("Content-Language", null);
    if (str == null)
      return null; 
    HeaderTokenizer headerTokenizer = new HeaderTokenizer(str, "()<>@,;:\\\"\t []/?=");
    FinalArrayList finalArrayList = new FinalArrayList();
    while (true) {
      HeaderTokenizer.Token token = headerTokenizer.next();
      int i = token.getType();
      if (i == -4)
        break; 
      if (i == -1)
        finalArrayList.add(token.getValue()); 
    } 
    return (finalArrayList.size() == 0) ? null : (String[])finalArrayList.toArray(new String[finalArrayList.size()]);
  }
  
  public void setContentLanguage(String[] paramArrayOfString) {
    StringBuffer stringBuffer = new StringBuffer(paramArrayOfString[0]);
    for (byte b = 1; b < paramArrayOfString.length; b++)
      stringBuffer.append(',').append(paramArrayOfString[b]); 
    setHeader("Content-Language", stringBuffer.toString());
  }
  
  public String getDescription() {
    String str = getHeader("Content-Description", null);
    if (str == null)
      return null; 
    try {
      return MimeUtility.decodeText(MimeUtility.unfold(str));
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      return str;
    } 
  }
  
  public void setDescription(String paramString) throws MessagingException { setDescription(paramString, null); }
  
  public void setDescription(String paramString1, String paramString2) throws MessagingException {
    if (paramString1 == null) {
      removeHeader("Content-Description");
      return;
    } 
    try {
      setHeader("Content-Description", MimeUtility.fold(21, MimeUtility.encodeText(paramString1, paramString2, null)));
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new MessagingException("Encoding error", unsupportedEncodingException);
    } 
  }
  
  public String getFileName() {
    String str1 = null;
    String str2 = getHeader("Content-Disposition", null);
    if (str2 != null) {
      ContentDisposition contentDisposition = new ContentDisposition(str2);
      str1 = contentDisposition.getParameter("filename");
    } 
    if (str1 == null) {
      str2 = getHeader("Content-Type", null);
      if (str2 != null)
        try {
          ContentType contentType = new ContentType(str2);
          str1 = contentType.getParameter("name");
        } catch (ParseException parseException) {} 
    } 
    return str1;
  }
  
  public void setFileName(String paramString) throws MessagingException {
    String str = getHeader("Content-Disposition", null);
    ContentDisposition contentDisposition = new ContentDisposition((str == null) ? "attachment" : str);
    contentDisposition.setParameter("filename", paramString);
    setHeader("Content-Disposition", contentDisposition.toString());
    str = getHeader("Content-Type", null);
    if (str != null)
      try {
        ContentType contentType = new ContentType(str);
        contentType.setParameter("name", paramString);
        setHeader("Content-Type", contentType.toString());
      } catch (ParseException parseException) {} 
  }
  
  public InputStream getInputStream() throws IOException { return getDataHandler().getInputStream(); }
  
  InputStream getContentStream() throws IOException {
    if (this.mimePart != null)
      return this.mimePart.read(); 
    if (this.contentStream != null)
      return ((SharedInputStream)this.contentStream).newStream(0L, -1L); 
    if (this.content != null)
      return new ByteArrayInputStream(this.content, this.start, this.contentLength); 
    throw new MessagingException("No content");
  }
  
  public InputStream getRawInputStream() throws IOException { return getContentStream(); }
  
  public DataHandler getDataHandler() {
    if (this.mimePart != null)
      return new DataHandler(new DataSource(this) {
            public InputStream getInputStream() throws IOException { return MimeBodyPart.this.mimePart.read(); }
            
            public OutputStream getOutputStream() throws IOException { throw new UnsupportedOperationException("getOutputStream cannot be supported : You have enabled LazyAttachments Option"); }
            
            public String getContentType() { return MimeBodyPart.this.mimePart.getContentType(); }
            
            public String getName() { return "MIMEPart Wrapped DataSource"; }
          }); 
    if (this.dh == null)
      this.dh = new DataHandler(new MimePartDataSource(this)); 
    return this.dh;
  }
  
  public Object getContent() throws IOException { return getDataHandler().getContent(); }
  
  public void setDataHandler(DataHandler paramDataHandler) {
    if (this.mimePart != null)
      this.mimePart = null; 
    this.dh = paramDataHandler;
    this.content = null;
    this.contentStream = null;
    removeHeader("Content-Type");
    removeHeader("Content-Transfer-Encoding");
  }
  
  public void setContent(Object paramObject, String paramString) {
    if (this.mimePart != null)
      this.mimePart = null; 
    if (paramObject instanceof MimeMultipart) {
      setContent((MimeMultipart)paramObject);
    } else {
      setDataHandler(new DataHandler(paramObject, paramString));
    } 
  }
  
  public void setText(String paramString) throws MessagingException { setText(paramString, null); }
  
  public void setText(String paramString1, String paramString2) throws MessagingException {
    if (paramString2 == null)
      if (MimeUtility.checkAscii(paramString1) != 1) {
        paramString2 = MimeUtility.getDefaultMIMECharset();
      } else {
        paramString2 = "us-ascii";
      }  
    setContent(paramString1, "text/plain; charset=" + MimeUtility.quote(paramString2, "()<>@,;:\\\"\t []/?="));
  }
  
  public void setContent(MimeMultipart paramMimeMultipart) {
    if (this.mimePart != null)
      this.mimePart = null; 
    setDataHandler(new DataHandler(paramMimeMultipart, paramMimeMultipart.getContentType().toString()));
    paramMimeMultipart.setParent(this);
  }
  
  public void writeTo(OutputStream paramOutputStream) throws IOException, MessagingException {
    List list = this.headers.getAllHeaderLines();
    int i = list.size();
    for (byte b = 0; b < i; b++)
      OutputUtil.writeln((String)list.get(b), paramOutputStream); 
    OutputUtil.writeln(paramOutputStream);
    if (this.contentStream != null) {
      ((SharedInputStream)this.contentStream).writeTo(0L, -1L, paramOutputStream);
    } else if (this.content != null) {
      paramOutputStream.write(this.content, this.start, this.contentLength);
    } else if (this.dh != null) {
      OutputStream outputStream = MimeUtility.encode(paramOutputStream, getEncoding());
      getDataHandler().writeTo(outputStream);
      if (paramOutputStream != outputStream)
        outputStream.flush(); 
    } else if (this.mimePart != null) {
      OutputStream outputStream = MimeUtility.encode(paramOutputStream, getEncoding());
      getDataHandler().writeTo(outputStream);
      if (paramOutputStream != outputStream)
        outputStream.flush(); 
    } else {
      throw new MessagingException("no content");
    } 
  }
  
  public String[] getHeader(String paramString) { return this.headers.getHeader(paramString); }
  
  public String getHeader(String paramString1, String paramString2) { return this.headers.getHeader(paramString1, paramString2); }
  
  public void setHeader(String paramString1, String paramString2) throws MessagingException { this.headers.setHeader(paramString1, paramString2); }
  
  public void addHeader(String paramString1, String paramString2) throws MessagingException { this.headers.addHeader(paramString1, paramString2); }
  
  public void removeHeader(String paramString) throws MessagingException { this.headers.removeHeader(paramString); }
  
  public FinalArrayList getAllHeaders() { return this.headers.getAllHeaders(); }
  
  public void addHeaderLine(String paramString) throws MessagingException { this.headers.addHeaderLine(paramString); }
  
  protected void updateHeaders() {
    DataHandler dataHandler = getDataHandler();
    if (dataHandler == null)
      return; 
    try {
      String str = dataHandler.getContentType();
      boolean bool1 = false;
      boolean bool2 = (getHeader("Content-Type") == null) ? 1 : 0;
      ContentType contentType = new ContentType(str);
      if (contentType.match("multipart/*")) {
        bool1 = true;
        Object object = dataHandler.getContent();
        ((MimeMultipart)object).updateHeaders();
      } else if (contentType.match("message/rfc822")) {
        bool1 = true;
      } 
      if (!bool1) {
        if (getHeader("Content-Transfer-Encoding") == null)
          setEncoding(MimeUtility.getEncoding(dataHandler)); 
        if (bool2 && setDefaultTextCharset && contentType.match("text/*") && contentType.getParameter("charset") == null) {
          String str1;
          String str2 = getEncoding();
          if (str2 != null && str2.equalsIgnoreCase("7bit")) {
            str1 = "us-ascii";
          } else {
            str1 = MimeUtility.getDefaultMIMECharset();
          } 
          contentType.setParameter("charset", str1);
          str = contentType.toString();
        } 
      } 
      if (bool2) {
        String str1 = getHeader("Content-Disposition", null);
        if (str1 != null) {
          ContentDisposition contentDisposition = new ContentDisposition(str1);
          String str2 = contentDisposition.getParameter("filename");
          if (str2 != null) {
            contentType.setParameter("name", str2);
            str = contentType.toString();
          } 
        } 
        setHeader("Content-Type", str);
      } 
    } catch (IOException iOException) {
      throw new MessagingException("IOException updating headers", iOException);
    } 
  }
  
  private void setEncoding(String paramString) throws MessagingException { setHeader("Content-Transfer-Encoding", paramString); }
  
  static  {
    try {
      String str = System.getProperty("mail.mime.setdefaulttextcharset");
      setDefaultTextCharset = (str == null || !str.equalsIgnoreCase("false"));
    } catch (SecurityException securityException) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\MimeBodyPart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */