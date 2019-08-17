package com.sun.xml.internal.org.jvnet.staxex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class Base64Data implements CharSequence, Cloneable {
  private DataHandler dataHandler;
  
  private byte[] data;
  
  private int dataLen;
  
  private boolean dataCloneByRef;
  
  private String mimeType;
  
  private static final Logger logger = Logger.getLogger(Base64Data.class.getName());
  
  private static final int CHUNK_SIZE;
  
  public Base64Data() {}
  
  public Base64Data(Base64Data paramBase64Data) {
    paramBase64Data.get();
    if (paramBase64Data.dataCloneByRef) {
      this.data = paramBase64Data.data;
    } else {
      this.data = new byte[paramBase64Data.dataLen];
      System.arraycopy(paramBase64Data.data, 0, this.data, 0, paramBase64Data.dataLen);
    } 
    this.dataCloneByRef = true;
    this.dataLen = paramBase64Data.dataLen;
    this.dataHandler = null;
    this.mimeType = paramBase64Data.mimeType;
  }
  
  public void set(byte[] paramArrayOfByte, int paramInt, String paramString, boolean paramBoolean) {
    this.data = paramArrayOfByte;
    this.dataLen = paramInt;
    this.dataCloneByRef = paramBoolean;
    this.dataHandler = null;
    this.mimeType = paramString;
  }
  
  public void set(byte[] paramArrayOfByte, int paramInt, String paramString) { set(paramArrayOfByte, paramInt, paramString, false); }
  
  public void set(byte[] paramArrayOfByte, String paramString) { set(paramArrayOfByte, paramArrayOfByte.length, paramString, false); }
  
  public void set(DataHandler paramDataHandler) {
    assert paramDataHandler != null;
    this.dataHandler = paramDataHandler;
    this.data = null;
  }
  
  public DataHandler getDataHandler() {
    if (this.dataHandler == null) {
      this.dataHandler = new Base64StreamingDataHandler(new Base64DataSource(this, null));
    } else if (!(this.dataHandler instanceof StreamingDataHandler)) {
      this.dataHandler = new FilterDataHandler(this.dataHandler);
    } 
    return this.dataHandler;
  }
  
  public byte[] getExact() {
    get();
    if (this.dataLen != this.data.length) {
      byte[] arrayOfByte = new byte[this.dataLen];
      System.arraycopy(this.data, 0, arrayOfByte, 0, this.dataLen);
      this.data = arrayOfByte;
    } 
    return this.data;
  }
  
  public InputStream getInputStream() throws IOException { return (this.dataHandler != null) ? this.dataHandler.getInputStream() : new ByteArrayInputStream(this.data, 0, this.dataLen); }
  
  public boolean hasData() { return (this.data != null); }
  
  public byte[] get() {
    if (this.data == null)
      try {
        ByteArrayOutputStreamEx byteArrayOutputStreamEx = new ByteArrayOutputStreamEx(1024);
        InputStream inputStream = this.dataHandler.getDataSource().getInputStream();
        byteArrayOutputStreamEx.readFrom(inputStream);
        inputStream.close();
        this.data = byteArrayOutputStreamEx.getBuffer();
        this.dataLen = byteArrayOutputStreamEx.size();
        this.dataCloneByRef = true;
      } catch (IOException iOException) {
        this.dataLen = 0;
      }  
    return this.data;
  }
  
  public int getDataLen() {
    get();
    return this.dataLen;
  }
  
  public String getMimeType() { return (this.mimeType == null) ? "application/octet-stream" : this.mimeType; }
  
  public int length() {
    get();
    return (this.dataLen + 2) / 3 * 4;
  }
  
  public char charAt(int paramInt) {
    byte b;
    int i = paramInt % 4;
    int j = paramInt / 4 * 3;
    switch (i) {
      case 0:
        return Base64Encoder.encode(this.data[j] >> 2);
      case 1:
        if (j + 1 < this.dataLen) {
          b = this.data[j + 1];
        } else {
          b = 0;
        } 
        return Base64Encoder.encode((this.data[j] & 0x3) << 4 | b >> 4 & 0xF);
      case 2:
        if (j + 1 < this.dataLen) {
          boolean bool;
          b = this.data[j + 1];
          if (j + 2 < this.dataLen) {
            bool = this.data[j + 2];
          } else {
            bool = false;
          } 
          return Base64Encoder.encode((b & 0xF) << 2 | bool >> 6 & 0x3);
        } 
        return '=';
      case 3:
        return (j + 2 < this.dataLen) ? Base64Encoder.encode(this.data[j + 2] & 0x3F) : 61;
    } 
    throw new IllegalStateException();
  }
  
  public CharSequence subSequence(int paramInt1, int paramInt2) {
    StringBuilder stringBuilder = new StringBuilder();
    get();
    for (int i = paramInt1; i < paramInt2; i++)
      stringBuilder.append(charAt(i)); 
    return stringBuilder;
  }
  
  public String toString() {
    get();
    return Base64Encoder.print(this.data, 0, this.dataLen);
  }
  
  public void writeTo(char[] paramArrayOfChar, int paramInt) {
    get();
    Base64Encoder.print(this.data, 0, this.dataLen, paramArrayOfChar, paramInt);
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter) throws IOException, XMLStreamException {
    if (this.data == null) {
      try {
        InputStream inputStream = this.dataHandler.getDataSource().getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Base64EncoderStream base64EncoderStream = new Base64EncoderStream(paramXMLStreamWriter, byteArrayOutputStream);
        byte[] arrayOfByte = new byte[CHUNK_SIZE];
        int i;
        while ((i = inputStream.read(arrayOfByte)) != -1)
          base64EncoderStream.write(arrayOfByte, 0, i); 
        byteArrayOutputStream.close();
        base64EncoderStream.close();
      } catch (IOException iOException) {
        this.dataLen = 0;
        throw iOException;
      } 
    } else {
      String str = Base64Encoder.print(this.data, 0, this.dataLen);
      paramXMLStreamWriter.writeCharacters(str);
    } 
  }
  
  public Base64Data clone() {
    try {
      Base64Data base64Data = (Base64Data)super.clone();
      base64Data.get();
      if (base64Data.dataCloneByRef) {
        this.data = base64Data.data;
      } else {
        this.data = new byte[base64Data.dataLen];
        System.arraycopy(base64Data.data, 0, this.data, 0, base64Data.dataLen);
      } 
      this.dataCloneByRef = true;
      this.dataLen = base64Data.dataLen;
      this.dataHandler = null;
      this.mimeType = base64Data.mimeType;
      return base64Data;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      Logger.getLogger(Base64Data.class.getName()).log(Level.SEVERE, null, cloneNotSupportedException);
      return null;
    } 
  }
  
  static String getProperty(final String propName) { return (System.getSecurityManager() == null) ? System.getProperty(paramString) : (String)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() throws CloneNotSupportedException { return System.getProperty(propName); }
        }); }
  
  static  {
    int i = 1024;
    try {
      String str = getProperty("com.sun.xml.internal.org.jvnet.staxex.Base64DataStreamWriteBufferSize");
      if (str != null)
        i = Integer.parseInt(str); 
    } catch (Exception exception) {
      logger.log(Level.INFO, "Error reading com.sun.xml.internal.org.jvnet.staxex.Base64DataStreamWriteBufferSize property", exception);
    } 
    CHUNK_SIZE = i;
  }
  
  private final class Base64DataSource implements DataSource {
    private Base64DataSource() {}
    
    public String getContentType() { return Base64Data.this.getMimeType(); }
    
    public InputStream getInputStream() throws IOException { return new ByteArrayInputStream(Base64Data.this.data, 0, Base64Data.this.dataLen); }
    
    public String getName() { return null; }
    
    public OutputStream getOutputStream() { throw new UnsupportedOperationException(); }
  }
  
  private final class Base64StreamingDataHandler extends StreamingDataHandler {
    Base64StreamingDataHandler(DataSource param1DataSource) { super(param1DataSource); }
    
    public InputStream readOnce() throws IOException { return getDataSource().getInputStream(); }
    
    public void moveTo(File param1File) throws IOException {
      fileOutputStream = new FileOutputStream(param1File);
      try {
        fileOutputStream.write(Base64Data.this.data, 0, Base64Data.this.dataLen);
      } finally {
        fileOutputStream.close();
      } 
    }
    
    public void close() {}
  }
  
  private static final class FilterDataHandler extends StreamingDataHandler {
    FilterDataHandler(DataHandler param1DataHandler) { super(param1DataHandler.getDataSource()); }
    
    public InputStream readOnce() throws IOException { return getDataSource().getInputStream(); }
    
    public void moveTo(File param1File) throws IOException {
      byte[] arrayOfByte = new byte[8192];
      inputStream = null;
      fileOutputStream = null;
      try {
        inputStream = getDataSource().getInputStream();
        fileOutputStream = new FileOutputStream(param1File);
        while (true) {
          int i = inputStream.read(arrayOfByte);
          if (i == -1)
            break; 
          fileOutputStream.write(arrayOfByte, 0, i);
        } 
      } finally {
        if (inputStream != null)
          try {
            inputStream.close();
          } catch (IOException iOException) {} 
        if (fileOutputStream != null)
          try {
            fileOutputStream.close();
          } catch (IOException iOException) {} 
      } 
    }
    
    public void close() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\staxex\Base64Data.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */