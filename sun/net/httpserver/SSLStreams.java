package sun.net.httpserver;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;

class SSLStreams {
  SSLContext sslctx;
  
  SocketChannel chan;
  
  TimeSource time;
  
  ServerImpl server;
  
  SSLEngine engine;
  
  EngineWrapper wrapper;
  
  OutputStream os;
  
  InputStream is;
  
  Lock handshaking = new ReentrantLock();
  
  int app_buf_size;
  
  int packet_buf_size;
  
  SSLStreams(ServerImpl paramServerImpl, SSLContext paramSSLContext, SocketChannel paramSocketChannel) throws IOException {
    this.server = paramServerImpl;
    this.time = paramServerImpl;
    this.sslctx = paramSSLContext;
    this.chan = paramSocketChannel;
    InetSocketAddress inetSocketAddress = (InetSocketAddress)paramSocketChannel.socket().getRemoteSocketAddress();
    this.engine = paramSSLContext.createSSLEngine(inetSocketAddress.getHostName(), inetSocketAddress.getPort());
    this.engine.setUseClientMode(false);
    HttpsConfigurator httpsConfigurator = paramServerImpl.getHttpsConfigurator();
    configureEngine(httpsConfigurator, inetSocketAddress);
    this.wrapper = new EngineWrapper(paramSocketChannel, this.engine);
  }
  
  private void configureEngine(HttpsConfigurator paramHttpsConfigurator, InetSocketAddress paramInetSocketAddress) {
    if (paramHttpsConfigurator != null) {
      Parameters parameters = new Parameters(paramHttpsConfigurator, paramInetSocketAddress);
      paramHttpsConfigurator.configure(parameters);
      SSLParameters sSLParameters = parameters.getSSLParameters();
      if (sSLParameters != null) {
        this.engine.setSSLParameters(sSLParameters);
      } else {
        if (parameters.getCipherSuites() != null)
          try {
            this.engine.setEnabledCipherSuites(parameters.getCipherSuites());
          } catch (IllegalArgumentException illegalArgumentException) {} 
        this.engine.setNeedClientAuth(parameters.getNeedClientAuth());
        this.engine.setWantClientAuth(parameters.getWantClientAuth());
        if (parameters.getProtocols() != null)
          try {
            this.engine.setEnabledProtocols(parameters.getProtocols());
          } catch (IllegalArgumentException illegalArgumentException) {} 
      } 
    } 
  }
  
  void close() throws IOException { this.wrapper.close(); }
  
  InputStream getInputStream() throws IOException {
    if (this.is == null)
      this.is = new InputStream(); 
    return this.is;
  }
  
  OutputStream getOutputStream() throws IOException {
    if (this.os == null)
      this.os = new OutputStream(); 
    return this.os;
  }
  
  SSLEngine getSSLEngine() { return this.engine; }
  
  void beginHandshake() throws IOException { this.engine.beginHandshake(); }
  
  private ByteBuffer allocate(BufType paramBufType) { return allocate(paramBufType, -1); }
  
  private ByteBuffer allocate(BufType paramBufType, int paramInt) {
    assert this.engine != null;
    synchronized (this) {
      int i;
      if (paramBufType == BufType.PACKET) {
        if (this.packet_buf_size == 0) {
          SSLSession sSLSession = this.engine.getSession();
          this.packet_buf_size = sSLSession.getPacketBufferSize();
        } 
        if (paramInt > this.packet_buf_size)
          this.packet_buf_size = paramInt; 
        i = this.packet_buf_size;
      } else {
        if (this.app_buf_size == 0) {
          SSLSession sSLSession = this.engine.getSession();
          this.app_buf_size = sSLSession.getApplicationBufferSize();
        } 
        if (paramInt > this.app_buf_size)
          this.app_buf_size = paramInt; 
        i = this.app_buf_size;
      } 
      return ByteBuffer.allocate(i);
    } 
  }
  
  private ByteBuffer realloc(ByteBuffer paramByteBuffer, boolean paramBoolean, BufType paramBufType) {
    synchronized (this) {
      int i = 2 * paramByteBuffer.capacity();
      ByteBuffer byteBuffer = allocate(paramBufType, i);
      if (paramBoolean)
        paramByteBuffer.flip(); 
      byteBuffer.put(paramByteBuffer);
      paramByteBuffer = byteBuffer;
    } 
    return paramByteBuffer;
  }
  
  public WrapperResult sendData(ByteBuffer paramByteBuffer) throws IOException {
    WrapperResult wrapperResult = null;
    while (paramByteBuffer.remaining() > 0) {
      wrapperResult = this.wrapper.wrapAndSend(paramByteBuffer);
      SSLEngineResult.Status status = wrapperResult.result.getStatus();
      if (status == SSLEngineResult.Status.CLOSED) {
        doClosure();
        return wrapperResult;
      } 
      SSLEngineResult.HandshakeStatus handshakeStatus = wrapperResult.result.getHandshakeStatus();
      if (handshakeStatus != SSLEngineResult.HandshakeStatus.FINISHED && handshakeStatus != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING)
        doHandshake(handshakeStatus); 
    } 
    return wrapperResult;
  }
  
  public WrapperResult recvData(ByteBuffer paramByteBuffer) throws IOException {
    WrapperResult wrapperResult = null;
    assert paramByteBuffer.position() == 0;
    while (paramByteBuffer.position() == 0) {
      wrapperResult = this.wrapper.recvAndUnwrap(paramByteBuffer);
      paramByteBuffer = (wrapperResult.buf != paramByteBuffer) ? wrapperResult.buf : paramByteBuffer;
      SSLEngineResult.Status status = wrapperResult.result.getStatus();
      if (status == SSLEngineResult.Status.CLOSED) {
        doClosure();
        return wrapperResult;
      } 
      SSLEngineResult.HandshakeStatus handshakeStatus = wrapperResult.result.getHandshakeStatus();
      if (handshakeStatus != SSLEngineResult.HandshakeStatus.FINISHED && handshakeStatus != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING)
        doHandshake(handshakeStatus); 
    } 
    paramByteBuffer.flip();
    return wrapperResult;
  }
  
  void doClosure() throws IOException {
    try {
      WrapperResult wrapperResult;
      this.handshaking.lock();
      ByteBuffer byteBuffer = allocate(BufType.APPLICATION);
      do {
        byteBuffer.clear();
        byteBuffer.flip();
        wrapperResult = this.wrapper.wrapAndSendX(byteBuffer, true);
      } while (wrapperResult.result.getStatus() != SSLEngineResult.Status.CLOSED);
    } finally {
      this.handshaking.unlock();
    } 
  }
  
  void doHandshake(SSLEngineResult.HandshakeStatus paramHandshakeStatus) throws IOException {
    try {
      this.handshaking.lock();
      ByteBuffer byteBuffer = allocate(BufType.APPLICATION);
      while (paramHandshakeStatus != SSLEngineResult.HandshakeStatus.FINISHED && paramHandshakeStatus != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
        Runnable runnable;
        WrapperResult wrapperResult = null;
        switch (paramHandshakeStatus) {
          case NEED_TASK:
            while ((runnable = this.engine.getDelegatedTask()) != null)
              runnable.run(); 
          case NEED_WRAP:
            byteBuffer.clear();
            byteBuffer.flip();
            wrapperResult = this.wrapper.wrapAndSend(byteBuffer);
            break;
          case NEED_UNWRAP:
            byteBuffer.clear();
            wrapperResult = this.wrapper.recvAndUnwrap(byteBuffer);
            if (wrapperResult.buf != byteBuffer)
              byteBuffer = wrapperResult.buf; 
            assert byteBuffer.position() == 0;
            break;
        } 
        paramHandshakeStatus = wrapperResult.result.getHandshakeStatus();
      } 
    } finally {
      this.handshaking.unlock();
    } 
  }
  
  enum BufType {
    PACKET, APPLICATION;
  }
  
  class EngineWrapper {
    SocketChannel chan;
    
    SSLEngine engine;
    
    Object wrapLock;
    
    Object unwrapLock;
    
    ByteBuffer unwrap_src;
    
    ByteBuffer wrap_dst;
    
    boolean closed = false;
    
    int u_remaining;
    
    EngineWrapper(SocketChannel param1SocketChannel, SSLEngine param1SSLEngine) throws IOException {
      this.chan = param1SocketChannel;
      this.engine = param1SSLEngine;
      this.wrapLock = new Object();
      this.unwrapLock = new Object();
      this.unwrap_src = this$0.allocate(SSLStreams.BufType.PACKET);
      this.wrap_dst = this$0.allocate(SSLStreams.BufType.PACKET);
    }
    
    void close() throws IOException {}
    
    SSLStreams.WrapperResult wrapAndSend(ByteBuffer param1ByteBuffer) throws IOException { return wrapAndSendX(param1ByteBuffer, false); }
    
    SSLStreams.WrapperResult wrapAndSendX(ByteBuffer param1ByteBuffer, boolean param1Boolean) throws IOException {
      if (this.closed && !param1Boolean)
        throw new IOException("Engine is closed"); 
      SSLStreams.WrapperResult wrapperResult = new SSLStreams.WrapperResult(SSLStreams.this);
      synchronized (this.wrapLock) {
        SSLEngineResult.Status status;
        this.wrap_dst.clear();
        do {
          wrapperResult.result = this.engine.wrap(param1ByteBuffer, this.wrap_dst);
          status = wrapperResult.result.getStatus();
          if (status != SSLEngineResult.Status.BUFFER_OVERFLOW)
            continue; 
          this.wrap_dst = SSLStreams.this.realloc(this.wrap_dst, true, SSLStreams.BufType.PACKET);
        } while (status == SSLEngineResult.Status.BUFFER_OVERFLOW);
        if (status == SSLEngineResult.Status.CLOSED && !param1Boolean) {
          this.closed = true;
          return wrapperResult;
        } 
        if (wrapperResult.result.bytesProduced() > 0) {
          this.wrap_dst.flip();
          int i = this.wrap_dst.remaining();
          assert i == wrapperResult.result.bytesProduced();
          while (i > 0)
            i -= this.chan.write(this.wrap_dst); 
        } 
      } 
      return wrapperResult;
    }
    
    SSLStreams.WrapperResult recvAndUnwrap(ByteBuffer param1ByteBuffer) throws IOException {
      boolean bool;
      SSLEngineResult.Status status = SSLEngineResult.Status.OK;
      SSLStreams.WrapperResult wrapperResult = new SSLStreams.WrapperResult(SSLStreams.this);
      wrapperResult.buf = param1ByteBuffer;
      if (this.closed)
        throw new IOException("Engine is closed"); 
      if (this.u_remaining > 0) {
        this.unwrap_src.compact();
        this.unwrap_src.flip();
        bool = false;
      } else {
        this.unwrap_src.clear();
        bool = true;
      } 
      synchronized (this.unwrapLock) {
        do {
          if (bool) {
            int i;
            do {
              i = this.chan.read(this.unwrap_src);
            } while (i == 0);
            if (i == -1)
              throw new IOException("connection closed for reading"); 
            this.unwrap_src.flip();
          } 
          wrapperResult.result = this.engine.unwrap(this.unwrap_src, wrapperResult.buf);
          status = wrapperResult.result.getStatus();
          if (status == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
            if (this.unwrap_src.limit() == this.unwrap_src.capacity()) {
              this.unwrap_src = SSLStreams.this.realloc(this.unwrap_src, false, SSLStreams.BufType.PACKET);
            } else {
              this.unwrap_src.position(this.unwrap_src.limit());
              this.unwrap_src.limit(this.unwrap_src.capacity());
            } 
            bool = true;
          } else if (status == SSLEngineResult.Status.BUFFER_OVERFLOW) {
            wrapperResult.buf = SSLStreams.this.realloc(wrapperResult.buf, true, SSLStreams.BufType.APPLICATION);
            bool = false;
          } else if (status == SSLEngineResult.Status.CLOSED) {
            this.closed = true;
            wrapperResult.buf.flip();
            return wrapperResult;
          } 
        } while (status != SSLEngineResult.Status.OK);
      } 
      this.u_remaining = this.unwrap_src.remaining();
      return wrapperResult;
    }
  }
  
  class InputStream extends InputStream {
    ByteBuffer bbuf;
    
    boolean closed = false;
    
    boolean eof = false;
    
    boolean needData = true;
    
    byte[] single = new byte[1];
    
    InputStream() { this.bbuf = this$0.allocate(SSLStreams.BufType.APPLICATION); }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      if (this.closed)
        throw new IOException("SSL stream is closed"); 
      if (this.eof)
        return 0; 
      int i = 0;
      if (!this.needData) {
        i = this.bbuf.remaining();
        this.needData = (i == 0);
      } 
      if (this.needData) {
        this.bbuf.clear();
        SSLStreams.WrapperResult wrapperResult = SSLStreams.this.recvData(this.bbuf);
        this.bbuf = (wrapperResult.buf == this.bbuf) ? this.bbuf : wrapperResult.buf;
        if ((i = this.bbuf.remaining()) == 0) {
          this.eof = true;
          return 0;
        } 
        this.needData = false;
      } 
      if (param1Int2 > i)
        param1Int2 = i; 
      this.bbuf.get(param1ArrayOfByte, param1Int1, param1Int2);
      return param1Int2;
    }
    
    public int available() throws IOException { return this.bbuf.remaining(); }
    
    public boolean markSupported() { return false; }
    
    public void reset() throws IOException { throw new IOException("mark/reset not supported"); }
    
    public long skip(long param1Long) throws IOException {
      int i = (int)param1Long;
      if (this.closed)
        throw new IOException("SSL stream is closed"); 
      if (this.eof)
        return 0L; 
      int j = i;
      while (i > 0) {
        if (this.bbuf.remaining() >= i) {
          this.bbuf.position(this.bbuf.position() + i);
          return j;
        } 
        i -= this.bbuf.remaining();
        this.bbuf.clear();
        SSLStreams.WrapperResult wrapperResult = SSLStreams.this.recvData(this.bbuf);
        this.bbuf = (wrapperResult.buf == this.bbuf) ? this.bbuf : wrapperResult.buf;
      } 
      return j;
    }
    
    public void close() throws IOException {
      this.eof = true;
      SSLStreams.this.engine.closeInbound();
    }
    
    public int read(byte[] param1ArrayOfByte) throws IOException { return read(param1ArrayOfByte, 0, param1ArrayOfByte.length); }
    
    public int read() throws IOException {
      int i = read(this.single, 0, 1);
      return (i == 0) ? -1 : (this.single[0] & 0xFF);
    }
  }
  
  class OutputStream extends OutputStream {
    ByteBuffer buf;
    
    boolean closed = false;
    
    byte[] single = new byte[1];
    
    OutputStream() { this.buf = this$0.allocate(SSLStreams.BufType.APPLICATION); }
    
    public void write(int param1Int) throws IOException {
      this.single[0] = (byte)param1Int;
      write(this.single, 0, 1);
    }
    
    public void write(byte[] param1ArrayOfByte) throws IOException { write(param1ArrayOfByte, 0, param1ArrayOfByte.length); }
    
    public void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      if (this.closed)
        throw new IOException("output stream is closed"); 
      while (param1Int2 > 0) {
        int i = (param1Int2 > this.buf.capacity()) ? this.buf.capacity() : param1Int2;
        this.buf.clear();
        this.buf.put(param1ArrayOfByte, param1Int1, i);
        param1Int2 -= i;
        param1Int1 += i;
        this.buf.flip();
        SSLStreams.WrapperResult wrapperResult = SSLStreams.this.sendData(this.buf);
        if (wrapperResult.result.getStatus() == SSLEngineResult.Status.CLOSED) {
          this.closed = true;
          if (param1Int2 > 0)
            throw new IOException("output stream is closed"); 
        } 
      } 
    }
    
    public void flush() throws IOException {}
    
    public void close() throws IOException {
      SSLStreams.WrapperResult wrapperResult = null;
      SSLStreams.this.engine.closeOutbound();
      this.closed = true;
      SSLEngineResult.HandshakeStatus handshakeStatus = SSLEngineResult.HandshakeStatus.NEED_WRAP;
      this.buf.clear();
      while (handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_WRAP) {
        wrapperResult = SSLStreams.this.wrapper.wrapAndSend(this.buf);
        handshakeStatus = wrapperResult.result.getHandshakeStatus();
      } 
      assert wrapperResult.result.getStatus() == SSLEngineResult.Status.CLOSED;
    }
  }
  
  class Parameters extends HttpsParameters {
    InetSocketAddress addr;
    
    HttpsConfigurator cfg;
    
    SSLParameters params;
    
    Parameters(HttpsConfigurator param1HttpsConfigurator, InetSocketAddress param1InetSocketAddress) {
      this.addr = param1InetSocketAddress;
      this.cfg = param1HttpsConfigurator;
    }
    
    public InetSocketAddress getClientAddress() { return this.addr; }
    
    public HttpsConfigurator getHttpsConfigurator() { return this.cfg; }
    
    public void setSSLParameters(SSLParameters param1SSLParameters) { this.params = param1SSLParameters; }
    
    SSLParameters getSSLParameters() { return this.params; }
  }
  
  class WrapperResult {
    SSLEngineResult result;
    
    ByteBuffer buf;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\SSLStreams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */