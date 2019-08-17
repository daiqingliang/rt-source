package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

class WriterToASCI extends Writer implements WriterChain {
  private final OutputStream m_os;
  
  public WriterToASCI(OutputStream paramOutputStream) { this.m_os = paramOutputStream; }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    int i = paramInt2 + paramInt1;
    for (int j = paramInt1; j < i; j++)
      this.m_os.write(paramArrayOfChar[j]); 
  }
  
  public void write(int paramInt) throws IOException { this.m_os.write(paramInt); }
  
  public void write(String paramString) throws IOException {
    int i = paramString.length();
    for (byte b = 0; b < i; b++)
      this.m_os.write(paramString.charAt(b)); 
  }
  
  public void flush() throws IOException { this.m_os.flush(); }
  
  public void close() throws IOException { this.m_os.close(); }
  
  public OutputStream getOutputStream() { return this.m_os; }
  
  public Writer getWriter() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\WriterToASCI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */