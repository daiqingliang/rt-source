package sun.nio.ch;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.SelectableChannel;

public class ChannelInputStream extends InputStream {
  protected final ReadableByteChannel ch;
  
  private ByteBuffer bb = null;
  
  private byte[] bs = null;
  
  private byte[] b1 = null;
  
  public static int read(ReadableByteChannel paramReadableByteChannel, ByteBuffer paramByteBuffer, boolean paramBoolean) throws IOException {
    if (paramReadableByteChannel instanceof SelectableChannel) {
      SelectableChannel selectableChannel = (SelectableChannel)paramReadableByteChannel;
      synchronized (selectableChannel.blockingLock()) {
        boolean bool = selectableChannel.isBlocking();
        if (!bool)
          throw new IllegalBlockingModeException(); 
        if (bool != paramBoolean)
          selectableChannel.configureBlocking(paramBoolean); 
        int i = paramReadableByteChannel.read(paramByteBuffer);
        if (bool != paramBoolean)
          selectableChannel.configureBlocking(bool); 
        return i;
      } 
    } 
    return paramReadableByteChannel.read(paramByteBuffer);
  }
  
  public ChannelInputStream(ReadableByteChannel paramReadableByteChannel) { this.ch = paramReadableByteChannel; }
  
  public int read() throws IOException {
    if (this.b1 == null)
      this.b1 = new byte[1]; 
    int i = read(this.b1);
    return (i == 1) ? (this.b1[0] & 0xFF) : -1;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt1 > paramArrayOfByte.length || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfByte.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return 0; 
    ByteBuffer byteBuffer = (this.bs == paramArrayOfByte) ? this.bb : ByteBuffer.wrap(paramArrayOfByte);
    byteBuffer.limit(Math.min(paramInt1 + paramInt2, byteBuffer.capacity()));
    byteBuffer.position(paramInt1);
    this.bb = byteBuffer;
    this.bs = paramArrayOfByte;
    return read(byteBuffer);
  }
  
  protected int read(ByteBuffer paramByteBuffer) throws IOException { return read(this.ch, paramByteBuffer, true); }
  
  public int available() throws IOException {
    if (this.ch instanceof SeekableByteChannel) {
      SeekableByteChannel seekableByteChannel = (SeekableByteChannel)this.ch;
      long l = Math.max(0L, seekableByteChannel.size() - seekableByteChannel.position());
      return (l > 2147483647L) ? Integer.MAX_VALUE : (int)l;
    } 
    return 0;
  }
  
  public void close() throws IOException { this.ch.close(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\ChannelInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */