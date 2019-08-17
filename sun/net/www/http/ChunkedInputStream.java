package sun.net.www.http;

import java.io.IOException;
import java.io.InputStream;
import sun.net.www.MessageHeader;

public class ChunkedInputStream extends InputStream implements Hurryable {
  private InputStream in;
  
  private HttpClient hc;
  
  private MessageHeader responses;
  
  private int chunkSize;
  
  private int chunkRead;
  
  private byte[] chunkData = new byte[4096];
  
  private int chunkPos;
  
  private int chunkCount;
  
  private byte[] rawData = new byte[32];
  
  private int rawPos;
  
  private int rawCount;
  
  private boolean error;
  
  private boolean closed;
  
  private static final int MAX_CHUNK_HEADER_SIZE = 2050;
  
  static final int STATE_AWAITING_CHUNK_HEADER = 1;
  
  static final int STATE_READING_CHUNK = 2;
  
  static final int STATE_AWAITING_CHUNK_EOL = 3;
  
  static final int STATE_AWAITING_TRAILERS = 4;
  
  static final int STATE_DONE = 5;
  
  private int state;
  
  private void ensureOpen() throws IOException {
    if (this.closed)
      throw new IOException("stream is closed"); 
  }
  
  private void ensureRawAvailable(int paramInt) {
    if (this.rawCount + paramInt > this.rawData.length) {
      int i = this.rawCount - this.rawPos;
      if (i + paramInt > this.rawData.length) {
        byte[] arrayOfByte = new byte[i + paramInt];
        if (i > 0)
          System.arraycopy(this.rawData, this.rawPos, arrayOfByte, 0, i); 
        this.rawData = arrayOfByte;
      } else if (i > 0) {
        System.arraycopy(this.rawData, this.rawPos, this.rawData, 0, i);
      } 
      this.rawCount = i;
      this.rawPos = 0;
    } 
  }
  
  private void closeUnderlying() throws IOException {
    if (this.in == null)
      return; 
    if (!this.error && this.state == 5) {
      this.hc.finished();
    } else if (!hurry()) {
      this.hc.closeServer();
    } 
    this.in = null;
  }
  
  private int fastRead(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    int i = this.chunkSize - this.chunkRead;
    int j = (i < paramInt2) ? i : paramInt2;
    if (j > 0) {
      int k;
      try {
        k = this.in.read(paramArrayOfByte, paramInt1, j);
      } catch (IOException iOException) {
        this.error = true;
        throw iOException;
      } 
      if (k > 0) {
        this.chunkRead += k;
        if (this.chunkRead >= this.chunkSize)
          this.state = 3; 
        return k;
      } 
      this.error = true;
      throw new IOException("Premature EOF");
    } 
    return 0;
  }
  
  private void processRaw() throws IOException {
    while (this.state != 5) {
      String str4;
      String str3;
      String str2;
      int k;
      String str1;
      int j;
      int i;
      switch (this.state) {
        case 1:
          i = this.rawPos;
          while (i < this.rawCount && this.rawData[i] != 10) {
            if (++i - this.rawPos >= 2050) {
              this.error = true;
              throw new IOException("Chunk header too long");
            } 
          } 
          if (i >= this.rawCount)
            return; 
          str1 = new String(this.rawData, this.rawPos, i - this.rawPos + 1, "US-ASCII");
          for (j = 0; j < str1.length() && Character.digit(str1.charAt(j), 16) != -1; j++);
          try {
            this.chunkSize = Integer.parseInt(str1.substring(0, j), 16);
          } catch (NumberFormatException numberFormatException) {
            this.error = true;
            throw new IOException("Bogus chunk size");
          } 
          this.rawPos = i + 1;
          this.chunkRead = 0;
          if (this.chunkSize > 0) {
            this.state = 2;
            continue;
          } 
          this.state = 4;
        case 2:
          if (this.rawPos >= this.rawCount)
            return; 
          k = Math.min(this.chunkSize - this.chunkRead, this.rawCount - this.rawPos);
          if (this.chunkData.length < this.chunkCount + k) {
            int m = this.chunkCount - this.chunkPos;
            if (this.chunkData.length < m + k) {
              byte[] arrayOfByte = new byte[m + k];
              System.arraycopy(this.chunkData, this.chunkPos, arrayOfByte, 0, m);
              this.chunkData = arrayOfByte;
            } else {
              System.arraycopy(this.chunkData, this.chunkPos, this.chunkData, 0, m);
            } 
            this.chunkPos = 0;
            this.chunkCount = m;
          } 
          System.arraycopy(this.rawData, this.rawPos, this.chunkData, this.chunkCount, k);
          this.rawPos += k;
          this.chunkCount += k;
          this.chunkRead += k;
          if (this.chunkSize - this.chunkRead <= 0) {
            this.state = 3;
            continue;
          } 
          return;
        case 3:
          if (this.rawPos + 1 >= this.rawCount)
            return; 
          if (this.rawData[this.rawPos] != 13) {
            this.error = true;
            throw new IOException("missing CR");
          } 
          if (this.rawData[this.rawPos + 1] != 10) {
            this.error = true;
            throw new IOException("missing LF");
          } 
          this.rawPos += 2;
          this.state = 1;
        case 4:
          for (i = this.rawPos; i < this.rawCount && this.rawData[i] != 10; i++);
          if (i >= this.rawCount)
            return; 
          if (i == this.rawPos) {
            this.error = true;
            throw new IOException("LF should be proceeded by CR");
          } 
          if (this.rawData[i - 1] != 13) {
            this.error = true;
            throw new IOException("LF should be proceeded by CR");
          } 
          if (i == this.rawPos + 1) {
            this.state = 5;
            closeUnderlying();
            return;
          } 
          str2 = new String(this.rawData, this.rawPos, i - this.rawPos, "US-ASCII");
          j = str2.indexOf(':');
          if (j == -1)
            throw new IOException("Malformed tailer - format should be key:value"); 
          str3 = str2.substring(0, j).trim();
          str4 = str2.substring(j + 1, str2.length()).trim();
          this.responses.add(str3, str4);
          this.rawPos = i + 1;
      } 
    } 
  }
  
  private int readAheadNonBlocking() throws IOException {
    int i = this.in.available();
    if (i > 0) {
      int j;
      ensureRawAvailable(i);
      try {
        j = this.in.read(this.rawData, this.rawCount, i);
      } catch (IOException iOException) {
        this.error = true;
        throw iOException;
      } 
      if (j < 0) {
        this.error = true;
        return -1;
      } 
      this.rawCount += j;
      processRaw();
    } 
    return this.chunkCount - this.chunkPos;
  }
  
  private int readAheadBlocking() throws IOException {
    do {
      int i;
      if (this.state == 5)
        return -1; 
      ensureRawAvailable(32);
      try {
        i = this.in.read(this.rawData, this.rawCount, this.rawData.length - this.rawCount);
      } catch (IOException iOException) {
        this.error = true;
        throw iOException;
      } 
      if (i < 0) {
        this.error = true;
        throw new IOException("Premature EOF");
      } 
      this.rawCount += i;
      processRaw();
    } while (this.chunkCount <= 0);
    return this.chunkCount - this.chunkPos;
  }
  
  private int readAhead(boolean paramBoolean) throws IOException {
    if (this.state == 5)
      return -1; 
    if (this.chunkPos >= this.chunkCount) {
      this.chunkCount = 0;
      this.chunkPos = 0;
    } 
    return paramBoolean ? readAheadBlocking() : readAheadNonBlocking();
  }
  
  public ChunkedInputStream(InputStream paramInputStream, HttpClient paramHttpClient, MessageHeader paramMessageHeader) throws IOException {
    this.in = paramInputStream;
    this.responses = paramMessageHeader;
    this.hc = paramHttpClient;
    this.state = 1;
  }
  
  public int read() throws IOException {
    ensureOpen();
    return (this.chunkPos >= this.chunkCount && readAhead(true) <= 0) ? -1 : (this.chunkData[this.chunkPos++] & 0xFF);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    ensureOpen();
    if (paramInt1 < 0 || paramInt1 > paramArrayOfByte.length || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfByte.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return 0; 
    int i = this.chunkCount - this.chunkPos;
    if (i <= 0) {
      if (this.state == 2)
        return fastRead(paramArrayOfByte, paramInt1, paramInt2); 
      i = readAhead(true);
      if (i < 0)
        return -1; 
    } 
    int j = (i < paramInt2) ? i : paramInt2;
    System.arraycopy(this.chunkData, this.chunkPos, paramArrayOfByte, paramInt1, j);
    this.chunkPos += j;
    return j;
  }
  
  public int available() throws IOException {
    ensureOpen();
    int i = this.chunkCount - this.chunkPos;
    if (i > 0)
      return i; 
    i = readAhead(false);
    return (i < 0) ? 0 : i;
  }
  
  public void close() throws IOException {
    if (this.closed)
      return; 
    closeUnderlying();
    this.closed = true;
  }
  
  public boolean hurry() {
    if (this.in == null || this.error)
      return false; 
    try {
      readAhead(false);
    } catch (Exception exception) {
      return false;
    } 
    return this.error ? false : ((this.state == 5));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\http\ChunkedInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */