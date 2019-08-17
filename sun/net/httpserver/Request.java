package sun.net.httpserver;

import com.sun.net.httpserver.Headers;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

class Request {
  static final int BUF_LEN = 2048;
  
  static final byte CR = 13;
  
  static final byte LF = 10;
  
  private String startLine;
  
  private SocketChannel chan;
  
  private InputStream is;
  
  private OutputStream os;
  
  char[] buf = new char[2048];
  
  int pos;
  
  StringBuffer lineBuf;
  
  Headers hdrs = null;
  
  Request(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
    this.is = paramInputStream;
    this.os = paramOutputStream;
    do {
      this.startLine = readLine();
      if (this.startLine == null)
        return; 
    } while (this.startLine != null && this.startLine.equals(""));
  }
  
  public InputStream inputStream() { return this.is; }
  
  public OutputStream outputStream() { return this.os; }
  
  public String readLine() throws IOException {
    boolean bool1 = false;
    boolean bool2 = false;
    this.pos = 0;
    this.lineBuf = new StringBuffer();
    while (!bool2) {
      int i = this.is.read();
      if (i == -1)
        return null; 
      if (bool1) {
        if (i == 10) {
          bool2 = true;
          continue;
        } 
        bool1 = false;
        consume(13);
        consume(i);
        continue;
      } 
      if (i == 13) {
        bool1 = true;
        continue;
      } 
      consume(i);
    } 
    this.lineBuf.append(this.buf, 0, this.pos);
    return new String(this.lineBuf);
  }
  
  private void consume(int paramInt) {
    if (this.pos == 2048) {
      this.lineBuf.append(this.buf);
      this.pos = 0;
    } 
    this.buf[this.pos++] = (char)paramInt;
  }
  
  public String requestLine() throws IOException { return this.startLine; }
  
  Headers headers() throws IOException { // Byte code:
    //   0: aload_0
    //   1: getfield hdrs : Lcom/sun/net/httpserver/Headers;
    //   4: ifnull -> 12
    //   7: aload_0
    //   8: getfield hdrs : Lcom/sun/net/httpserver/Headers;
    //   11: areturn
    //   12: aload_0
    //   13: new com/sun/net/httpserver/Headers
    //   16: dup
    //   17: invokespecial <init> : ()V
    //   20: putfield hdrs : Lcom/sun/net/httpserver/Headers;
    //   23: bipush #10
    //   25: newarray char
    //   27: astore_1
    //   28: iconst_0
    //   29: istore_2
    //   30: aload_0
    //   31: getfield is : Ljava/io/InputStream;
    //   34: invokevirtual read : ()I
    //   37: istore_3
    //   38: iload_3
    //   39: bipush #13
    //   41: if_icmpeq -> 50
    //   44: iload_3
    //   45: bipush #10
    //   47: if_icmpne -> 88
    //   50: aload_0
    //   51: getfield is : Ljava/io/InputStream;
    //   54: invokevirtual read : ()I
    //   57: istore #4
    //   59: iload #4
    //   61: bipush #13
    //   63: if_icmpeq -> 73
    //   66: iload #4
    //   68: bipush #10
    //   70: if_icmpne -> 78
    //   73: aload_0
    //   74: getfield hdrs : Lcom/sun/net/httpserver/Headers;
    //   77: areturn
    //   78: aload_1
    //   79: iconst_0
    //   80: iload_3
    //   81: i2c
    //   82: castore
    //   83: iconst_1
    //   84: istore_2
    //   85: iload #4
    //   87: istore_3
    //   88: iload_3
    //   89: bipush #10
    //   91: if_icmpeq -> 503
    //   94: iload_3
    //   95: bipush #13
    //   97: if_icmpeq -> 503
    //   100: iload_3
    //   101: iflt -> 503
    //   104: iconst_m1
    //   105: istore #4
    //   107: iload_3
    //   108: bipush #32
    //   110: if_icmple -> 117
    //   113: iconst_1
    //   114: goto -> 118
    //   117: iconst_0
    //   118: istore #6
    //   120: aload_1
    //   121: iload_2
    //   122: iinc #2, 1
    //   125: iload_3
    //   126: i2c
    //   127: castore
    //   128: aload_0
    //   129: getfield is : Ljava/io/InputStream;
    //   132: invokevirtual read : ()I
    //   135: dup
    //   136: istore #5
    //   138: iflt -> 326
    //   141: iload #5
    //   143: lookupswitch default -> 288, 9 -> 210, 10 -> 220, 13 -> 220, 32 -> 214, 58 -> 192
    //   192: iload #6
    //   194: ifeq -> 204
    //   197: iload_2
    //   198: ifle -> 204
    //   201: iload_2
    //   202: istore #4
    //   204: iconst_0
    //   205: istore #6
    //   207: goto -> 288
    //   210: bipush #32
    //   212: istore #5
    //   214: iconst_0
    //   215: istore #6
    //   217: goto -> 288
    //   220: aload_0
    //   221: getfield is : Ljava/io/InputStream;
    //   224: invokevirtual read : ()I
    //   227: istore_3
    //   228: iload #5
    //   230: bipush #13
    //   232: if_icmpne -> 263
    //   235: iload_3
    //   236: bipush #10
    //   238: if_icmpne -> 263
    //   241: aload_0
    //   242: getfield is : Ljava/io/InputStream;
    //   245: invokevirtual read : ()I
    //   248: istore_3
    //   249: iload_3
    //   250: bipush #13
    //   252: if_icmpne -> 263
    //   255: aload_0
    //   256: getfield is : Ljava/io/InputStream;
    //   259: invokevirtual read : ()I
    //   262: istore_3
    //   263: iload_3
    //   264: bipush #10
    //   266: if_icmpeq -> 328
    //   269: iload_3
    //   270: bipush #13
    //   272: if_icmpeq -> 328
    //   275: iload_3
    //   276: bipush #32
    //   278: if_icmple -> 284
    //   281: goto -> 328
    //   284: bipush #32
    //   286: istore #5
    //   288: iload_2
    //   289: aload_1
    //   290: arraylength
    //   291: if_icmplt -> 314
    //   294: aload_1
    //   295: arraylength
    //   296: iconst_2
    //   297: imul
    //   298: newarray char
    //   300: astore #7
    //   302: aload_1
    //   303: iconst_0
    //   304: aload #7
    //   306: iconst_0
    //   307: iload_2
    //   308: invokestatic arraycopy : (Ljava/lang/Object;ILjava/lang/Object;II)V
    //   311: aload #7
    //   313: astore_1
    //   314: aload_1
    //   315: iload_2
    //   316: iinc #2, 1
    //   319: iload #5
    //   321: i2c
    //   322: castore
    //   323: goto -> 128
    //   326: iconst_m1
    //   327: istore_3
    //   328: iload_2
    //   329: ifle -> 348
    //   332: aload_1
    //   333: iload_2
    //   334: iconst_1
    //   335: isub
    //   336: caload
    //   337: bipush #32
    //   339: if_icmpgt -> 348
    //   342: iinc #2, -1
    //   345: goto -> 328
    //   348: iload #4
    //   350: ifgt -> 362
    //   353: aconst_null
    //   354: astore #7
    //   356: iconst_0
    //   357: istore #4
    //   359: goto -> 410
    //   362: aload_1
    //   363: iconst_0
    //   364: iload #4
    //   366: invokestatic copyValueOf : ([CII)Ljava/lang/String;
    //   369: astore #7
    //   371: iload #4
    //   373: iload_2
    //   374: if_icmpge -> 389
    //   377: aload_1
    //   378: iload #4
    //   380: caload
    //   381: bipush #58
    //   383: if_icmpne -> 389
    //   386: iinc #4, 1
    //   389: iload #4
    //   391: iload_2
    //   392: if_icmpge -> 410
    //   395: aload_1
    //   396: iload #4
    //   398: caload
    //   399: bipush #32
    //   401: if_icmpgt -> 410
    //   404: iinc #4, 1
    //   407: goto -> 389
    //   410: iload #4
    //   412: iload_2
    //   413: if_icmplt -> 428
    //   416: new java/lang/String
    //   419: dup
    //   420: invokespecial <init> : ()V
    //   423: astore #8
    //   425: goto -> 440
    //   428: aload_1
    //   429: iload #4
    //   431: iload_2
    //   432: iload #4
    //   434: isub
    //   435: invokestatic copyValueOf : ([CII)Ljava/lang/String;
    //   438: astore #8
    //   440: aload_0
    //   441: getfield hdrs : Lcom/sun/net/httpserver/Headers;
    //   444: invokevirtual size : ()I
    //   447: invokestatic getMaxReqHeaders : ()I
    //   450: if_icmplt -> 487
    //   453: new java/io/IOException
    //   456: dup
    //   457: new java/lang/StringBuilder
    //   460: dup
    //   461: invokespecial <init> : ()V
    //   464: ldc 'Maximum number of request headers (sun.net.httpserver.maxReqHeaders) exceeded, '
    //   466: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   469: invokestatic getMaxReqHeaders : ()I
    //   472: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   475: ldc '.'
    //   477: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   480: invokevirtual toString : ()Ljava/lang/String;
    //   483: invokespecial <init> : (Ljava/lang/String;)V
    //   486: athrow
    //   487: aload_0
    //   488: getfield hdrs : Lcom/sun/net/httpserver/Headers;
    //   491: aload #7
    //   493: aload #8
    //   495: invokevirtual add : (Ljava/lang/String;Ljava/lang/String;)V
    //   498: iconst_0
    //   499: istore_2
    //   500: goto -> 88
    //   503: aload_0
    //   504: getfield hdrs : Lcom/sun/net/httpserver/Headers;
    //   507: areturn }
  
  static class ReadStream extends InputStream {
    SocketChannel channel;
    
    ByteBuffer chanbuf;
    
    byte[] one;
    
    private boolean closed = false;
    
    private boolean eof = false;
    
    ByteBuffer markBuf;
    
    boolean marked;
    
    boolean reset;
    
    int readlimit;
    
    static long readTimeout;
    
    ServerImpl server;
    
    static final int BUFSIZE = 8192;
    
    public ReadStream(ServerImpl param1ServerImpl, SocketChannel param1SocketChannel) throws IOException {
      this.channel = param1SocketChannel;
      this.server = param1ServerImpl;
      this.chanbuf = ByteBuffer.allocate(8192);
      this.chanbuf.clear();
      this.one = new byte[1];
      this.closed = this.marked = this.reset = false;
    }
    
    public int read(byte[] param1ArrayOfByte) throws IOException { return read(param1ArrayOfByte, 0, param1ArrayOfByte.length); }
    
    public int read() throws IOException {
      int i = read(this.one, 0, 1);
      return (i == 1) ? (this.one[0] & 0xFF) : -1;
    }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      int i;
      if (this.closed)
        throw new IOException("Stream closed"); 
      if (this.eof)
        return -1; 
      assert this.channel.isBlocking();
      if (param1Int1 < 0 || param1Int2 < 0 || param1Int2 > param1ArrayOfByte.length - param1Int1)
        throw new IndexOutOfBoundsException(); 
      if (this.reset) {
        int j = this.markBuf.remaining();
        i = (j > param1Int2) ? param1Int2 : j;
        this.markBuf.get(param1ArrayOfByte, param1Int1, i);
        if (j == i)
          this.reset = false; 
      } else {
        this.chanbuf.clear();
        if (param1Int2 < 8192)
          this.chanbuf.limit(param1Int2); 
        do {
          i = this.channel.read(this.chanbuf);
        } while (i == 0);
        if (i == -1) {
          this.eof = true;
          return -1;
        } 
        this.chanbuf.flip();
        this.chanbuf.get(param1ArrayOfByte, param1Int1, i);
        if (this.marked)
          try {
            this.markBuf.put(param1ArrayOfByte, param1Int1, i);
          } catch (BufferOverflowException bufferOverflowException) {
            this.marked = false;
          }  
      } 
      return i;
    }
    
    public boolean markSupported() { return true; }
    
    public int available() throws IOException {
      if (this.closed)
        throw new IOException("Stream is closed"); 
      return this.eof ? -1 : (this.reset ? this.markBuf.remaining() : this.chanbuf.remaining());
    }
    
    public void close() throws IOException {
      if (this.closed)
        return; 
      this.channel.close();
      this.closed = true;
    }
    
    public void mark(int param1Int) {
      if (this.closed)
        return; 
      this.readlimit = param1Int;
      this.markBuf = ByteBuffer.allocate(param1Int);
      this.marked = true;
      this.reset = false;
    }
    
    public void reset() throws IOException {
      if (this.closed)
        return; 
      if (!this.marked)
        throw new IOException("Stream not marked"); 
      this.marked = false;
      this.reset = true;
      this.markBuf.flip();
    }
  }
  
  static class WriteStream extends OutputStream {
    SocketChannel channel;
    
    ByteBuffer buf;
    
    SelectionKey key;
    
    boolean closed;
    
    byte[] one;
    
    ServerImpl server;
    
    public WriteStream(ServerImpl param1ServerImpl, SocketChannel param1SocketChannel) throws IOException {
      this.channel = param1SocketChannel;
      this.server = param1ServerImpl;
      assert param1SocketChannel.isBlocking();
      this.closed = false;
      this.one = new byte[1];
      this.buf = ByteBuffer.allocate(4096);
    }
    
    public void write(int param1Int) {
      this.one[0] = (byte)param1Int;
      write(this.one, 0, 1);
    }
    
    public void write(byte[] param1ArrayOfByte) throws IOException { write(param1ArrayOfByte, 0, param1ArrayOfByte.length); }
    
    public void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      int i = param1Int2;
      if (this.closed)
        throw new IOException("stream is closed"); 
      int j = this.buf.capacity();
      if (j < param1Int2) {
        int m = param1Int2 - j;
        this.buf = ByteBuffer.allocate(2 * (j + m));
      } 
      this.buf.clear();
      this.buf.put(param1ArrayOfByte, param1Int1, param1Int2);
      this.buf.flip();
      int k;
      while ((k = this.channel.write(this.buf)) < i) {
        i -= k;
        if (i == 0)
          return; 
      } 
    }
    
    public void close() throws IOException {
      if (this.closed)
        return; 
      this.channel.close();
      this.closed = true;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\Request.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */