package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MultipartDataSource;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.LineInputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.OutputUtil;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import com.sun.xml.internal.messaging.saaj.util.SAAJUtil;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

public class MimeMultipart {
  protected DataSource ds = null;
  
  protected boolean parsed = true;
  
  protected FinalArrayList parts = new FinalArrayList();
  
  protected ContentType contentType;
  
  protected MimeBodyPart parent;
  
  protected static final boolean ignoreMissingEndBoundary = SAAJUtil.getSystemBoolean("saaj.mime.multipart.ignoremissingendboundary");
  
  public MimeMultipart() { this("mixed"); }
  
  public MimeMultipart(String paramString) {
    String str = UniqueValue.getUniqueBoundaryValue();
    this.contentType = new ContentType("multipart", paramString, null);
    this.contentType.setParameter("boundary", str);
  }
  
  public MimeMultipart(DataSource paramDataSource, ContentType paramContentType) throws MessagingException {
    this.parsed = false;
    this.ds = paramDataSource;
    if (paramContentType == null) {
      this.contentType = new ContentType(paramDataSource.getContentType());
    } else {
      this.contentType = paramContentType;
    } 
  }
  
  public void setSubType(String paramString) { this.contentType.setSubType(paramString); }
  
  public int getCount() throws MessagingException {
    parse();
    return (this.parts == null) ? 0 : this.parts.size();
  }
  
  public MimeBodyPart getBodyPart(int paramInt) throws MessagingException {
    parse();
    if (this.parts == null)
      throw new IndexOutOfBoundsException("No such BodyPart"); 
    return (MimeBodyPart)this.parts.get(paramInt);
  }
  
  public MimeBodyPart getBodyPart(String paramString) throws MessagingException {
    parse();
    int i = getCount();
    for (byte b = 0; b < i; b++) {
      MimeBodyPart mimeBodyPart = getBodyPart(b);
      String str1 = mimeBodyPart.getContentID();
      String str2 = (str1 != null) ? str1.replaceFirst("^<", "").replaceFirst(">$", "") : null;
      if (str1 != null && (str1.equals(paramString) || paramString.equals(str2)))
        return mimeBodyPart; 
    } 
    return null;
  }
  
  protected void updateHeaders() {
    for (byte b = 0; b < this.parts.size(); b++)
      ((MimeBodyPart)this.parts.get(b)).updateHeaders(); 
  }
  
  public void writeTo(OutputStream paramOutputStream) throws IOException, MessagingException {
    parse();
    String str = "--" + this.contentType.getParameter("boundary");
    for (byte b = 0; b < this.parts.size(); b++) {
      OutputUtil.writeln(str, paramOutputStream);
      getBodyPart(b).writeTo(paramOutputStream);
      OutputUtil.writeln(paramOutputStream);
    } 
    OutputUtil.writeAsAscii(str, paramOutputStream);
    OutputUtil.writeAsAscii("--", paramOutputStream);
    paramOutputStream.flush();
  }
  
  protected void parse() {
    InputStream inputStream;
    if (this.parsed)
      return; 
    SharedInputStream sharedInputStream = null;
    long l1 = 0L;
    long l2 = 0L;
    boolean bool = false;
    try {
      inputStream = this.ds.getInputStream();
      if (!(inputStream instanceof java.io.ByteArrayInputStream) && !(inputStream instanceof BufferedInputStream) && !(inputStream instanceof SharedInputStream))
        inputStream = new BufferedInputStream(inputStream); 
    } catch (Exception exception) {
      throw new MessagingException("No inputstream from datasource");
    } 
    if (inputStream instanceof SharedInputStream)
      sharedInputStream = (SharedInputStream)inputStream; 
    String str = "--" + this.contentType.getParameter("boundary");
    byte[] arrayOfByte = ASCIIUtility.getBytes(str);
    int i = arrayOfByte.length;
    try {
      LineInputStream lineInputStream = new LineInputStream(inputStream);
      String str1;
      while ((str1 = lineInputStream.readLine()) != null) {
        int j;
        for (j = str1.length() - 1; j >= 0; j--) {
          char c = str1.charAt(j);
          if (c != ' ' && c != '\t')
            break; 
        } 
        str1 = str1.substring(0, j + 1);
        if (str1.equals(str))
          break; 
      } 
      if (str1 == null)
        throw new MessagingException("Missing start boundary"); 
      boolean bool1 = false;
      while (!bool1) {
        MimeBodyPart mimeBodyPart;
        InternetHeaders internetHeaders = null;
        if (sharedInputStream != null) {
          l1 = sharedInputStream.getPosition();
          while ((str1 = lineInputStream.readLine()) != null && str1.length() > 0);
          if (str1 == null) {
            if (!ignoreMissingEndBoundary)
              throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers"); 
            break;
          } 
        } else {
          internetHeaders = createInternetHeaders(inputStream);
        } 
        if (!inputStream.markSupported())
          throw new MessagingException("Stream doesn't support mark"); 
        ByteOutputStream byteOutputStream = null;
        if (sharedInputStream == null)
          byteOutputStream = new ByteOutputStream(); 
        boolean bool2 = true;
        int j = -1;
        int k = -1;
        while (true) {
          if (bool2) {
            inputStream.mark(i + 4 + 1000);
            byte b;
            for (b = 0; b < i && inputStream.read() == arrayOfByte[b]; b++);
            if (b == i) {
              int n = inputStream.read();
              if (n == 45 && inputStream.read() == 45) {
                bool1 = true;
                bool = true;
                break;
              } 
              while (n == 32 || n == 9)
                n = inputStream.read(); 
              if (n == 10)
                break; 
              if (n == 13) {
                inputStream.mark(1);
                if (inputStream.read() != 10)
                  inputStream.reset(); 
                break;
              } 
            } 
            inputStream.reset();
            if (byteOutputStream != null && j != -1) {
              byteOutputStream.write(j);
              if (k != -1)
                byteOutputStream.write(k); 
              j = k = -1;
            } 
          } 
          int m;
          if ((m = inputStream.read()) < 0) {
            bool1 = true;
            break;
          } 
          if (m == 13 || m == 10) {
            bool2 = true;
            if (sharedInputStream != null)
              l2 = sharedInputStream.getPosition() - 1L; 
            j = m;
            if (m == 13) {
              inputStream.mark(1);
              if ((m = inputStream.read()) == 10) {
                k = m;
                continue;
              } 
              inputStream.reset();
            } 
            continue;
          } 
          bool2 = false;
          if (byteOutputStream != null)
            byteOutputStream.write(m); 
        } 
        if (sharedInputStream != null) {
          mimeBodyPart = createMimeBodyPart(sharedInputStream.newStream(l1, l2));
        } else {
          mimeBodyPart = createMimeBodyPart(internetHeaders, byteOutputStream.getBytes(), byteOutputStream.getCount());
        } 
        addBodyPart(mimeBodyPart);
      } 
    } catch (IOException iOException) {
      throw new MessagingException("IO Error", iOException);
    } 
    if (!ignoreMissingEndBoundary && !bool && sharedInputStream == null)
      throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers"); 
    this.parsed = true;
  }
  
  protected InternetHeaders createInternetHeaders(InputStream paramInputStream) throws MessagingException { return new InternetHeaders(paramInputStream); }
  
  protected MimeBodyPart createMimeBodyPart(InternetHeaders paramInternetHeaders, byte[] paramArrayOfByte, int paramInt) { return new MimeBodyPart(paramInternetHeaders, paramArrayOfByte, paramInt); }
  
  protected MimeBodyPart createMimeBodyPart(InputStream paramInputStream) throws MessagingException { return new MimeBodyPart(paramInputStream); }
  
  protected void setMultipartDataSource(MultipartDataSource paramMultipartDataSource) throws MessagingException {
    this.contentType = new ContentType(paramMultipartDataSource.getContentType());
    int i = paramMultipartDataSource.getCount();
    for (byte b = 0; b < i; b++)
      addBodyPart(paramMultipartDataSource.getBodyPart(b)); 
  }
  
  public ContentType getContentType() { return this.contentType; }
  
  public boolean removeBodyPart(MimeBodyPart paramMimeBodyPart) throws MessagingException {
    if (this.parts == null)
      throw new MessagingException("No such body part"); 
    boolean bool = this.parts.remove(paramMimeBodyPart);
    paramMimeBodyPart.setParent(null);
    return bool;
  }
  
  public void removeBodyPart(int paramInt) {
    if (this.parts == null)
      throw new IndexOutOfBoundsException("No such BodyPart"); 
    MimeBodyPart mimeBodyPart = (MimeBodyPart)this.parts.get(paramInt);
    this.parts.remove(paramInt);
    mimeBodyPart.setParent(null);
  }
  
  public void addBodyPart(MimeBodyPart paramMimeBodyPart) {
    if (this.parts == null)
      this.parts = new FinalArrayList(); 
    this.parts.add(paramMimeBodyPart);
    paramMimeBodyPart.setParent(this);
  }
  
  public void addBodyPart(MimeBodyPart paramMimeBodyPart, int paramInt) {
    if (this.parts == null)
      this.parts = new FinalArrayList(); 
    this.parts.add(paramInt, paramMimeBodyPart);
    paramMimeBodyPart.setParent(this);
  }
  
  MimeBodyPart getParent() { return this.parent; }
  
  void setParent(MimeBodyPart paramMimeBodyPart) { this.parent = paramMimeBodyPart; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\MimeMultipart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */