package java.io;

import java.util.ArrayList;
import java.util.List;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

public final class FileDescriptor {
  private int fd = -1;
  
  private long handle = -1L;
  
  private Closeable parent;
  
  private List<Closeable> otherParents;
  
  private boolean closed;
  
  public static final FileDescriptor in;
  
  public static final FileDescriptor out;
  
  public static final FileDescriptor err;
  
  public boolean valid() { return (this.handle != -1L || this.fd != -1); }
  
  public native void sync();
  
  private static native void initIDs();
  
  private static native long set(int paramInt);
  
  private static FileDescriptor standardStream(int paramInt) {
    FileDescriptor fileDescriptor;
    fileDescriptor.handle = (fileDescriptor = new FileDescriptor()).set(paramInt);
    return fileDescriptor;
  }
  
  void attach(Closeable paramCloseable) {
    if (this.parent == null) {
      this.parent = paramCloseable;
    } else if (this.otherParents == null) {
      this.otherParents = new ArrayList();
      this.otherParents.add(this.parent);
      this.otherParents.add(paramCloseable);
    } else {
      this.otherParents.add(paramCloseable);
    } 
  }
  
  void closeAll(Closeable paramCloseable) {
    if (!this.closed) {
      this.closed = true;
      iOException = null;
      try (Closeable null = paramCloseable) {
        if (this.otherParents != null)
          for (Closeable closeable1 : this.otherParents) {
            try {
              closeable1.close();
            } catch (IOException iOException1) {
              if (iOException == null) {
                iOException = iOException1;
                continue;
              } 
              iOException.addSuppressed(iOException1);
            } 
          }  
      } catch (IOException iOException1) {
        if (iOException != null)
          iOException1.addSuppressed(iOException); 
        iOException = iOException1;
      } finally {
        if (iOException != null)
          throw iOException; 
      } 
    } 
  }
  
  static  {
    initIDs();
    SharedSecrets.setJavaIOFileDescriptorAccess(new JavaIOFileDescriptorAccess() {
          public void set(FileDescriptor param1FileDescriptor, int param1Int) { param1FileDescriptor.fd = param1Int; }
          
          public int get(FileDescriptor param1FileDescriptor) { return param1FileDescriptor.fd; }
          
          public void setHandle(FileDescriptor param1FileDescriptor, long param1Long) { param1FileDescriptor.handle = param1Long; }
          
          public long getHandle(FileDescriptor param1FileDescriptor) { return param1FileDescriptor.handle; }
        });
    err = (out = (in = standardStream(0)).standardStream(1)).standardStream(2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\FileDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */