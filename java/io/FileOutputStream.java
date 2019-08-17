package java.io;

import java.nio.channels.FileChannel;
import sun.nio.ch.FileChannelImpl;

public class FileOutputStream extends OutputStream {
  private final FileDescriptor fd;
  
  private final boolean append;
  
  private FileChannel channel;
  
  private final String path;
  
  private final Object closeLock = new Object();
  
  public FileOutputStream(String paramString) throws FileNotFoundException { this((paramString != null) ? new File(paramString) : null, false); }
  
  public FileOutputStream(String paramString, boolean paramBoolean) throws FileNotFoundException { this((paramString != null) ? new File(paramString) : null, paramBoolean); }
  
  public FileOutputStream(File paramFile) throws FileNotFoundException { this(paramFile, false); }
  
  public FileOutputStream(File paramFile, boolean paramBoolean) throws FileNotFoundException {
    String str = (paramFile != null) ? paramFile.getPath() : null;
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkWrite(str); 
    if (str == null)
      throw new NullPointerException(); 
    if (paramFile.isInvalid())
      throw new FileNotFoundException("Invalid file path"); 
    this.fd = new FileDescriptor();
    this.fd.attach(this);
    this.append = paramBoolean;
    this.path = str;
    open(str, paramBoolean);
  }
  
  public FileOutputStream(FileDescriptor paramFileDescriptor) {
    SecurityManager securityManager = System.getSecurityManager();
    if (paramFileDescriptor == null)
      throw new NullPointerException(); 
    if (securityManager != null)
      securityManager.checkWrite(paramFileDescriptor); 
    this.fd = paramFileDescriptor;
    this.append = false;
    this.path = null;
    this.fd.attach(this);
  }
  
  private native void open0(String paramString, boolean paramBoolean) throws FileNotFoundException;
  
  private void open(String paramString, boolean paramBoolean) throws FileNotFoundException { open0(paramString, paramBoolean); }
  
  private native void write(int paramInt, boolean paramBoolean) throws IOException;
  
  public void write(int paramInt) throws IOException { write(paramInt, this.append); }
  
  private native void writeBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean) throws IOException;
  
  public void write(byte[] paramArrayOfByte) throws IOException { writeBytes(paramArrayOfByte, 0, paramArrayOfByte.length, this.append); }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException { writeBytes(paramArrayOfByte, paramInt1, paramInt2, this.append); }
  
  public void close() throws IOException {
    synchronized (this.closeLock) {
      if (this.closed)
        return; 
      this.closed = true;
    } 
    if (this.channel != null)
      this.channel.close(); 
    this.fd.closeAll(new Closeable() {
          public void close() throws IOException { FileOutputStream.this.close0(); }
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
        this.channel = FileChannelImpl.open(this.fd, this.path, false, true, this.append, this); 
      return this.channel;
    } 
  }
  
  protected void finalize() throws IOException {
    if (this.fd != null)
      if (this.fd == FileDescriptor.out || this.fd == FileDescriptor.err) {
        flush();
      } else {
        close();
      }  
  }
  
  private native void close0() throws IOException;
  
  private static native void initIDs() throws IOException;
  
  static  {
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\FileOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */