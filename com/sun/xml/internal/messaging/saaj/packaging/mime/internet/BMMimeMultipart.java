package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.OutputUtil;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.BitSet;
import javax.activation.DataSource;

public class BMMimeMultipart extends MimeMultipart {
  private boolean begining = true;
  
  int[] bcs = new int[256];
  
  int[] gss = null;
  
  private static final int BUFFER_SIZE = 4096;
  
  private byte[] buffer = new byte[4096];
  
  private byte[] prevBuffer = new byte[4096];
  
  private BitSet lastPartFound = new BitSet(1);
  
  private InputStream in = null;
  
  private String boundary = null;
  
  int b = 0;
  
  private boolean lazyAttachments = false;
  
  byte[] buf = new byte[1024];
  
  public BMMimeMultipart() {}
  
  public BMMimeMultipart(String paramString) { super(paramString); }
  
  public BMMimeMultipart(DataSource paramDataSource, ContentType paramContentType) throws MessagingException {
    super(paramDataSource, paramContentType);
    this.boundary = paramContentType.getParameter("boundary");
  }
  
  public InputStream initStream() throws MessagingException {
    if (this.in == null) {
      try {
        this.in = this.ds.getInputStream();
        if (!(this.in instanceof java.io.ByteArrayInputStream) && !(this.in instanceof BufferedInputStream) && !(this.in instanceof SharedInputStream))
          this.in = new BufferedInputStream(this.in); 
      } catch (Exception exception) {
        throw new MessagingException("No inputstream from datasource");
      } 
      if (!this.in.markSupported())
        throw new MessagingException("InputStream does not support Marking"); 
    } 
    return this.in;
  }
  
  protected void parse() {
    if (this.parsed)
      return; 
    initStream();
    SharedInputStream sharedInputStream = null;
    if (this.in instanceof SharedInputStream)
      sharedInputStream = (SharedInputStream)this.in; 
    String str = "--" + this.boundary;
    byte[] arrayOfByte = ASCIIUtility.getBytes(str);
    try {
      parse(this.in, arrayOfByte, sharedInputStream);
    } catch (IOException iOException) {
      throw new MessagingException("IO Error", iOException);
    } catch (Exception exception) {
      throw new MessagingException("Error", exception);
    } 
    this.parsed = true;
  }
  
  public boolean lastBodyPartFound() { return this.lastPartFound.get(0); }
  
  public MimeBodyPart getNextPart(InputStream paramInputStream, byte[] paramArrayOfByte, SharedInputStream paramSharedInputStream) throws Exception {
    if (!paramInputStream.markSupported())
      throw new Exception("InputStream does not support Marking"); 
    if (this.begining) {
      compile(paramArrayOfByte);
      if (!skipPreamble(paramInputStream, paramArrayOfByte, paramSharedInputStream))
        throw new Exception("Missing Start Boundary, or boundary does not start on a new line"); 
      this.begining = false;
    } 
    if (lastBodyPartFound())
      throw new Exception("No parts found in Multipart InputStream"); 
    if (paramSharedInputStream != null) {
      long l1 = paramSharedInputStream.getPosition();
      this.b = readHeaders(paramInputStream);
      if (this.b == -1)
        throw new Exception("End of Stream encountered while reading part headers"); 
      long[] arrayOfLong = new long[1];
      arrayOfLong[0] = -1L;
      this.b = readBody(paramInputStream, paramArrayOfByte, arrayOfLong, null, paramSharedInputStream);
      if (!ignoreMissingEndBoundary && this.b == -1 && !lastBodyPartFound())
        throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers"); 
      long l2 = arrayOfLong[0];
      MimeBodyPart mimeBodyPart1 = createMimeBodyPart(paramSharedInputStream.newStream(l1, l2));
      addBodyPart(mimeBodyPart1);
      return mimeBodyPart1;
    } 
    InternetHeaders internetHeaders = createInternetHeaders(paramInputStream);
    ByteOutputStream byteOutputStream = new ByteOutputStream();
    this.b = readBody(paramInputStream, paramArrayOfByte, null, byteOutputStream, null);
    if (!ignoreMissingEndBoundary && this.b == -1 && !lastBodyPartFound())
      throw new MessagingException("Missing End Boundary for Mime Package : EOF while skipping headers"); 
    MimeBodyPart mimeBodyPart = createMimeBodyPart(internetHeaders, byteOutputStream.getBytes(), byteOutputStream.getCount());
    addBodyPart(mimeBodyPart);
    return mimeBodyPart;
  }
  
  public boolean parse(InputStream paramInputStream, byte[] paramArrayOfByte, SharedInputStream paramSharedInputStream) throws Exception {
    while (!this.lastPartFound.get(0) && this.b != -1)
      getNextPart(paramInputStream, paramArrayOfByte, paramSharedInputStream); 
    return true;
  }
  
  private int readHeaders(InputStream paramInputStream) throws Exception {
    int i;
    for (i = paramInputStream.read(); i != -1; i = paramInputStream.read()) {
      if (i == 13) {
        i = paramInputStream.read();
        if (i == 10) {
          i = paramInputStream.read();
          if (i == 13) {
            i = paramInputStream.read();
            if (i == 10)
              return i; 
          } 
        } 
        continue;
      } 
    } 
    if (i == -1)
      throw new Exception("End of inputstream while reading Mime-Part Headers"); 
    return i;
  }
  
  private int readBody(InputStream paramInputStream, byte[] paramArrayOfByte, long[] paramArrayOfLong, ByteOutputStream paramByteOutputStream, SharedInputStream paramSharedInputStream) throws Exception {
    if (!find(paramInputStream, paramArrayOfByte, paramArrayOfLong, paramByteOutputStream, paramSharedInputStream))
      throw new Exception("Missing boundary delimitier while reading Body Part"); 
    return this.b;
  }
  
  private boolean skipPreamble(InputStream paramInputStream, byte[] paramArrayOfByte, SharedInputStream paramSharedInputStream) throws Exception {
    if (!find(paramInputStream, paramArrayOfByte, paramSharedInputStream))
      return false; 
    if (this.lastPartFound.get(0))
      throw new Exception("Found closing boundary delimiter while trying to skip preamble"); 
    return true;
  }
  
  public int readNext(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt, BitSet paramBitSet, long[] paramArrayOfLong, SharedInputStream paramSharedInputStream) throws Exception {
    int i = paramInputStream.read(this.buffer, 0, paramInt);
    if (i == -1) {
      paramBitSet.flip(0);
    } else if (i < paramInt) {
      int j = 0;
      long l = 0L;
      int k;
      for (k = i; k < paramInt; k++) {
        if (paramSharedInputStream != null)
          l = paramSharedInputStream.getPosition(); 
        j = paramInputStream.read();
        if (j == -1) {
          paramBitSet.flip(0);
          if (paramSharedInputStream != null)
            paramArrayOfLong[0] = l; 
          break;
        } 
        this.buffer[k] = (byte)j;
      } 
      i = k;
    } 
    return i;
  }
  
  public boolean find(InputStream paramInputStream, byte[] paramArrayOfByte, SharedInputStream paramSharedInputStream) throws Exception {
    int i = paramArrayOfByte.length;
    int j = i - 1;
    int k = 0;
    BitSet bitSet = new BitSet(1);
    long[] arrayOfLong = new long[1];
    while (true) {
      paramInputStream.mark(i);
      k = readNext(paramInputStream, this.buffer, i, bitSet, arrayOfLong, paramSharedInputStream);
      if (bitSet.get(0))
        return false; 
      int m;
      for (m = j; m >= 0 && this.buffer[m] == paramArrayOfByte[m]; m--);
      if (m < 0) {
        if (!skipLWSPAndCRLF(paramInputStream))
          throw new Exception("Boundary does not terminate with CRLF"); 
        return true;
      } 
      int n = Math.max(m + 1 - this.bcs[this.buffer[m] & 0x7F], this.gss[m]);
      paramInputStream.reset();
      paramInputStream.skip(n);
    } 
  }
  
  public boolean find(InputStream paramInputStream, byte[] paramArrayOfByte, long[] paramArrayOfLong, ByteOutputStream paramByteOutputStream, SharedInputStream paramSharedInputStream) throws Exception {
    int i = paramArrayOfByte.length;
    int j = i - 1;
    int k = 0;
    int m = 0;
    long l = -1L;
    byte[] arrayOfByte = null;
    boolean bool = true;
    BitSet bitSet = new BitSet(1);
    while (true) {
      paramInputStream.mark(i);
      if (!bool) {
        arrayOfByte = this.prevBuffer;
        this.prevBuffer = this.buffer;
        this.buffer = arrayOfByte;
      } 
      if (paramSharedInputStream != null)
        l = paramSharedInputStream.getPosition(); 
      k = readNext(paramInputStream, this.buffer, i, bitSet, paramArrayOfLong, paramSharedInputStream);
      if (k == -1) {
        this.b = -1;
        if (m == i && paramSharedInputStream == null)
          paramByteOutputStream.write(this.prevBuffer, 0, m); 
        return true;
      } 
      if (k < i) {
        if (paramSharedInputStream == null)
          paramByteOutputStream.write(this.buffer, 0, k); 
        this.b = -1;
        return true;
      } 
      int n;
      for (n = j; n >= 0 && this.buffer[n] == paramArrayOfByte[n]; n--);
      if (n < 0) {
        if (m > 0)
          if (m <= 2) {
            if (m == 2) {
              if (this.prevBuffer[1] == 10) {
                if (this.prevBuffer[0] != 13 && this.prevBuffer[0] != 10)
                  paramByteOutputStream.write(this.prevBuffer, 0, 1); 
                if (paramSharedInputStream != null)
                  paramArrayOfLong[0] = l; 
              } else {
                throw new Exception("Boundary characters encountered in part Body without a preceeding CRLF");
              } 
            } else if (m == 1) {
              if (this.prevBuffer[0] != 10)
                throw new Exception("Boundary characters encountered in part Body without a preceeding CRLF"); 
              if (paramSharedInputStream != null)
                paramArrayOfLong[0] = l; 
            } 
          } else if (m > 2) {
            if (this.prevBuffer[m - 2] == 13 && this.prevBuffer[m - 1] == 10) {
              if (paramSharedInputStream != null) {
                paramArrayOfLong[0] = l - 2L;
              } else {
                paramByteOutputStream.write(this.prevBuffer, 0, m - 2);
              } 
            } else if (this.prevBuffer[m - 1] == 10) {
              if (paramSharedInputStream != null) {
                paramArrayOfLong[0] = l - 1L;
              } else {
                paramByteOutputStream.write(this.prevBuffer, 0, m - 1);
              } 
            } else {
              throw new Exception("Boundary characters encountered in part Body without a preceeding CRLF");
            } 
          }  
        if (!skipLWSPAndCRLF(paramInputStream));
        return true;
      } 
      if (m > 0 && paramSharedInputStream == null)
        if (this.prevBuffer[m - 1] == 13) {
          if (this.buffer[0] == 10) {
            int i1 = j - 1;
            for (i1 = j - 1; i1 > 0 && this.buffer[i1 + 1] == paramArrayOfByte[i1]; i1--);
            if (i1 == 0) {
              paramByteOutputStream.write(this.prevBuffer, 0, m - 1);
            } else {
              paramByteOutputStream.write(this.prevBuffer, 0, m);
            } 
          } else {
            paramByteOutputStream.write(this.prevBuffer, 0, m);
          } 
        } else {
          paramByteOutputStream.write(this.prevBuffer, 0, m);
        }  
      m = Math.max(n + 1 - this.bcs[this.buffer[n] & 0x7F], this.gss[n]);
      paramInputStream.reset();
      paramInputStream.skip(m);
      if (bool)
        bool = false; 
    } 
  }
  
  private boolean skipLWSPAndCRLF(InputStream paramInputStream) throws Exception {
    this.b = paramInputStream.read();
    if (this.b == 10)
      return true; 
    if (this.b == 13) {
      this.b = paramInputStream.read();
      if (this.b == 13)
        this.b = paramInputStream.read(); 
      if (this.b == 10)
        return true; 
      throw new Exception("transport padding after a Mime Boundary  should end in a CRLF, found CR only");
    } 
    if (this.b == 45) {
      this.b = paramInputStream.read();
      if (this.b != 45)
        throw new Exception("Unexpected singular '-' character after Mime Boundary"); 
      this.lastPartFound.flip(0);
      this.b = paramInputStream.read();
    } 
    while (this.b != -1 && (this.b == 32 || this.b == 9)) {
      this.b = paramInputStream.read();
      if (this.b == 10)
        return true; 
      if (this.b == 13) {
        this.b = paramInputStream.read();
        if (this.b == 13)
          this.b = paramInputStream.read(); 
        if (this.b == 10)
          return true; 
      } 
    } 
    if (this.b == -1) {
      if (!this.lastPartFound.get(0))
        throw new Exception("End of Multipart Stream before encountering  closing boundary delimiter"); 
      return true;
    } 
    return false;
  }
  
  private void compile(byte[] paramArrayOfByte) {
    int i = paramArrayOfByte.length;
    int j;
    for (j = 0; j < i; j++)
      this.bcs[paramArrayOfByte[j]] = j + true; 
    this.gss = new int[i];
    for (j = i; j > 0; j--) {
      int k = i - 1;
      while (true) {
        if (k >= j) {
          if (paramArrayOfByte[k] == paramArrayOfByte[k - j]) {
            this.gss[k - 1] = j;
            k--;
            continue;
          } 
          break;
        } 
        while (k > 0)
          this.gss[--k] = j; 
        break;
      } 
    } 
    this.gss[i - 1] = 1;
  }
  
  public void writeTo(OutputStream paramOutputStream) throws IOException, MessagingException {
    if (this.in != null)
      this.contentType.setParameter("boundary", this.boundary); 
    String str = "--" + this.contentType.getParameter("boundary");
    for (byte b1 = 0; b1 < this.parts.size(); b1++) {
      OutputUtil.writeln(str, paramOutputStream);
      ((MimeBodyPart)this.parts.get(b1)).writeTo(paramOutputStream);
      OutputUtil.writeln(paramOutputStream);
    } 
    if (this.in != null) {
      OutputUtil.writeln(str, paramOutputStream);
      if (paramOutputStream instanceof ByteOutputStream && this.lazyAttachments) {
        ((ByteOutputStream)paramOutputStream).write(this.in);
      } else {
        ByteOutputStream byteOutputStream = new ByteOutputStream(this.in.available());
        byteOutputStream.write(this.in);
        byteOutputStream.writeTo(paramOutputStream);
        this.in = byteOutputStream.newInputStream();
      } 
    } else {
      OutputUtil.writeAsAscii(str, paramOutputStream);
      OutputUtil.writeAsAscii("--", paramOutputStream);
    } 
  }
  
  public void setInputStream(InputStream paramInputStream) { this.in = paramInputStream; }
  
  public InputStream getInputStream() throws MessagingException { return this.in; }
  
  public void setBoundary(String paramString) {
    this.boundary = paramString;
    if (this.contentType != null)
      this.contentType.setParameter("boundary", paramString); 
  }
  
  public String getBoundary() { return this.boundary; }
  
  public boolean isEndOfStream() { return (this.b == -1); }
  
  public void setLazyAttachments(boolean paramBoolean) { this.lazyAttachments = paramBoolean; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\BMMimeMultipart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */