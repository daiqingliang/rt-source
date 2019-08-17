package sun.nio.fs;

import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.AccessController;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;
import sun.security.action.GetPropertyAction;

class WindowsFileAttributes implements DosFileAttributes {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final short SIZEOF_FILE_INFORMATION = 52;
  
  private static final short OFFSETOF_FILE_INFORMATION_ATTRIBUTES = 0;
  
  private static final short OFFSETOF_FILE_INFORMATION_CREATETIME = 4;
  
  private static final short OFFSETOF_FILE_INFORMATION_LASTACCESSTIME = 12;
  
  private static final short OFFSETOF_FILE_INFORMATION_LASTWRITETIME = 20;
  
  private static final short OFFSETOF_FILE_INFORMATION_VOLSERIALNUM = 28;
  
  private static final short OFFSETOF_FILE_INFORMATION_SIZEHIGH = 32;
  
  private static final short OFFSETOF_FILE_INFORMATION_SIZELOW = 36;
  
  private static final short OFFSETOF_FILE_INFORMATION_INDEXHIGH = 44;
  
  private static final short OFFSETOF_FILE_INFORMATION_INDEXLOW = 48;
  
  private static final short SIZEOF_FILE_ATTRIBUTE_DATA = 36;
  
  private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_ATTRIBUTES = 0;
  
  private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_CREATETIME = 4;
  
  private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_LASTACCESSTIME = 12;
  
  private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_LASTWRITETIME = 20;
  
  private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_SIZEHIGH = 28;
  
  private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_SIZELOW = 32;
  
  private static final short SIZEOF_FIND_DATA = 592;
  
  private static final short OFFSETOF_FIND_DATA_ATTRIBUTES = 0;
  
  private static final short OFFSETOF_FIND_DATA_CREATETIME = 4;
  
  private static final short OFFSETOF_FIND_DATA_LASTACCESSTIME = 12;
  
  private static final short OFFSETOF_FIND_DATA_LASTWRITETIME = 20;
  
  private static final short OFFSETOF_FIND_DATA_SIZEHIGH = 28;
  
  private static final short OFFSETOF_FIND_DATA_SIZELOW = 32;
  
  private static final short OFFSETOF_FIND_DATA_RESERVED0 = 36;
  
  private static final long WINDOWS_EPOCH_IN_MICROSECONDS = -11644473600000000L;
  
  private static final boolean ensureAccurateMetadata;
  
  private final int fileAttrs;
  
  private final long creationTime;
  
  private final long lastAccessTime;
  
  private final long lastWriteTime;
  
  private final long size;
  
  private final int reparseTag;
  
  private final int volSerialNumber;
  
  private final int fileIndexHigh;
  
  private final int fileIndexLow;
  
  static FileTime toFileTime(long paramLong) {
    paramLong /= 10L;
    paramLong += -11644473600000000L;
    return FileTime.from(paramLong, TimeUnit.MICROSECONDS);
  }
  
  static long toWindowsTime(FileTime paramFileTime) {
    null = paramFileTime.to(TimeUnit.MICROSECONDS);
    null -= -11644473600000000L;
    return 10L;
  }
  
  private WindowsFileAttributes(int paramInt1, long paramLong1, long paramLong2, long paramLong3, long paramLong4, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    this.fileAttrs = paramInt1;
    this.creationTime = paramLong1;
    this.lastAccessTime = paramLong2;
    this.lastWriteTime = paramLong3;
    this.size = paramLong4;
    this.reparseTag = paramInt2;
    this.volSerialNumber = paramInt3;
    this.fileIndexHigh = paramInt4;
    this.fileIndexLow = paramInt5;
  }
  
  private static WindowsFileAttributes fromFileInformation(long paramLong, int paramInt) {
    int i = unsafe.getInt(paramLong + 0L);
    long l1 = unsafe.getLong(paramLong + 4L);
    long l2 = unsafe.getLong(paramLong + 12L);
    long l3 = unsafe.getLong(paramLong + 20L);
    long l4 = (unsafe.getInt(paramLong + 32L) << 32) + (unsafe.getInt(paramLong + 36L) & 0xFFFFFFFFL);
    int j = unsafe.getInt(paramLong + 28L);
    int k = unsafe.getInt(paramLong + 44L);
    int m = unsafe.getInt(paramLong + 48L);
    return new WindowsFileAttributes(i, l1, l2, l3, l4, paramInt, j, k, m);
  }
  
  private static WindowsFileAttributes fromFileAttributeData(long paramLong, int paramInt) {
    int i = unsafe.getInt(paramLong + 0L);
    long l1 = unsafe.getLong(paramLong + 4L);
    long l2 = unsafe.getLong(paramLong + 12L);
    long l3 = unsafe.getLong(paramLong + 20L);
    long l4 = (unsafe.getInt(paramLong + 28L) << 32) + (unsafe.getInt(paramLong + 32L) & 0xFFFFFFFFL);
    return new WindowsFileAttributes(i, l1, l2, l3, l4, paramInt, 0, 0, 0);
  }
  
  static NativeBuffer getBufferForFindData() { return NativeBuffers.getNativeBuffer(592); }
  
  static WindowsFileAttributes fromFindData(long paramLong) {
    int i = unsafe.getInt(paramLong + 0L);
    long l1 = unsafe.getLong(paramLong + 4L);
    long l2 = unsafe.getLong(paramLong + 12L);
    long l3 = unsafe.getLong(paramLong + 20L);
    long l4 = (unsafe.getInt(paramLong + 28L) << 32) + (unsafe.getInt(paramLong + 32L) & 0xFFFFFFFFL);
    int j = isReparsePoint(i) ? unsafe.getInt(paramLong + 36L) : 0;
    return new WindowsFileAttributes(i, l1, l2, l3, l4, j, 0, 0, 0);
  }
  
  static WindowsFileAttributes readAttributes(long paramLong) {
    nativeBuffer = NativeBuffers.getNativeBuffer(52);
    try {
      long l = nativeBuffer.address();
      WindowsNativeDispatcher.GetFileInformationByHandle(paramLong, l);
      int i = 0;
      int j = unsafe.getInt(l + 0L);
      if (isReparsePoint(j)) {
        char c = '䀀';
        nativeBuffer1 = NativeBuffers.getNativeBuffer(c);
        try {
          WindowsNativeDispatcher.DeviceIoControlGetReparsePoint(paramLong, nativeBuffer1.address(), c);
          i = (int)unsafe.getLong(nativeBuffer1.address());
        } finally {
          nativeBuffer1.release();
        } 
      } 
      return fromFileInformation(l, i);
    } finally {
      nativeBuffer.release();
    } 
  }
  
  static WindowsFileAttributes get(WindowsPath paramWindowsPath, boolean paramBoolean) throws WindowsException {
    if (!ensureAccurateMetadata) {
      WindowsException windowsException = null;
      nativeBuffer = NativeBuffers.getNativeBuffer(36);
      try {
        long l1 = nativeBuffer.address();
        WindowsNativeDispatcher.GetFileAttributesEx(paramWindowsPath.getPathForWin32Calls(), l1);
        int i = unsafe.getInt(l1 + 0L);
        if (!isReparsePoint(i))
          return fromFileAttributeData(l1, 0); 
      } catch (WindowsException windowsException1) {
        if (windowsException1.lastError() != 32)
          throw windowsException1; 
        windowsException = windowsException1;
      } finally {
        nativeBuffer.release();
      } 
      if (windowsException != null) {
        String str = paramWindowsPath.getPathForWin32Calls();
        char c = str.charAt(str.length() - 1);
        if (c == ':' || c == '\\')
          throw windowsException; 
        nativeBuffer = getBufferForFindData();
        try {
          long l1 = WindowsNativeDispatcher.FindFirstFile(str, nativeBuffer.address());
          WindowsNativeDispatcher.FindClose(l1);
          WindowsFileAttributes windowsFileAttributes = fromFindData(nativeBuffer.address());
          if (windowsFileAttributes.isReparsePoint())
            throw windowsException; 
          return windowsFileAttributes;
        } catch (WindowsException windowsException1) {
          throw windowsException;
        } finally {
          nativeBuffer.release();
        } 
      } 
    } 
    l = paramWindowsPath.openForReadAttributeAccess(paramBoolean);
    try {
      return readAttributes(l);
    } finally {
      WindowsNativeDispatcher.CloseHandle(l);
    } 
  }
  
  static boolean isSameFile(WindowsFileAttributes paramWindowsFileAttributes1, WindowsFileAttributes paramWindowsFileAttributes2) { return (paramWindowsFileAttributes1.volSerialNumber == paramWindowsFileAttributes2.volSerialNumber && paramWindowsFileAttributes1.fileIndexHigh == paramWindowsFileAttributes2.fileIndexHigh && paramWindowsFileAttributes1.fileIndexLow == paramWindowsFileAttributes2.fileIndexLow); }
  
  static boolean isReparsePoint(int paramInt) { return ((paramInt & 0x400) != 0); }
  
  int attributes() { return this.fileAttrs; }
  
  int volSerialNumber() { return this.volSerialNumber; }
  
  int fileIndexHigh() { return this.fileIndexHigh; }
  
  int fileIndexLow() { return this.fileIndexLow; }
  
  public long size() { return this.size; }
  
  public FileTime lastModifiedTime() { return toFileTime(this.lastWriteTime); }
  
  public FileTime lastAccessTime() { return toFileTime(this.lastAccessTime); }
  
  public FileTime creationTime() { return toFileTime(this.creationTime); }
  
  public Object fileKey() { return null; }
  
  boolean isReparsePoint() { return isReparsePoint(this.fileAttrs); }
  
  boolean isDirectoryLink() { return (isSymbolicLink() && (this.fileAttrs & 0x10) != 0); }
  
  public boolean isSymbolicLink() { return (this.reparseTag == -1610612724); }
  
  public boolean isDirectory() { return isSymbolicLink() ? false : (((this.fileAttrs & 0x10) != 0)); }
  
  public boolean isOther() { return isSymbolicLink() ? false : (((this.fileAttrs & 0x440) != 0)); }
  
  public boolean isRegularFile() { return (!isSymbolicLink() && !isDirectory() && !isOther()); }
  
  public boolean isReadOnly() { return ((this.fileAttrs & true) != 0); }
  
  public boolean isHidden() { return ((this.fileAttrs & 0x2) != 0); }
  
  public boolean isArchive() { return ((this.fileAttrs & 0x20) != 0); }
  
  public boolean isSystem() { return ((this.fileAttrs & 0x4) != 0); }
  
  static  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.nio.fs.ensureAccurateMetadata", "false"));
    ensureAccurateMetadata = (str.length() == 0) ? true : Boolean.valueOf(str).booleanValue();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsFileAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */