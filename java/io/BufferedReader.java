package java.io;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BufferedReader extends Reader {
  private Reader in;
  
  private char[] cb;
  
  private int nChars;
  
  private int nextChar;
  
  private static final int INVALIDATED = -2;
  
  private static final int UNMARKED = -1;
  
  private int markedChar = -1;
  
  private int readAheadLimit = 0;
  
  private boolean skipLF = false;
  
  private boolean markedSkipLF = false;
  
  private static int defaultCharBufferSize = 8192;
  
  private static int defaultExpectedLineLength = 80;
  
  public BufferedReader(Reader paramReader, int paramInt) {
    super(paramReader);
    if (paramInt <= 0)
      throw new IllegalArgumentException("Buffer size <= 0"); 
    this.in = paramReader;
    this.cb = new char[paramInt];
    this.nextChar = this.nChars = 0;
  }
  
  public BufferedReader(Reader paramReader) { this(paramReader, defaultCharBufferSize); }
  
  private void ensureOpen() throws IOException {
    if (this.in == null)
      throw new IOException("Stream closed"); 
  }
  
  private void fill() throws IOException {
    int j;
    int i;
    if (this.markedChar <= -1) {
      i = 0;
    } else {
      j = this.nextChar - this.markedChar;
      if (j >= this.readAheadLimit) {
        this.markedChar = -2;
        this.readAheadLimit = 0;
        i = 0;
      } else {
        if (this.readAheadLimit <= this.cb.length) {
          System.arraycopy(this.cb, this.markedChar, this.cb, 0, j);
          this.markedChar = 0;
          i = j;
        } else {
          char[] arrayOfChar = new char[this.readAheadLimit];
          System.arraycopy(this.cb, this.markedChar, arrayOfChar, 0, j);
          this.cb = arrayOfChar;
          this.markedChar = 0;
          i = j;
        } 
        this.nextChar = this.nChars = j;
      } 
    } 
    do {
      j = this.in.read(this.cb, i, this.cb.length - i);
    } while (j == 0);
    if (j > 0) {
      this.nChars = i + j;
      this.nextChar = i;
    } 
  }
  
  public int read() throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      while (true) {
        if (this.nextChar >= this.nChars) {
          fill();
          if (this.nextChar >= this.nChars)
            return -1; 
        } 
        if (this.skipLF) {
          this.skipLF = false;
          if (this.cb[this.nextChar] == '\n') {
            this.nextChar++;
            continue;
          } 
        } 
        break;
      } 
      return this.cb[this.nextChar++];
    } 
  }
  
  private int read1(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    if (this.nextChar >= this.nChars) {
      if (paramInt2 >= this.cb.length && this.markedChar <= -1 && !this.skipLF)
        return this.in.read(paramArrayOfChar, paramInt1, paramInt2); 
      fill();
    } 
    if (this.nextChar >= this.nChars)
      return -1; 
    if (this.skipLF) {
      this.skipLF = false;
      if (this.cb[this.nextChar] == '\n') {
        this.nextChar++;
        if (this.nextChar >= this.nChars)
          fill(); 
        if (this.nextChar >= this.nChars)
          return -1; 
      } 
    } 
    int i = Math.min(paramInt2, this.nChars - this.nextChar);
    System.arraycopy(this.cb, this.nextChar, paramArrayOfChar, paramInt1, i);
    this.nextChar += i;
    return i;
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      if (paramInt1 < 0 || paramInt1 > paramArrayOfChar.length || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfChar.length || paramInt1 + paramInt2 < 0)
        throw new IndexOutOfBoundsException(); 
      if (paramInt2 == 0)
        return 0; 
      int i = read1(paramArrayOfChar, paramInt1, paramInt2);
      if (i <= 0)
        return i; 
      while (i < paramInt2 && this.in.ready()) {
        int j = read1(paramArrayOfChar, paramInt1 + i, paramInt2 - i);
        if (j <= 0)
          break; 
        i += j;
      } 
      return i;
    } 
  }
  
  String readLine(boolean paramBoolean) throws IOException {
    StringBuffer stringBuffer = null;
    synchronized (this.lock) {
      ensureOpen();
      boolean bool = (paramBoolean || this.skipLF) ? 1 : 0;
      while (true) {
        if (this.nextChar >= this.nChars)
          fill(); 
        if (this.nextChar >= this.nChars) {
          if (stringBuffer != null && stringBuffer.length() > 0)
            return stringBuffer.toString(); 
          return null;
        } 
        boolean bool1 = false;
        char c = Character.MIN_VALUE;
        if (bool && this.cb[this.nextChar] == '\n')
          this.nextChar++; 
        this.skipLF = false;
        bool = false;
        int j;
        for (j = this.nextChar; j < this.nChars; j++) {
          c = this.cb[j];
          if (c == '\n' || c == '\r') {
            bool1 = true;
            break;
          } 
        } 
        int i = this.nextChar;
        this.nextChar = j;
        if (bool1) {
          String str;
          if (stringBuffer == null) {
            str = new String(this.cb, i, j - i);
          } else {
            stringBuffer.append(this.cb, i, j - i);
            str = stringBuffer.toString();
          } 
          this.nextChar++;
          if (c == '\r')
            this.skipLF = true; 
          return str;
        } 
        if (stringBuffer == null)
          stringBuffer = new StringBuffer(defaultExpectedLineLength); 
        stringBuffer.append(this.cb, i, j - i);
      } 
    } 
  }
  
  public String readLine() throws IOException { return readLine(false); }
  
  public long skip(long paramLong) throws IOException {
    if (paramLong < 0L)
      throw new IllegalArgumentException("skip value is negative"); 
    synchronized (this.lock) {
      ensureOpen();
      long l = paramLong;
      while (l > 0L) {
        if (this.nextChar >= this.nChars)
          fill(); 
        if (this.nextChar >= this.nChars)
          break; 
        if (this.skipLF) {
          this.skipLF = false;
          if (this.cb[this.nextChar] == '\n')
            this.nextChar++; 
        } 
        long l1 = (this.nChars - this.nextChar);
        if (l <= l1) {
          this.nextChar = (int)(this.nextChar + l);
          l = 0L;
          break;
        } 
        l -= l1;
        this.nextChar = this.nChars;
      } 
      return paramLong - l;
    } 
  }
  
  public boolean ready() throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      if (this.skipLF) {
        if (this.nextChar >= this.nChars && this.in.ready())
          fill(); 
        if (this.nextChar < this.nChars) {
          if (this.cb[this.nextChar] == '\n')
            this.nextChar++; 
          this.skipLF = false;
        } 
      } 
      return (this.nextChar < this.nChars || this.in.ready());
    } 
  }
  
  public boolean markSupported() throws IOException { return true; }
  
  public void mark(int paramInt) throws IOException {
    if (paramInt < 0)
      throw new IllegalArgumentException("Read-ahead limit < 0"); 
    synchronized (this.lock) {
      ensureOpen();
      this.readAheadLimit = paramInt;
      this.markedChar = this.nextChar;
      this.markedSkipLF = this.skipLF;
    } 
  }
  
  public void reset() throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      if (this.markedChar < 0)
        throw new IOException((this.markedChar == -2) ? "Mark invalid" : "Stream not marked"); 
      this.nextChar = this.markedChar;
      this.skipLF = this.markedSkipLF;
    } 
  }
  
  public void close() throws IOException {
    synchronized (this.lock) {
      if (this.in == null)
        return; 
      try {
        this.in.close();
      } finally {
        this.in = null;
        this.cb = null;
      } 
    } 
  }
  
  public Stream<String> lines() {
    Iterator<String> iterator = new Iterator<String>() {
        String nextLine = null;
        
        public boolean hasNext() throws IOException {
          if (this.nextLine != null)
            return true; 
          try {
            this.nextLine = BufferedReader.this.readLine();
            return (this.nextLine != null);
          } catch (IOException iOException) {
            throw new UncheckedIOException(iOException);
          } 
        }
        
        public String next() throws IOException {
          if (this.nextLine != null || hasNext()) {
            String str = this.nextLine;
            this.nextLine = null;
            return str;
          } 
          throw new NoSuchElementException();
        }
      };
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 272), false);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\BufferedReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */