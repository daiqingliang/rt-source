package sun.nio.fs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystemException;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;

class WindowsFileStore extends FileStore {
  private final String root;
  
  private final WindowsNativeDispatcher.VolumeInformation volInfo;
  
  private final int volType;
  
  private final String displayName;
  
  private WindowsFileStore(String paramString) throws WindowsException {
    assert paramString.charAt(paramString.length() - 1) == '\\';
    this.root = paramString;
    this.volInfo = WindowsNativeDispatcher.GetVolumeInformation(paramString);
    this.volType = WindowsNativeDispatcher.GetDriveType(paramString);
    String str = this.volInfo.volumeName();
    if (str.length() > 0) {
      this.displayName = str;
    } else {
      this.displayName = (this.volType == 2) ? "Removable Disk" : "";
    } 
  }
  
  static WindowsFileStore create(String paramString, boolean paramBoolean) throws IOException {
    try {
      return new WindowsFileStore(paramString);
    } catch (WindowsException windowsException) {
      if (paramBoolean && windowsException.lastError() == 21)
        return null; 
      windowsException.rethrowAsIOException(paramString);
      return null;
    } 
  }
  
  static WindowsFileStore create(WindowsPath paramWindowsPath) throws IOException {
    try {
      String str;
      if (paramWindowsPath.getFileSystem().supportsLinks()) {
        str = WindowsLinkSupport.getFinalPath(paramWindowsPath, true);
      } else {
        WindowsFileAttributes.get(paramWindowsPath, true);
        str = paramWindowsPath.getPathForWin32Calls();
      } 
      try {
        return createFromPath(str);
      } catch (WindowsException windowsException) {
        if (windowsException.lastError() != 144)
          throw windowsException; 
        str = WindowsLinkSupport.getFinalPath(paramWindowsPath);
        if (str == null)
          throw new FileSystemException(paramWindowsPath.getPathForExceptionMessage(), null, "Couldn't resolve path"); 
        return createFromPath(str);
      } 
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(paramWindowsPath);
      return null;
    } 
  }
  
  private static WindowsFileStore createFromPath(String paramString) throws WindowsException {
    String str = WindowsNativeDispatcher.GetVolumePathName(paramString);
    return new WindowsFileStore(str);
  }
  
  WindowsNativeDispatcher.VolumeInformation volumeInformation() { return this.volInfo; }
  
  int volumeType() { return this.volType; }
  
  public String name() { return this.volInfo.volumeName(); }
  
  public String type() { return this.volInfo.fileSystemName(); }
  
  public boolean isReadOnly() { return ((this.volInfo.flags() & 0x80000) != 0); }
  
  private WindowsNativeDispatcher.DiskFreeSpace readDiskFreeSpace() throws IOException {
    try {
      return WindowsNativeDispatcher.GetDiskFreeSpaceEx(this.root);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(this.root);
      return null;
    } 
  }
  
  public long getTotalSpace() throws IOException { return readDiskFreeSpace().totalNumberOfBytes(); }
  
  public long getUsableSpace() throws IOException { return readDiskFreeSpace().freeBytesAvailable(); }
  
  public long getUnallocatedSpace() throws IOException { return readDiskFreeSpace().freeBytesAvailable(); }
  
  public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> paramClass) {
    if (paramClass == null)
      throw new NullPointerException(); 
    return (V)(FileStoreAttributeView)null;
  }
  
  public Object getAttribute(String paramString) throws IOException {
    if (paramString.equals("totalSpace"))
      return Long.valueOf(getTotalSpace()); 
    if (paramString.equals("usableSpace"))
      return Long.valueOf(getUsableSpace()); 
    if (paramString.equals("unallocatedSpace"))
      return Long.valueOf(getUnallocatedSpace()); 
    if (paramString.equals("volume:vsn"))
      return Integer.valueOf(this.volInfo.volumeSerialNumber()); 
    if (paramString.equals("volume:isRemovable"))
      return Boolean.valueOf((this.volType == 2)); 
    if (paramString.equals("volume:isCdrom"))
      return Boolean.valueOf((this.volType == 5)); 
    throw new UnsupportedOperationException("'" + paramString + "' not recognized");
  }
  
  public boolean supportsFileAttributeView(Class<? extends FileAttributeView> paramClass) {
    if (paramClass == null)
      throw new NullPointerException(); 
    return (paramClass == java.nio.file.attribute.BasicFileAttributeView.class || paramClass == java.nio.file.attribute.DosFileAttributeView.class) ? true : ((paramClass == java.nio.file.attribute.AclFileAttributeView.class || paramClass == java.nio.file.attribute.FileOwnerAttributeView.class) ? (((this.volInfo.flags() & 0x8) != 0)) : ((paramClass == java.nio.file.attribute.UserDefinedFileAttributeView.class) ? (((this.volInfo.flags() & 0x40000) != 0)) : false));
  }
  
  public boolean supportsFileAttributeView(String paramString) { return (paramString.equals("basic") || paramString.equals("dos")) ? true : (paramString.equals("acl") ? supportsFileAttributeView(java.nio.file.attribute.AclFileAttributeView.class) : (paramString.equals("owner") ? supportsFileAttributeView(java.nio.file.attribute.FileOwnerAttributeView.class) : (paramString.equals("user") ? supportsFileAttributeView(java.nio.file.attribute.UserDefinedFileAttributeView.class) : 0))); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof WindowsFileStore))
      return false; 
    WindowsFileStore windowsFileStore = (WindowsFileStore)paramObject;
    return this.root.equals(windowsFileStore.root);
  }
  
  public int hashCode() { return this.root.hashCode(); }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(this.displayName);
    if (stringBuilder.length() > 0)
      stringBuilder.append(" "); 
    stringBuilder.append("(");
    stringBuilder.append(this.root.subSequence(0, this.root.length() - 1));
    stringBuilder.append(")");
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsFileStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */