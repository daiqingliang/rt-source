package java.io;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Formatter;
import sun.misc.JavaIOAccess;
import sun.misc.SharedSecrets;
import sun.nio.cs.StreamDecoder;
import sun.nio.cs.StreamEncoder;

public final class Console implements Flushable {
  private Object readLock = new Object();
  
  private Object writeLock = new Object();
  
  private Reader reader;
  
  private Writer out;
  
  private PrintWriter pw;
  
  private Formatter formatter;
  
  private Charset cs;
  
  private char[] rcb;
  
  private static boolean echoOff;
  
  private static Console cons;
  
  public PrintWriter writer() { return this.pw; }
  
  public Reader reader() { return this.reader; }
  
  public Console format(String paramString, Object... paramVarArgs) {
    this.formatter.format(paramString, paramVarArgs).flush();
    return this;
  }
  
  public Console printf(String paramString, Object... paramVarArgs) { return format(paramString, paramVarArgs); }
  
  public String readLine(String paramString, Object... paramVarArgs) {
    String str = null;
    synchronized (this.writeLock) {
      synchronized (this.readLock) {
        if (paramString.length() != 0)
          this.pw.format(paramString, paramVarArgs); 
        try {
          char[] arrayOfChar = readline(false);
          if (arrayOfChar != null)
            str = new String(arrayOfChar); 
        } catch (IOException iOException) {
          throw new IOError(iOException);
        } 
      } 
    } 
    return str;
  }
  
  public String readLine() { return readLine("", new Object[0]); }
  
  public char[] readPassword(String paramString, Object... paramVarArgs) {
    char[] arrayOfChar = null;
    synchronized (this.writeLock) {
      synchronized (this.readLock) {
        try {
          echoOff = echo(false);
        } catch (IOException iOException) {
          throw new IOError(iOException);
        } 
        iOError = null;
        try {
          if (paramString.length() != 0)
            this.pw.format(paramString, paramVarArgs); 
          arrayOfChar = readline(true);
        } catch (IOException iOException) {
          iOError = new IOError(iOException);
        } finally {
          try {
            echoOff = echo(true);
          } catch (IOException iOException) {
            if (iOError == null) {
              iOError = new IOError(iOException);
            } else {
              iOError.addSuppressed(iOException);
            } 
          } 
          if (iOError != null)
            throw iOError; 
        } 
        this.pw.println();
      } 
    } 
    return arrayOfChar;
  }
  
  public char[] readPassword() { return readPassword("", new Object[0]); }
  
  public void flush() { this.pw.flush(); }
  
  private static native String encoding();
  
  private static native boolean echo(boolean paramBoolean) throws IOException;
  
  private char[] readline(boolean paramBoolean) throws IOException {
    int i = this.reader.read(this.rcb, 0, this.rcb.length);
    if (i < 0)
      return null; 
    if (this.rcb[i - 1] == '\r') {
      i--;
    } else if (this.rcb[i - 1] == '\n' && --i > 0 && this.rcb[i - 1] == '\r') {
      i--;
    } 
    char[] arrayOfChar = new char[i];
    if (i > 0) {
      System.arraycopy(this.rcb, 0, arrayOfChar, 0, i);
      if (paramBoolean)
        Arrays.fill(this.rcb, 0, i, ' '); 
    } 
    return arrayOfChar;
  }
  
  private char[] grow() {
    assert Thread.holdsLock(this.readLock);
    char[] arrayOfChar = new char[this.rcb.length * 2];
    System.arraycopy(this.rcb, 0, arrayOfChar, 0, this.rcb.length);
    this.rcb = arrayOfChar;
    return this.rcb;
  }
  
  private static native boolean istty();
  
  private Console() {
    String str = encoding();
    if (str != null)
      try {
        this.cs = Charset.forName(str);
      } catch (Exception exception) {} 
    if (this.cs == null)
      this.cs = Charset.defaultCharset(); 
    this.out = StreamEncoder.forOutputStreamWriter(new FileOutputStream(FileDescriptor.out), this.writeLock, this.cs);
    this.pw = new PrintWriter(this.out, true) {
        public void close() {}
      };
    this.formatter = new Formatter(this.out);
    this.reader = new LineReader(StreamDecoder.forInputStreamReader(new FileInputStream(FileDescriptor.in), this.readLock, this.cs));
    this.rcb = new char[1024];
  }
  
  static  {
    try {
      SharedSecrets.getJavaLangAccess().registerShutdownHook(0, false, new Runnable() {
            public void run() {
              try {
                if (echoOff)
                  Console.echo(true); 
              } catch (IOException iOException) {}
            }
          });
    } catch (IllegalStateException illegalStateException) {}
    SharedSecrets.setJavaIOAccess(new JavaIOAccess() {
          public Console console() {
            if (Console.istty()) {
              if (cons == null)
                cons = new Console(null); 
              return cons;
            } 
            return null;
          }
          
          public Charset charset() { return cons.cs; }
        });
  }
  
  class LineReader extends Reader {
    private Reader in;
    
    private char[] cb;
    
    private int nChars;
    
    private int nextChar;
    
    boolean leftoverLF;
    
    LineReader(Reader param1Reader) {
      this.in = param1Reader;
      this.cb = new char[1024];
      this.nextChar = this.nChars = 0;
      this.leftoverLF = false;
    }
    
    public void close() {}
    
    public boolean ready() { return this.in.ready(); }
    
    public int read(char[] param1ArrayOfChar, int param1Int1, int param1Int2) throws IOException {
      int i = param1Int1;
      int j = param1Int1 + param1Int2;
      if (param1Int1 < 0 || param1Int1 > param1ArrayOfChar.length || param1Int2 < 0 || j < 0 || j > param1ArrayOfChar.length)
        throw new IndexOutOfBoundsException(); 
      synchronized (Console.this.readLock) {
        boolean bool = false;
        char c = Character.MIN_VALUE;
        do {
          if (this.nextChar >= this.nChars) {
            int k = 0;
            do {
              k = this.in.read(this.cb, 0, this.cb.length);
            } while (k == 0);
            if (k > 0) {
              this.nChars = k;
              this.nextChar = 0;
              if (k < this.cb.length && this.cb[k - 1] != '\n' && this.cb[k - 1] != '\r')
                bool = true; 
            } else {
              if (i - param1Int1 == 0)
                return -1; 
              return i - param1Int1;
            } 
          } 
          if (this.leftoverLF && param1ArrayOfChar == Console.this.rcb && this.cb[this.nextChar] == '\n')
            this.nextChar++; 
          this.leftoverLF = false;
          while (this.nextChar < this.nChars) {
            c = param1ArrayOfChar[i++] = this.cb[this.nextChar];
            this.cb[this.nextChar++] = Character.MIN_VALUE;
            if (c == '\n')
              return i - param1Int1; 
            if (c == '\r') {
              if (i == j)
                if (param1ArrayOfChar == Console.this.rcb) {
                  param1ArrayOfChar = Console.this.grow();
                  j = param1ArrayOfChar.length;
                } else {
                  this.leftoverLF = true;
                  return i - param1Int1;
                }  
              if (this.nextChar == this.nChars && this.in.ready()) {
                this.nChars = this.in.read(this.cb, 0, this.cb.length);
                this.nextChar = 0;
              } 
              if (this.nextChar < this.nChars && this.cb[this.nextChar] == '\n') {
                param1ArrayOfChar[i++] = '\n';
                this.nextChar++;
              } 
              return i - param1Int1;
            } 
            if (i == j) {
              if (param1ArrayOfChar == Console.this.rcb) {
                param1ArrayOfChar = Console.this.grow();
                j = param1ArrayOfChar.length;
                continue;
              } 
              return i - param1Int1;
            } 
          } 
        } while (!bool);
        return i - param1Int1;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\Console.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */