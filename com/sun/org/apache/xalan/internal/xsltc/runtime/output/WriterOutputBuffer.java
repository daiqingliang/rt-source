package com.sun.org.apache.xalan.internal.xsltc.runtime.output;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

class WriterOutputBuffer implements OutputBuffer {
  private static final int KB = 1024;
  
  private static int BUFFER_SIZE = 4096;
  
  private Writer _writer;
  
  public WriterOutputBuffer(Writer paramWriter) { this._writer = new BufferedWriter(paramWriter, BUFFER_SIZE); }
  
  public String close() {
    try {
      this._writer.flush();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException.toString());
    } 
    return "";
  }
  
  public OutputBuffer append(String paramString) {
    try {
      this._writer.write(paramString);
    } catch (IOException iOException) {
      throw new RuntimeException(iOException.toString());
    } 
    return this;
  }
  
  public OutputBuffer append(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    try {
      this._writer.write(paramArrayOfChar, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new RuntimeException(iOException.toString());
    } 
    return this;
  }
  
  public OutputBuffer append(char paramChar) {
    try {
      this._writer.write(paramChar);
    } catch (IOException iOException) {
      throw new RuntimeException(iOException.toString());
    } 
    return this;
  }
  
  static  {
    String str = SecuritySupport.getSystemProperty("os.name");
    if (str.equalsIgnoreCase("solaris"))
      BUFFER_SIZE = 32768; 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\output\WriterOutputBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */