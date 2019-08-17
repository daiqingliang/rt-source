package sun.misc;

import java.io.FileDescriptor;

public interface JavaIOFileDescriptorAccess {
  void set(FileDescriptor paramFileDescriptor, int paramInt);
  
  int get(FileDescriptor paramFileDescriptor);
  
  void setHandle(FileDescriptor paramFileDescriptor, long paramLong);
  
  long getHandle(FileDescriptor paramFileDescriptor);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\JavaIOFileDescriptorAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */