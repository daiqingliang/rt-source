package java.io;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Formatter;
import java.util.Locale;

public class PrintStream extends FilterOutputStream implements Appendable, Closeable {
  private final boolean autoFlush;
  
  private boolean trouble = false;
  
  private Formatter formatter;
  
  private BufferedWriter textOut;
  
  private OutputStreamWriter charOut;
  
  private boolean closing = false;
  
  private static <T> T requireNonNull(T paramT, String paramString) {
    if (paramT == null)
      throw new NullPointerException(paramString); 
    return paramT;
  }
  
  private static Charset toCharset(String paramString) throws UnsupportedEncodingException {
    requireNonNull(paramString, "charsetName");
    try {
      return Charset.forName(paramString);
    } catch (IllegalCharsetNameException|java.nio.charset.UnsupportedCharsetException illegalCharsetNameException) {
      throw new UnsupportedEncodingException(paramString);
    } 
  }
  
  private PrintStream(boolean paramBoolean, OutputStream paramOutputStream) {
    super(paramOutputStream);
    this.autoFlush = paramBoolean;
    this.charOut = new OutputStreamWriter(this);
    this.textOut = new BufferedWriter(this.charOut);
  }
  
  private PrintStream(boolean paramBoolean, OutputStream paramOutputStream, Charset paramCharset) {
    super(paramOutputStream);
    this.autoFlush = paramBoolean;
    this.charOut = new OutputStreamWriter(this, paramCharset);
    this.textOut = new BufferedWriter(this.charOut);
  }
  
  private PrintStream(boolean paramBoolean, Charset paramCharset, OutputStream paramOutputStream) throws UnsupportedEncodingException { this(paramBoolean, paramOutputStream, paramCharset); }
  
  public PrintStream(OutputStream paramOutputStream) { this(paramOutputStream, false); }
  
  public PrintStream(OutputStream paramOutputStream, boolean paramBoolean) { this(paramBoolean, (OutputStream)requireNonNull(paramOutputStream, "Null output stream")); }
  
  public PrintStream(OutputStream paramOutputStream, boolean paramBoolean, String paramString) throws UnsupportedEncodingException { this(paramBoolean, (OutputStream)requireNonNull(paramOutputStream, "Null output stream"), toCharset(paramString)); }
  
  public PrintStream(String paramString) throws FileNotFoundException { this(false, new FileOutputStream(paramString)); }
  
  public PrintStream(String paramString1, String paramString2) throws FileNotFoundException, UnsupportedEncodingException { this(false, toCharset(paramString2), new FileOutputStream(paramString1)); }
  
  public PrintStream(File paramFile) throws FileNotFoundException { this(false, new FileOutputStream(paramFile)); }
  
  public PrintStream(File paramFile, String paramString) throws FileNotFoundException, UnsupportedEncodingException { this(false, toCharset(paramString), new FileOutputStream(paramFile)); }
  
  private void ensureOpen() throws IOException {
    if (this.out == null)
      throw new IOException("Stream closed"); 
  }
  
  public void flush() throws IOException {
    synchronized (this) {
      try {
        ensureOpen();
        this.out.flush();
      } catch (IOException iOException) {
        this.trouble = true;
      } 
    } 
  }
  
  public void close() throws IOException {
    synchronized (this) {
      if (!this.closing) {
        this.closing = true;
        try {
          this.textOut.close();
          this.out.close();
        } catch (IOException iOException) {
          this.trouble = true;
        } 
        this.textOut = null;
        this.charOut = null;
        this.out = null;
      } 
    } 
  }
  
  public boolean checkError() {
    if (this.out != null)
      flush(); 
    if (this.out instanceof PrintStream) {
      PrintStream printStream = (PrintStream)this.out;
      return printStream.checkError();
    } 
    return this.trouble;
  }
  
  protected void setError() throws IOException { this.trouble = true; }
  
  protected void clearError() throws IOException { this.trouble = false; }
  
  public void write(int paramInt) {
    try {
      synchronized (this) {
        ensureOpen();
        this.out.write(paramInt);
        if (paramInt == 10 && this.autoFlush)
          this.out.flush(); 
      } 
    } catch (InterruptedIOException interruptedIOException) {
      Thread.currentThread().interrupt();
    } catch (IOException iOException) {
      this.trouble = true;
    } 
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    try {
      synchronized (this) {
        ensureOpen();
        this.out.write(paramArrayOfByte, paramInt1, paramInt2);
        if (this.autoFlush)
          this.out.flush(); 
      } 
    } catch (InterruptedIOException interruptedIOException) {
      Thread.currentThread().interrupt();
    } catch (IOException iOException) {
      this.trouble = true;
    } 
  }
  
  private void write(char[] paramArrayOfChar) {
    try {
      synchronized (this) {
        ensureOpen();
        this.textOut.write(paramArrayOfChar);
        this.textOut.flushBuffer();
        this.charOut.flushBuffer();
        if (this.autoFlush)
          for (byte b = 0; b < paramArrayOfChar.length; b++) {
            if (paramArrayOfChar[b] == '\n')
              this.out.flush(); 
          }  
      } 
    } catch (InterruptedIOException interruptedIOException) {
      Thread.currentThread().interrupt();
    } catch (IOException iOException) {
      this.trouble = true;
    } 
  }
  
  private void write(String paramString) throws FileNotFoundException {
    try {
      synchronized (this) {
        ensureOpen();
        this.textOut.write(paramString);
        this.textOut.flushBuffer();
        this.charOut.flushBuffer();
        if (this.autoFlush && paramString.indexOf('\n') >= 0)
          this.out.flush(); 
      } 
    } catch (InterruptedIOException interruptedIOException) {
      Thread.currentThread().interrupt();
    } catch (IOException iOException) {
      this.trouble = true;
    } 
  }
  
  private void newLine() throws IOException {
    try {
      synchronized (this) {
        ensureOpen();
        this.textOut.newLine();
        this.textOut.flushBuffer();
        this.charOut.flushBuffer();
        if (this.autoFlush)
          this.out.flush(); 
      } 
    } catch (InterruptedIOException interruptedIOException) {
      Thread.currentThread().interrupt();
    } catch (IOException iOException) {
      this.trouble = true;
    } 
  }
  
  public void print(boolean paramBoolean) { write(paramBoolean ? "true" : "false"); }
  
  public void print(char paramChar) { write(String.valueOf(paramChar)); }
  
  public void print(int paramInt) { write(String.valueOf(paramInt)); }
  
  public void print(long paramLong) { write(String.valueOf(paramLong)); }
  
  public void print(float paramFloat) { write(String.valueOf(paramFloat)); }
  
  public void print(double paramDouble) { write(String.valueOf(paramDouble)); }
  
  public void print(char[] paramArrayOfChar) { write(paramArrayOfChar); }
  
  public void print(String paramString) throws FileNotFoundException {
    if (paramString == null)
      paramString = "null"; 
    write(paramString);
  }
  
  public void print(Object paramObject) { write(String.valueOf(paramObject)); }
  
  public void println() throws IOException { newLine(); }
  
  public void println(boolean paramBoolean) {
    synchronized (this) {
      print(paramBoolean);
      newLine();
    } 
  }
  
  public void println(char paramChar) {
    synchronized (this) {
      print(paramChar);
      newLine();
    } 
  }
  
  public void println(int paramInt) {
    synchronized (this) {
      print(paramInt);
      newLine();
    } 
  }
  
  public void println(long paramLong) {
    synchronized (this) {
      print(paramLong);
      newLine();
    } 
  }
  
  public void println(float paramFloat) {
    synchronized (this) {
      print(paramFloat);
      newLine();
    } 
  }
  
  public void println(double paramDouble) {
    synchronized (this) {
      print(paramDouble);
      newLine();
    } 
  }
  
  public void println(char[] paramArrayOfChar) {
    synchronized (this) {
      print(paramArrayOfChar);
      newLine();
    } 
  }
  
  public void println(String paramString) throws FileNotFoundException {
    synchronized (this) {
      print(paramString);
      newLine();
    } 
  }
  
  public void println(Object paramObject) {
    String str = String.valueOf(paramObject);
    synchronized (this) {
      print(str);
      newLine();
    } 
  }
  
  public PrintStream printf(String paramString, Object... paramVarArgs) { return format(paramString, paramVarArgs); }
  
  public PrintStream printf(Locale paramLocale, String paramString, Object... paramVarArgs) { return format(paramLocale, paramString, paramVarArgs); }
  
  public PrintStream format(String paramString, Object... paramVarArgs) {
    try {
      synchronized (this) {
        ensureOpen();
        if (this.formatter == null || this.formatter.locale() != Locale.getDefault())
          this.formatter = new Formatter(this); 
        this.formatter.format(Locale.getDefault(), paramString, paramVarArgs);
      } 
    } catch (InterruptedIOException interruptedIOException) {
      Thread.currentThread().interrupt();
    } catch (IOException iOException) {
      this.trouble = true;
    } 
    return this;
  }
  
  public PrintStream format(Locale paramLocale, String paramString, Object... paramVarArgs) {
    try {
      synchronized (this) {
        ensureOpen();
        if (this.formatter == null || this.formatter.locale() != paramLocale)
          this.formatter = new Formatter(this, paramLocale); 
        this.formatter.format(paramLocale, paramString, paramVarArgs);
      } 
    } catch (InterruptedIOException interruptedIOException) {
      Thread.currentThread().interrupt();
    } catch (IOException iOException) {
      this.trouble = true;
    } 
    return this;
  }
  
  public PrintStream append(CharSequence paramCharSequence) {
    if (paramCharSequence == null) {
      print("null");
    } else {
      print(paramCharSequence.toString());
    } 
    return this;
  }
  
  public PrintStream append(CharSequence paramCharSequence, int paramInt1, int paramInt2) {
    String str = (paramCharSequence == null) ? "null" : paramCharSequence;
    write(str.subSequence(paramInt1, paramInt2).toString());
    return this;
  }
  
  public PrintStream append(char paramChar) {
    print(paramChar);
    return this;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\PrintStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */