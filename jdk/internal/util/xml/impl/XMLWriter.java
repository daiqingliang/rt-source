package jdk.internal.util.xml.impl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import jdk.internal.util.xml.XMLStreamException;

public class XMLWriter {
  private Writer _writer;
  
  private CharsetEncoder _encoder = null;
  
  public XMLWriter(OutputStream paramOutputStream, String paramString, Charset paramCharset) throws XMLStreamException {
    this._encoder = paramCharset.newEncoder();
    try {
      this._writer = getWriter(paramOutputStream, paramString, paramCharset);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new XMLStreamException(unsupportedEncodingException);
    } 
  }
  
  public boolean canEncode(char paramChar) { return (this._encoder == null) ? false : this._encoder.canEncode(paramChar); }
  
  public void write(String paramString) throws XMLStreamException {
    try {
      this._writer.write(paramString.toCharArray());
    } catch (IOException iOException) {
      throw new XMLStreamException("I/O error", iOException);
    } 
  }
  
  public void write(String paramString, int paramInt1, int paramInt2) throws XMLStreamException {
    try {
      this._writer.write(paramString, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new XMLStreamException("I/O error", iOException);
    } 
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws XMLStreamException {
    try {
      this._writer.write(paramArrayOfChar, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new XMLStreamException("I/O error", iOException);
    } 
  }
  
  void write(int paramInt) throws XMLStreamException {
    try {
      this._writer.write(paramInt);
    } catch (IOException iOException) {
      throw new XMLStreamException("I/O error", iOException);
    } 
  }
  
  void flush() throws XMLStreamException {
    try {
      this._writer.flush();
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  void close() throws XMLStreamException {
    try {
      this._writer.close();
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
  
  private void nl() throws XMLStreamException {
    String str = System.getProperty("line.separator");
    try {
      this._writer.write(str);
    } catch (IOException iOException) {
      throw new XMLStreamException("I/O error", iOException);
    } 
  }
  
  private Writer getWriter(OutputStream paramOutputStream, String paramString, Charset paramCharset) throws XMLStreamException, UnsupportedEncodingException { return (paramCharset != null) ? new OutputStreamWriter(new BufferedOutputStream(paramOutputStream), paramCharset) : new OutputStreamWriter(new BufferedOutputStream(paramOutputStream), paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\interna\\util\xml\impl\XMLWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */