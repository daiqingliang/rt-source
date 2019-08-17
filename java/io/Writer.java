package java.io;

public abstract class Writer implements Appendable, Closeable, Flushable {
  private char[] writeBuffer;
  
  private static final int WRITE_BUFFER_SIZE = 1024;
  
  protected Object lock;
  
  protected Writer() { this.lock = this; }
  
  protected Writer(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
    this.lock = paramObject;
  }
  
  public void write(int paramInt) throws IOException {
    synchronized (this.lock) {
      if (this.writeBuffer == null)
        this.writeBuffer = new char[1024]; 
      this.writeBuffer[0] = (char)paramInt;
      write(this.writeBuffer, 0, 1);
    } 
  }
  
  public void write(char[] paramArrayOfChar) throws IOException { write(paramArrayOfChar, 0, paramArrayOfChar.length); }
  
  public abstract void write(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException;
  
  public void write(String paramString) throws IOException { write(paramString, 0, paramString.length()); }
  
  public void write(String paramString, int paramInt1, int paramInt2) throws IOException {
    synchronized (this.lock) {
      char[] arrayOfChar;
      if (paramInt2 <= 1024) {
        if (this.writeBuffer == null)
          this.writeBuffer = new char[1024]; 
        arrayOfChar = this.writeBuffer;
      } else {
        arrayOfChar = new char[paramInt2];
      } 
      paramString.getChars(paramInt1, paramInt1 + paramInt2, arrayOfChar, 0);
      write(arrayOfChar, 0, paramInt2);
    } 
  }
  
  public Writer append(CharSequence paramCharSequence) throws IOException {
    if (paramCharSequence == null) {
      write("null");
    } else {
      write(paramCharSequence.toString());
    } 
    return this;
  }
  
  public Writer append(CharSequence paramCharSequence, int paramInt1, int paramInt2) throws IOException {
    String str = (paramCharSequence == null) ? "null" : paramCharSequence;
    write(str.subSequence(paramInt1, paramInt2).toString());
    return this;
  }
  
  public Writer append(char paramChar) throws IOException {
    write(paramChar);
    return this;
  }
  
  public abstract void flush();
  
  public abstract void close();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\Writer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */