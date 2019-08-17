package java.net;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

class SocketOutputStream extends FileOutputStream {
  private AbstractPlainSocketImpl impl = null;
  
  private byte[] temp = new byte[1];
  
  private Socket socket = null;
  
  private boolean closing = false;
  
  SocketOutputStream(AbstractPlainSocketImpl paramAbstractPlainSocketImpl) throws IOException {
    super(paramAbstractPlainSocketImpl.getFileDescriptor());
    this.impl = paramAbstractPlainSocketImpl;
    this.socket = paramAbstractPlainSocketImpl.getSocket();
  }
  
  public final FileChannel getChannel() { return null; }
  
  private native void socketWrite0(FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException;
  
  private void socketWrite(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 <= 0 || paramInt1 < 0 || paramInt2 > paramArrayOfByte.length - paramInt1) {
      if (paramInt2 == 0)
        return; 
      throw new ArrayIndexOutOfBoundsException("len == " + paramInt2 + " off == " + paramInt1 + " buffer length == " + paramArrayOfByte.length);
    } 
    FileDescriptor fileDescriptor = this.impl.acquireFD();
    try {
      socketWrite0(fileDescriptor, paramArrayOfByte, paramInt1, paramInt2);
    } catch (SocketException socketException) {
      if (socketException instanceof sun.net.ConnectionResetException) {
        this.impl.setConnectionResetPending();
        socketException = new SocketException("Connection reset");
      } 
      if (this.impl.isClosedOrPending())
        throw new SocketException("Socket closed"); 
      throw socketException;
    } finally {
      this.impl.releaseFD();
    } 
  }
  
  public void write(int paramInt) throws IOException {
    this.temp[0] = (byte)paramInt;
    socketWrite(this.temp, 0, 1);
  }
  
  public void write(byte[] paramArrayOfByte) throws IOException { socketWrite(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException { socketWrite(paramArrayOfByte, paramInt1, paramInt2); }
  
  public void close() throws IOException {
    if (this.closing)
      return; 
    this.closing = true;
    if (this.socket != null) {
      if (!this.socket.isClosed())
        this.socket.close(); 
    } else {
      this.impl.close();
    } 
    this.closing = false;
  }
  
  protected void finalize() throws IOException {}
  
  private static native void init() throws IOException;
  
  static  {
    init();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\SocketOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */