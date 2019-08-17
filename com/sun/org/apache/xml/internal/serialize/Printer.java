package com.sun.org.apache.xml.internal.serialize;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class Printer {
  protected final OutputFormat _format;
  
  protected Writer _writer;
  
  protected StringWriter _dtdWriter;
  
  protected Writer _docWriter;
  
  protected IOException _exception;
  
  private static final int BufferSize = 4096;
  
  private final char[] _buffer = new char[4096];
  
  private int _pos = 0;
  
  public Printer(Writer paramWriter, OutputFormat paramOutputFormat) {
    this._writer = paramWriter;
    this._format = paramOutputFormat;
    this._exception = null;
    this._dtdWriter = null;
    this._docWriter = null;
    this._pos = 0;
  }
  
  public IOException getException() { return this._exception; }
  
  public void enterDTD() throws IOException {
    if (this._dtdWriter == null) {
      flushLine(false);
      this._dtdWriter = new StringWriter();
      this._docWriter = this._writer;
      this._writer = this._dtdWriter;
    } 
  }
  
  public String leaveDTD() throws IOException {
    if (this._writer == this._dtdWriter) {
      flushLine(false);
      this._writer = this._docWriter;
      return this._dtdWriter.toString();
    } 
    return null;
  }
  
  public void printText(String paramString) throws IOException {
    try {
      int i = paramString.length();
      for (byte b = 0; b < i; b++) {
        if (this._pos == 4096) {
          this._writer.write(this._buffer);
          this._pos = 0;
        } 
        this._buffer[this._pos] = paramString.charAt(b);
        this._pos++;
      } 
    } catch (IOException iOException) {
      if (this._exception == null)
        this._exception = iOException; 
      throw iOException;
    } 
  }
  
  public void printText(StringBuffer paramStringBuffer) throws IOException {
    try {
      int i = paramStringBuffer.length();
      for (byte b = 0; b < i; b++) {
        if (this._pos == 4096) {
          this._writer.write(this._buffer);
          this._pos = 0;
        } 
        this._buffer[this._pos] = paramStringBuffer.charAt(b);
        this._pos++;
      } 
    } catch (IOException iOException) {
      if (this._exception == null)
        this._exception = iOException; 
      throw iOException;
    } 
  }
  
  public void printText(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    try {
      while (paramInt2-- > 0) {
        if (this._pos == 4096) {
          this._writer.write(this._buffer);
          this._pos = 0;
        } 
        this._buffer[this._pos] = paramArrayOfChar[paramInt1];
        paramInt1++;
        this._pos++;
      } 
    } catch (IOException iOException) {
      if (this._exception == null)
        this._exception = iOException; 
      throw iOException;
    } 
  }
  
  public void printText(char paramChar) throws IOException {
    try {
      if (this._pos == 4096) {
        this._writer.write(this._buffer);
        this._pos = 0;
      } 
      this._buffer[this._pos] = paramChar;
      this._pos++;
    } catch (IOException iOException) {
      if (this._exception == null)
        this._exception = iOException; 
      throw iOException;
    } 
  }
  
  public void printSpace() throws IOException {
    try {
      if (this._pos == 4096) {
        this._writer.write(this._buffer);
        this._pos = 0;
      } 
      this._buffer[this._pos] = ' ';
      this._pos++;
    } catch (IOException iOException) {
      if (this._exception == null)
        this._exception = iOException; 
      throw iOException;
    } 
  }
  
  public void breakLine() throws IOException {
    try {
      if (this._pos == 4096) {
        this._writer.write(this._buffer);
        this._pos = 0;
      } 
      this._buffer[this._pos] = '\n';
      this._pos++;
    } catch (IOException iOException) {
      if (this._exception == null)
        this._exception = iOException; 
      throw iOException;
    } 
  }
  
  public void breakLine(boolean paramBoolean) throws IOException { breakLine(); }
  
  public void flushLine(boolean paramBoolean) throws IOException {
    try {
      this._writer.write(this._buffer, 0, this._pos);
    } catch (IOException iOException) {
      if (this._exception == null)
        this._exception = iOException; 
    } 
    this._pos = 0;
  }
  
  public void flush() throws IOException {
    try {
      this._writer.write(this._buffer, 0, this._pos);
      this._writer.flush();
    } catch (IOException iOException) {
      if (this._exception == null)
        this._exception = iOException; 
      throw iOException;
    } 
    this._pos = 0;
  }
  
  public void indent() throws IOException {}
  
  public void unindent() throws IOException {}
  
  public int getNextIndent() { return 0; }
  
  public void setNextIndent(int paramInt) {}
  
  public void setThisIndent(int paramInt) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serialize\Printer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */