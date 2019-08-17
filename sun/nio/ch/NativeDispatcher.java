package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;

abstract class NativeDispatcher {
  abstract int read(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException;
  
  boolean needsPositionLock() { return false; }
  
  int pread(FileDescriptor paramFileDescriptor, long paramLong1, int paramInt, long paramLong2) throws IOException { throw new IOException("Operation Unsupported"); }
  
  abstract long readv(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException;
  
  abstract int write(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException;
  
  int pwrite(FileDescriptor paramFileDescriptor, long paramLong1, int paramInt, long paramLong2) throws IOException { throw new IOException("Operation Unsupported"); }
  
  abstract long writev(FileDescriptor paramFileDescriptor, long paramLong, int paramInt) throws IOException;
  
  abstract void close(FileDescriptor paramFileDescriptor) throws IOException;
  
  void preClose(FileDescriptor paramFileDescriptor) throws IOException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\NativeDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */