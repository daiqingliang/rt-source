package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

class MIMEParser extends Object implements Iterable<MIMEEvent> {
  private static final Logger LOGGER = Logger.getLogger(MIMEParser.class.getName());
  
  private static final String HEADER_ENCODING = "ISO8859-1";
  
  private static final int NO_LWSP = 1000;
  
  private STATE state = STATE.START_MESSAGE;
  
  private final InputStream in;
  
  private final byte[] bndbytes;
  
  private final int bl;
  
  private final MIMEConfig config;
  
  private final int[] bcs = new int[128];
  
  private final int[] gss;
  
  private boolean parsed;
  
  private boolean done = false;
  
  private boolean eof;
  
  private final int capacity;
  
  private byte[] buf;
  
  private int len;
  
  private boolean bol;
  
  MIMEParser(InputStream paramInputStream, String paramString, MIMEConfig paramMIMEConfig) {
    this.in = paramInputStream;
    this.bndbytes = getBytes("--" + paramString);
    this.bl = this.bndbytes.length;
    this.config = paramMIMEConfig;
    this.gss = new int[this.bl];
    compileBoundaryPattern();
    this.capacity = paramMIMEConfig.chunkSize + 2 + this.bl + 4 + 1000;
    createBuf(this.capacity);
  }
  
  public Iterator<MIMEEvent> iterator() { return new MIMEEventIterator(); }
  
  private InternetHeaders readHeaders() {
    if (!this.eof)
      fillBuf(); 
    return new InternetHeaders(new LineInputStream(this));
  }
  
  private ByteBuffer readBody() {
    if (!this.eof)
      fillBuf(); 
    int i = match(this.buf, 0, this.len);
    if (i == -1) {
      assert this.eof || this.len >= this.config.chunkSize;
      int n = this.eof ? this.len : this.config.chunkSize;
      if (this.eof) {
        this.done = true;
        throw new MIMEParsingException("Reached EOF, but there is no closing MIME boundary.");
      } 
      return adjustBuf(n, this.len - n);
    } 
    int j = i;
    if (!this.bol || i != 0)
      if (i > 0 && (this.buf[i - 1] == 10 || this.buf[i - 1] == 13)) {
        j--;
        if (this.buf[i - 1] == 10 && i > 1 && this.buf[i - 2] == 13)
          j--; 
      } else {
        return adjustBuf(i + 1, this.len - i - 1);
      }  
    if (i + this.bl + 1 < this.len && this.buf[i + this.bl] == 45 && this.buf[i + this.bl + 1] == 45) {
      this.state = STATE.END_PART;
      this.done = true;
      return adjustBuf(j, 0);
    } 
    int k = 0;
    for (int m = i + this.bl; m < this.len && (this.buf[m] == 32 || this.buf[m] == 9); m++)
      k++; 
    if (i + this.bl + k < this.len && this.buf[i + this.bl + k] == 10) {
      this.state = STATE.END_PART;
      return adjustBuf(j, this.len - i - this.bl - k - 1);
    } 
    if (i + this.bl + k + 1 < this.len && this.buf[i + this.bl + k] == 13 && this.buf[i + this.bl + k + 1] == 10) {
      this.state = STATE.END_PART;
      return adjustBuf(j, this.len - i - this.bl - k - 2);
    } 
    if (i + this.bl + k + 1 < this.len)
      return adjustBuf(j + 1, this.len - j - 1); 
    if (this.eof) {
      this.done = true;
      throw new MIMEParsingException("Reached EOF, but there is no closing MIME boundary.");
    } 
    return adjustBuf(j, this.len - j);
  }
  
  private ByteBuffer adjustBuf(int paramInt1, int paramInt2) {
    assert this.buf != null;
    assert paramInt1 >= 0;
    assert paramInt2 >= 0;
    byte[] arrayOfByte = this.buf;
    createBuf(paramInt2);
    System.arraycopy(arrayOfByte, this.len - paramInt2, this.buf, 0, paramInt2);
    this.len = paramInt2;
    return ByteBuffer.wrap(arrayOfByte, 0, paramInt1);
  }
  
  private void createBuf(int paramInt) { this.buf = new byte[(paramInt < this.capacity) ? this.capacity : paramInt]; }
  
  private void skipPreamble() {
    while (true) {
      if (!this.eof)
        fillBuf(); 
      int i = match(this.buf, 0, this.len);
      if (i == -1) {
        if (this.eof)
          throw new MIMEParsingException("Missing start boundary"); 
        adjustBuf(this.len - this.bl + 1, this.bl - 1);
        continue;
      } 
      if (i > this.config.chunkSize) {
        adjustBuf(i, this.len - i);
        continue;
      } 
      int j = 0;
      for (int k = i + this.bl; k < this.len && (this.buf[k] == 32 || this.buf[k] == 9); k++)
        j++; 
      if (i + this.bl + j < this.len && (this.buf[i + this.bl + j] == 10 || this.buf[i + this.bl + j] == 13)) {
        if (this.buf[i + this.bl + j] == 10) {
          adjustBuf(i + this.bl + j + 1, this.len - i - this.bl - j - 1);
          break;
        } 
        if (i + this.bl + j + 1 < this.len && this.buf[i + this.bl + j + 1] == 10) {
          adjustBuf(i + this.bl + j + 2, this.len - i - this.bl - j - 2);
          break;
        } 
      } 
      adjustBuf(i + 1, this.len - i - 1);
    } 
    if (LOGGER.isLoggable(Level.FINE))
      LOGGER.log(Level.FINE, "Skipped the preamble. buffer len={0}", Integer.valueOf(this.len)); 
  }
  
  private static byte[] getBytes(String paramString) {
    char[] arrayOfChar = paramString.toCharArray();
    int i = arrayOfChar.length;
    byte[] arrayOfByte = new byte[i];
    byte b = 0;
    while (b < i)
      arrayOfByte[b] = (byte)arrayOfChar[b++]; 
    return arrayOfByte;
  }
  
  private void compileBoundaryPattern() {
    int i;
    for (i = 0; i < this.bndbytes.length; i++)
      this.bcs[this.bndbytes[i] & 0x7F] = i + true; 
    for (i = this.bndbytes.length; i > 0; i--) {
      int j = this.bndbytes.length - 1;
      while (true) {
        if (j >= i) {
          if (this.bndbytes[j] == this.bndbytes[j - i]) {
            this.gss[j - 1] = i;
            j--;
            continue;
          } 
          break;
        } 
        while (j > 0)
          this.gss[--j] = i; 
        break;
      } 
    } 
    this.gss[this.bndbytes.length - 1] = 1;
  }
  
  private int match(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    int i = paramInt2 - this.bndbytes.length;
    label15: while (paramInt1 <= i) {
      for (int j = this.bndbytes.length - 1; j >= 0; j--) {
        byte b = paramArrayOfByte[paramInt1 + j];
        if (b != this.bndbytes[j]) {
          paramInt1 += Math.max(j + 1 - this.bcs[b & 0x7F], this.gss[j]);
          continue label15;
        } 
      } 
      return paramInt1;
    } 
    return -1;
  }
  
  private void fillBuf() {
    if (LOGGER.isLoggable(Level.FINER))
      LOGGER.log(Level.FINER, "Before fillBuf() buffer len={0}", Integer.valueOf(this.len)); 
    assert !this.eof;
    while (this.len < this.buf.length) {
      int i;
      try {
        i = this.in.read(this.buf, this.len, this.buf.length - this.len);
      } catch (IOException iOException) {
        throw new MIMEParsingException(iOException);
      } 
      if (i == -1) {
        this.eof = true;
        try {
          if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine("Closing the input stream."); 
          this.in.close();
          break;
        } catch (IOException iOException) {
          throw new MIMEParsingException(iOException);
        } 
      } 
      this.len += i;
    } 
    if (LOGGER.isLoggable(Level.FINER))
      LOGGER.log(Level.FINER, "After fillBuf() buffer len={0}", Integer.valueOf(this.len)); 
  }
  
  private void doubleBuf() {
    byte[] arrayOfByte = new byte[2 * this.len];
    System.arraycopy(this.buf, 0, arrayOfByte, 0, this.len);
    this.buf = arrayOfByte;
    if (!this.eof)
      fillBuf(); 
  }
  
  class LineInputStream {
    private int offset;
    
    public String readLine() throws IOException {
      int i = 0;
      int j = 0;
      while (this.offset + i < MIMEParser.this.len) {
        if (MIMEParser.this.buf[this.offset + i] == 10) {
          j = 1;
          break;
        } 
        if (this.offset + i + 1 == MIMEParser.this.len)
          MIMEParser.this.doubleBuf(); 
        if (this.offset + i + 1 >= MIMEParser.this.len) {
          assert MIMEParser.this.eof;
          return null;
        } 
        if (MIMEParser.this.buf[this.offset + i] == 13 && MIMEParser.this.buf[this.offset + i + 1] == 10) {
          j = 2;
          break;
        } 
        i++;
      } 
      if (i == 0) {
        MIMEParser.this.adjustBuf(this.offset + j, MIMEParser.this.len - this.offset - j);
        return null;
      } 
      String str = new String(MIMEParser.this.buf, this.offset, i, "ISO8859-1");
      this.offset += i + j;
      return str;
    }
  }
  
  class MIMEEventIterator extends Object implements Iterator<MIMEEvent> {
    public boolean hasNext() { return !MIMEParser.this.parsed; }
    
    public MIMEEvent next() {
      ByteBuffer byteBuffer;
      InternetHeaders internetHeaders;
      switch (MIMEParser.null.$SwitchMap$com$sun$xml$internal$org$jvnet$mimepull$MIMEParser$STATE[MIMEParser.this.state.ordinal()]) {
        case 1:
          if (LOGGER.isLoggable(Level.FINER))
            LOGGER.log(Level.FINER, "MIMEParser state={0}", MIMEParser.STATE.START_MESSAGE); 
          MIMEParser.this.state = MIMEParser.STATE.SKIP_PREAMBLE;
          return MIMEEvent.START_MESSAGE;
        case 2:
          if (LOGGER.isLoggable(Level.FINER))
            LOGGER.log(Level.FINER, "MIMEParser state={0}", MIMEParser.STATE.SKIP_PREAMBLE); 
          MIMEParser.this.skipPreamble();
        case 3:
          if (LOGGER.isLoggable(Level.FINER))
            LOGGER.log(Level.FINER, "MIMEParser state={0}", MIMEParser.STATE.START_PART); 
          MIMEParser.this.state = MIMEParser.STATE.HEADERS;
          return MIMEEvent.START_PART;
        case 4:
          if (LOGGER.isLoggable(Level.FINER))
            LOGGER.log(Level.FINER, "MIMEParser state={0}", MIMEParser.STATE.HEADERS); 
          internetHeaders = MIMEParser.this.readHeaders();
          MIMEParser.this.state = MIMEParser.STATE.BODY;
          MIMEParser.this.bol = true;
          return new MIMEEvent.Headers(internetHeaders);
        case 5:
          if (LOGGER.isLoggable(Level.FINER))
            LOGGER.log(Level.FINER, "MIMEParser state={0}", MIMEParser.STATE.BODY); 
          byteBuffer = MIMEParser.this.readBody();
          MIMEParser.this.bol = false;
          return new MIMEEvent.Content(byteBuffer);
        case 6:
          if (LOGGER.isLoggable(Level.FINER))
            LOGGER.log(Level.FINER, "MIMEParser state={0}", MIMEParser.STATE.END_PART); 
          if (MIMEParser.this.done) {
            MIMEParser.this.state = MIMEParser.STATE.END_MESSAGE;
          } else {
            MIMEParser.this.state = MIMEParser.STATE.START_PART;
          } 
          return MIMEEvent.END_PART;
        case 7:
          if (LOGGER.isLoggable(Level.FINER))
            LOGGER.log(Level.FINER, "MIMEParser state={0}", MIMEParser.STATE.END_MESSAGE); 
          MIMEParser.this.parsed = true;
          return MIMEEvent.END_MESSAGE;
      } 
      throw new MIMEParsingException("Unknown Parser state = " + MIMEParser.this.state);
    }
    
    public void remove() { throw new UnsupportedOperationException(); }
  }
  
  private enum STATE {
    START_MESSAGE, SKIP_PREAMBLE, START_PART, HEADERS, BODY, END_PART, END_MESSAGE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\MIMEParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */