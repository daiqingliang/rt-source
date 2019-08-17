package java.io;

import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public class BufferedWriter extends Writer {
  private Writer out;
  
  private char[] cb;
  
  private int nChars;
  
  private int nextChar;
  
  private static int defaultCharBufferSize = 8192;
  
  private String lineSeparator;
  
  public BufferedWriter(Writer paramWriter) { this(paramWriter, defaultCharBufferSize); }
  
  public BufferedWriter(Writer paramWriter, int paramInt) {
    super(paramWriter);
    if (paramInt <= 0)
      throw new IllegalArgumentException("Buffer size <= 0"); 
    this.out = paramWriter;
    this.cb = new char[paramInt];
    this.nChars = paramInt;
    this.nextChar = 0;
    this.lineSeparator = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
  }
  
  private void ensureOpen() throws IOException {
    if (this.out == null)
      throw new IOException("Stream closed"); 
  }
  
  void flushBuffer() throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      if (this.nextChar == 0)
        return; 
      this.out.write(this.cb, 0, this.nextChar);
      this.nextChar = 0;
    } 
  }
  
  public void write(int paramInt) throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      if (this.nextChar >= this.nChars)
        flushBuffer(); 
      this.cb[this.nextChar++] = (char)paramInt;
    } 
  }
  
  private int min(int paramInt1, int paramInt2) { return (paramInt1 < paramInt2) ? paramInt1 : paramInt2; }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      if (paramInt1 < 0 || paramInt1 > paramArrayOfChar.length || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfChar.length || paramInt1 + paramInt2 < 0)
        throw new IndexOutOfBoundsException(); 
      if (paramInt2 == 0)
        return; 
      if (paramInt2 >= this.nChars) {
        flushBuffer();
        this.out.write(paramArrayOfChar, paramInt1, paramInt2);
        return;
      } 
      int i = paramInt1;
      int j = paramInt1 + paramInt2;
      while (i < j) {
        int k = min(this.nChars - this.nextChar, j - i);
        System.arraycopy(paramArrayOfChar, i, this.cb, this.nextChar, k);
        i += k;
        this.nextChar += k;
        if (this.nextChar >= this.nChars)
          flushBuffer(); 
      } 
    } 
  }
  
  public void write(String paramString, int paramInt1, int paramInt2) throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      int i = paramInt1;
      int j = paramInt1 + paramInt2;
      while (i < j) {
        int k = min(this.nChars - this.nextChar, j - i);
        paramString.getChars(i, i + k, this.cb, this.nextChar);
        i += k;
        this.nextChar += k;
        if (this.nextChar >= this.nChars)
          flushBuffer(); 
      } 
    } 
  }
  
  public void newLine() throws IOException { write(this.lineSeparator); }
  
  public void flush() throws IOException {
    synchronized (this.lock) {
      flushBuffer();
      this.out.flush();
    } 
  }
  
  public void close() throws IOException {
    synchronized (this.lock) {
      if (this.out == null)
        return; 
      try (Writer null = this.out) {
        flushBuffer();
      } finally {
        this.out = null;
        this.cb = null;
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\BufferedWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */