package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;

public class FileKey {
  private long dwVolumeSerialNumber;
  
  private long nFileIndexHigh;
  
  private long nFileIndexLow;
  
  public static FileKey create(FileDescriptor paramFileDescriptor) {
    FileKey fileKey = new FileKey();
    try {
      fileKey.init(paramFileDescriptor);
    } catch (IOException iOException) {
      throw new Error(iOException);
    } 
    return fileKey;
  }
  
  public int hashCode() { return (int)(this.dwVolumeSerialNumber ^ this.dwVolumeSerialNumber >>> 32) + (int)(this.nFileIndexHigh ^ this.nFileIndexHigh >>> 32) + (int)(this.nFileIndexLow ^ this.nFileIndexHigh >>> 32); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof FileKey))
      return false; 
    FileKey fileKey = (FileKey)paramObject;
    return !(this.dwVolumeSerialNumber != fileKey.dwVolumeSerialNumber || this.nFileIndexHigh != fileKey.nFileIndexHigh || this.nFileIndexLow != fileKey.nFileIndexLow);
  }
  
  private native void init(FileDescriptor paramFileDescriptor) throws IOException;
  
  private static native void initIDs();
  
  static  {
    IOUtil.load();
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\FileKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */