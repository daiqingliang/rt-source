package java.io;

public abstract class FilterWriter extends Writer {
  protected Writer out;
  
  protected FilterWriter(Writer paramWriter) {
    super(paramWriter);
    this.out = paramWriter;
  }
  
  public void write(int paramInt) throws IOException { this.out.write(paramInt); }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException { this.out.write(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void write(String paramString, int paramInt1, int paramInt2) throws IOException { this.out.write(paramString, paramInt1, paramInt2); }
  
  public void flush() throws IOException { this.out.flush(); }
  
  public void close() throws IOException { this.out.close(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\FilterWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */