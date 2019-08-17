package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;
import com.sun.xml.internal.bind.v2.runtime.output.UTF8XmlOutput;
import com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public final class Base64Data extends Pcdata {
  private DataHandler dataHandler;
  
  private byte[] data;
  
  private int dataLen;
  
  @Nullable
  private String mimeType;
  
  public void set(byte[] paramArrayOfByte, int paramInt, @Nullable String paramString) {
    this.data = paramArrayOfByte;
    this.dataLen = paramInt;
    this.dataHandler = null;
    this.mimeType = paramString;
  }
  
  public void set(byte[] paramArrayOfByte, @Nullable String paramString) { set(paramArrayOfByte, paramArrayOfByte.length, paramString); }
  
  public void set(DataHandler paramDataHandler) {
    assert paramDataHandler != null;
    this.dataHandler = paramDataHandler;
    this.data = null;
  }
  
  public DataHandler getDataHandler() {
    if (this.dataHandler == null)
      this.dataHandler = new DataHandler(new DataSource(this) {
            public String getContentType() { return Base64Data.this.getMimeType(); }
            
            public InputStream getInputStream() throws IOException { return new ByteArrayInputStream(Base64Data.this.data, 0, Base64Data.this.dataLen); }
            
            public String getName() { return null; }
            
            public OutputStream getOutputStream() { throw new UnsupportedOperationException(); }
          }); 
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
      } catch (IOException iOException) {
        this.dataLen = 0;
      }  
    return this.data;
  }
  
  public int getDataLen() { return this.dataLen; }
  
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
        return DatatypeConverterImpl.encode(this.data[j] >> 2);
      case 1:
        if (j + 1 < this.dataLen) {
          b = this.data[j + 1];
        } else {
          b = 0;
        } 
        return DatatypeConverterImpl.encode((this.data[j] & 0x3) << 4 | b >> 4 & 0xF);
      case 2:
        if (j + 1 < this.dataLen) {
          boolean bool;
          b = this.data[j + 1];
          if (j + 2 < this.dataLen) {
            bool = this.data[j + 2];
          } else {
            bool = false;
          } 
          return DatatypeConverterImpl.encode((b & 0xF) << 2 | bool >> 6 & 0x3);
        } 
        return '=';
      case 3:
        return (j + 2 < this.dataLen) ? DatatypeConverterImpl.encode(this.data[j + 2] & 0x3F) : 61;
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
    return DatatypeConverterImpl._printBase64Binary(this.data, 0, this.dataLen);
  }
  
  public void writeTo(char[] paramArrayOfChar, int paramInt) {
    get();
    DatatypeConverterImpl._printBase64Binary(this.data, 0, this.dataLen, paramArrayOfChar, paramInt);
  }
  
  public void writeTo(UTF8XmlOutput paramUTF8XmlOutput) throws IOException {
    get();
    paramUTF8XmlOutput.text(this.data, this.dataLen);
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter) throws IOException, XMLStreamException {
    get();
    DatatypeConverterImpl._printBase64Binary(this.data, 0, this.dataLen, paramXMLStreamWriter);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\Base64Data.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */