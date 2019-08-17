package java.io;

public class LineNumberReader extends BufferedReader {
  private int lineNumber = 0;
  
  private int markedLineNumber;
  
  private boolean skipLF;
  
  private boolean markedSkipLF;
  
  private static final int maxSkipBufferSize = 8192;
  
  private char[] skipBuffer = null;
  
  public LineNumberReader(Reader paramReader) { super(paramReader); }
  
  public LineNumberReader(Reader paramReader, int paramInt) { super(paramReader, paramInt); }
  
  public void setLineNumber(int paramInt) { this.lineNumber = paramInt; }
  
  public int getLineNumber() { return this.lineNumber; }
  
  public int read() {
    synchronized (this.lock) {
      int i = super.read();
      if (this.skipLF) {
        if (i == 10)
          i = super.read(); 
        this.skipLF = false;
      } 
      switch (i) {
        case 13:
          this.skipLF = true;
        case 10:
          this.lineNumber++;
          return 10;
      } 
      return i;
    } 
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    synchronized (this.lock) {
      int i = super.read(paramArrayOfChar, paramInt1, paramInt2);
      for (int j = paramInt1; j < paramInt1 + i; j++) {
        char c = paramArrayOfChar[j];
        if (this.skipLF) {
          this.skipLF = false;
          if (c == '\n')
            continue; 
        } 
        switch (c) {
          case '\r':
            this.skipLF = true;
          case '\n':
            this.lineNumber++;
            break;
        } 
        continue;
      } 
      return i;
    } 
  }
  
  public String readLine() throws IOException {
    synchronized (this.lock) {
      String str = readLine(this.skipLF);
      this.skipLF = false;
      if (str != null)
        this.lineNumber++; 
      return str;
    } 
  }
  
  public long skip(long paramLong) throws IOException {
    if (paramLong < 0L)
      throw new IllegalArgumentException("skip() value is negative"); 
    int i = (int)Math.min(paramLong, 8192L);
    synchronized (this.lock) {
      if (this.skipBuffer == null || this.skipBuffer.length < i)
        this.skipBuffer = new char[i]; 
      long l;
      for (l = paramLong; l > 0L; l -= j) {
        int j = read(this.skipBuffer, 0, (int)Math.min(l, i));
        if (j == -1)
          break; 
      } 
      return paramLong - l;
    } 
  }
  
  public void mark(int paramInt) {
    synchronized (this.lock) {
      super.mark(paramInt);
      this.markedLineNumber = this.lineNumber;
      this.markedSkipLF = this.skipLF;
    } 
  }
  
  public void reset() throws IOException {
    synchronized (this.lock) {
      super.reset();
      this.lineNumber = this.markedLineNumber;
      this.skipLF = this.markedSkipLF;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\LineNumberReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */