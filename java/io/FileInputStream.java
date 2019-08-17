package java.io;

import java.nio.channels.FileChannel;
import sun.nio.ch.FileChannelImpl;

public class FileInputStream extends InputStream {
  private final FileDescriptor fd;
  
  private final String path;
  
  private FileChannel channel = null;
  
  private final Object closeLock = new Object();
  
  public FileInputStream(String paramString) throws FileNotFoundException { this((paramString != null) ? new File(paramString) : null); }
  
  public FileInputStream(File paramFile) throws FileNotFoundException {
    String str = (paramFile != null) ? paramFile.getPath() : null;
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkRead(str); 
    if (str == null)
      throw new NullPointerException(); 
    if (paramFile.isInvalid())
      throw new FileNotFoundException("Invalid file path"); 
    this.fd = new FileDescriptor();
    this.fd.attach(this);
    this.path = str;
    open(str);
  }
  
  public FileInputStream(FileDescriptor paramFileDescriptor) {
    SecurityManager securityManager = System.getSecurityManager();
    if (paramFileDescriptor == null)
      throw new NullPointerException(); 
    if (securityManager != null)
      securityManager.checkRead(paramFileDescriptor); 
    this.fd = paramFileDescriptor;
    this.path = null;
    this.fd.attach(this);
  }
  
  private native void open0(String paramString) throws FileNotFoundException;
  
  private void open(String paramString) throws FileNotFoundException { open0(paramString); }
  
  public int read() throws IOException { return read0(); }
  
  private native int read0() throws IOException;
  
  private native int readBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException;
  
  public int read(byte[] paramArrayOfByte) throws IOException { return readBytes(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException { return readBytes(paramArrayOfByte, paramInt1, paramInt2); }
  
  public long skip(long paramLong) throws IOException { return skip0(paramLong); }
  
  private native long skip0(long paramLong) throws IOException;
  
  public int available() throws IOException { return available0(); }
  
  private native int available0() throws IOException;
  
  public void close() throws IOException {
    synchronized (this.closeLock) {
      if (this.closed)
        return; 
      this.closed = true;
    } 
    if (this.channel != null)
      this.channel.close(); 
    this.fd.closeAll(new Closeable() {
          public void close() throws IOException { FileInputStream.this.close0(); }
        });
  }
  
  public final FileDescriptor getFD() throws IOException {
    if (this.fd != null)
      return this.fd; 
    throw new IOException();
  }
  
  public FileChannel getChannel() {
    synchronized (this) {
      if (this.channel == null)
        this.channel = FileChannelImpl.open(this.fd, this.path, true, false, this); 
      return this.channel;
    } 
  }
  
  private static native void initIDs() throws IOException;
  
  private native void close0() throws IOException;
  
  protected void finalize() throws IOException {
    if (this.fd != null && this.fd != FileDescriptor.in)
      close(); 
  }
  
  static  {
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\FileInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */