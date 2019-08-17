package java.nio.channels;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.ExecutionException;
import sun.nio.ch.ChannelInputStream;
import sun.nio.cs.StreamDecoder;
import sun.nio.cs.StreamEncoder;

public final class Channels {
  private static void checkNotNull(Object paramObject, String paramString) {
    if (paramObject == null)
      throw new NullPointerException("\"" + paramString + "\" is null!"); 
  }
  
  private static void writeFullyImpl(WritableByteChannel paramWritableByteChannel, ByteBuffer paramByteBuffer) throws IOException {
    while (paramByteBuffer.remaining() > 0) {
      int i = paramWritableByteChannel.write(paramByteBuffer);
      if (i <= 0)
        throw new RuntimeException("no bytes written"); 
    } 
  }
  
  private static void writeFully(WritableByteChannel paramWritableByteChannel, ByteBuffer paramByteBuffer) throws IOException {
    if (paramWritableByteChannel instanceof SelectableChannel) {
      SelectableChannel selectableChannel = (SelectableChannel)paramWritableByteChannel;
      synchronized (selectableChannel.blockingLock()) {
        if (!selectableChannel.isBlocking())
          throw new IllegalBlockingModeException(); 
        writeFullyImpl(paramWritableByteChannel, paramByteBuffer);
      } 
    } else {
      writeFullyImpl(paramWritableByteChannel, paramByteBuffer);
    } 
  }
  
  public static InputStream newInputStream(ReadableByteChannel paramReadableByteChannel) {
    checkNotNull(paramReadableByteChannel, "ch");
    return new ChannelInputStream(paramReadableByteChannel);
  }
  
  public static OutputStream newOutputStream(final WritableByteChannel ch) {
    checkNotNull(paramWritableByteChannel, "ch");
    return new OutputStream() {
        private ByteBuffer bb = null;
        
        private byte[] bs = null;
        
        private byte[] b1 = null;
        
        public void write(int param1Int) throws IOException {
          if (this.b1 == null)
            this.b1 = new byte[1]; 
          this.b1[0] = (byte)param1Int;
          write(this.b1);
        }
        
        public void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
          if (param1Int1 < 0 || param1Int1 > param1ArrayOfByte.length || param1Int2 < 0 || param1Int1 + param1Int2 > param1ArrayOfByte.length || param1Int1 + param1Int2 < 0)
            throw new IndexOutOfBoundsException(); 
          if (param1Int2 == 0)
            return; 
          ByteBuffer byteBuffer = (this.bs == param1ArrayOfByte) ? this.bb : ByteBuffer.wrap(param1ArrayOfByte);
          byteBuffer.limit(Math.min(param1Int1 + param1Int2, byteBuffer.capacity()));
          byteBuffer.position(param1Int1);
          this.bb = byteBuffer;
          this.bs = param1ArrayOfByte;
          Channels.writeFully(ch, byteBuffer);
        }
        
        public void close() { ch.close(); }
      };
  }
  
  public static InputStream newInputStream(final AsynchronousByteChannel ch) {
    checkNotNull(paramAsynchronousByteChannel, "ch");
    return new InputStream() {
        private ByteBuffer bb = null;
        
        private byte[] bs = null;
        
        private byte[] b1 = null;
        
        public int read() throws IOException {
          if (this.b1 == null)
            this.b1 = new byte[1]; 
          int i = read(this.b1);
          return (i == 1) ? (this.b1[0] & 0xFF) : -1;
        }
        
        public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
          if (param1Int1 < 0 || param1Int1 > param1ArrayOfByte.length || param1Int2 < 0 || param1Int1 + param1Int2 > param1ArrayOfByte.length || param1Int1 + param1Int2 < 0)
            throw new IndexOutOfBoundsException(); 
          if (param1Int2 == 0)
            return 0; 
          ByteBuffer byteBuffer = (this.bs == param1ArrayOfByte) ? this.bb : ByteBuffer.wrap(param1ArrayOfByte);
          byteBuffer.position(param1Int1);
          byteBuffer.limit(Math.min(param1Int1 + param1Int2, byteBuffer.capacity()));
          this.bb = byteBuffer;
          this.bs = param1ArrayOfByte;
          bool = false;
          while (true) {
            try {
              return ((Integer)ch.read(byteBuffer).get()).intValue();
            } catch (ExecutionException executionException) {
              throw new IOException(executionException.getCause());
            } catch (InterruptedException interruptedException) {
            
            } finally {
              if (bool)
                Thread.currentThread().interrupt(); 
            } 
          } 
        }
        
        public void close() { ch.close(); }
      };
  }
  
  public static OutputStream newOutputStream(final AsynchronousByteChannel ch) {
    checkNotNull(paramAsynchronousByteChannel, "ch");
    return new OutputStream() {
        private ByteBuffer bb = null;
        
        private byte[] bs = null;
        
        private byte[] b1 = null;
        
        public void write(int param1Int) throws IOException {
          if (this.b1 == null)
            this.b1 = new byte[1]; 
          this.b1[0] = (byte)param1Int;
          write(this.b1);
        }
        
        public void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
          if (param1Int1 < 0 || param1Int1 > param1ArrayOfByte.length || param1Int2 < 0 || param1Int1 + param1Int2 > param1ArrayOfByte.length || param1Int1 + param1Int2 < 0)
            throw new IndexOutOfBoundsException(); 
          if (param1Int2 == 0)
            return; 
          ByteBuffer byteBuffer = (this.bs == param1ArrayOfByte) ? this.bb : ByteBuffer.wrap(param1ArrayOfByte);
          byteBuffer.limit(Math.min(param1Int1 + param1Int2, byteBuffer.capacity()));
          byteBuffer.position(param1Int1);
          this.bb = byteBuffer;
          this.bs = param1ArrayOfByte;
          bool = false;
          try {
            while (byteBuffer.remaining() > 0) {
              try {
                ch.write(byteBuffer).get();
              } catch (ExecutionException executionException) {
                throw new IOException(executionException.getCause());
              } catch (InterruptedException interruptedException) {
                bool = true;
              } 
            } 
          } finally {
            if (bool)
              Thread.currentThread().interrupt(); 
          } 
        }
        
        public void close() { ch.close(); }
      };
  }
  
  public static ReadableByteChannel newChannel(InputStream paramInputStream) {
    checkNotNull(paramInputStream, "in");
    return (paramInputStream instanceof FileInputStream && FileInputStream.class.equals(paramInputStream.getClass())) ? ((FileInputStream)paramInputStream).getChannel() : new ReadableByteChannelImpl(paramInputStream);
  }
  
  public static WritableByteChannel newChannel(OutputStream paramOutputStream) {
    checkNotNull(paramOutputStream, "out");
    return (paramOutputStream instanceof FileOutputStream && FileOutputStream.class.equals(paramOutputStream.getClass())) ? ((FileOutputStream)paramOutputStream).getChannel() : new WritableByteChannelImpl(paramOutputStream);
  }
  
  public static Reader newReader(ReadableByteChannel paramReadableByteChannel, CharsetDecoder paramCharsetDecoder, int paramInt) {
    checkNotNull(paramReadableByteChannel, "ch");
    return StreamDecoder.forDecoder(paramReadableByteChannel, paramCharsetDecoder.reset(), paramInt);
  }
  
  public static Reader newReader(ReadableByteChannel paramReadableByteChannel, String paramString) {
    checkNotNull(paramString, "csName");
    return newReader(paramReadableByteChannel, Charset.forName(paramString).newDecoder(), -1);
  }
  
  public static Writer newWriter(WritableByteChannel paramWritableByteChannel, CharsetEncoder paramCharsetEncoder, int paramInt) {
    checkNotNull(paramWritableByteChannel, "ch");
    return StreamEncoder.forEncoder(paramWritableByteChannel, paramCharsetEncoder.reset(), paramInt);
  }
  
  public static Writer newWriter(WritableByteChannel paramWritableByteChannel, String paramString) {
    checkNotNull(paramString, "csName");
    return newWriter(paramWritableByteChannel, Charset.forName(paramString).newEncoder(), -1);
  }
  
  private static class ReadableByteChannelImpl extends AbstractInterruptibleChannel implements ReadableByteChannel {
    InputStream in;
    
    private static final int TRANSFER_SIZE = 8192;
    
    private byte[] buf = new byte[0];
    
    private boolean open = true;
    
    private Object readLock = new Object();
    
    ReadableByteChannelImpl(InputStream param1InputStream) { this.in = param1InputStream; }
    
    public int read(ByteBuffer param1ByteBuffer) throws IOException {
      int i = param1ByteBuffer.remaining();
      int j = 0;
      k = 0;
      synchronized (this.readLock) {
        while (j < i) {
          int m = Math.min(i - j, 8192);
          if (this.buf.length < m)
            this.buf = new byte[m]; 
          if (j > 0 && this.in.available() <= 0)
            break; 
          try {
            begin();
            k = this.in.read(this.buf, 0, m);
          } finally {
            end((k > 0));
          } 
          if (k < 0)
            break; 
          j += k;
          param1ByteBuffer.put(this.buf, 0, k);
        } 
        if (k < 0 && j == 0)
          return -1; 
        return j;
      } 
    }
    
    protected void implCloseChannel() {
      this.in.close();
      this.open = false;
    }
  }
  
  private static class WritableByteChannelImpl extends AbstractInterruptibleChannel implements WritableByteChannel {
    OutputStream out;
    
    private static final int TRANSFER_SIZE = 8192;
    
    private byte[] buf = new byte[0];
    
    private boolean open = true;
    
    private Object writeLock = new Object();
    
    WritableByteChannelImpl(OutputStream param1OutputStream) { this.out = param1OutputStream; }
    
    public int write(ByteBuffer param1ByteBuffer) throws IOException {
      int i = param1ByteBuffer.remaining();
      int j = 0;
      synchronized (this.writeLock) {
        while (j < i) {
          k = Math.min(i - j, 8192);
          if (this.buf.length < k)
            this.buf = new byte[k]; 
          param1ByteBuffer.get(this.buf, 0, k);
          try {
            begin();
            this.out.write(this.buf, 0, k);
          } finally {
            end((k > 0));
          } 
          j += k;
        } 
        return j;
      } 
    }
    
    protected void implCloseChannel() {
      this.out.close();
      this.open = false;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\Channels.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */