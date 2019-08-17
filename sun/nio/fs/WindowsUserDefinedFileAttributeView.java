package sun.nio.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import sun.misc.Unsafe;

class WindowsUserDefinedFileAttributeView extends AbstractUserDefinedFileAttributeView {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private final WindowsPath file;
  
  private final boolean followLinks;
  
  private String join(String paramString1, String paramString2) {
    if (paramString2 == null)
      throw new NullPointerException("'name' is null"); 
    return paramString1 + ":" + paramString2;
  }
  
  private String join(WindowsPath paramWindowsPath, String paramString) throws WindowsException { return join(paramWindowsPath.getPathForWin32Calls(), paramString); }
  
  WindowsUserDefinedFileAttributeView(WindowsPath paramWindowsPath, boolean paramBoolean) {
    this.file = paramWindowsPath;
    this.followLinks = paramBoolean;
  }
  
  private List<String> listUsingStreamEnumeration() throws IOException {
    ArrayList arrayList = new ArrayList();
    try {
      WindowsNativeDispatcher.FirstStream firstStream = WindowsNativeDispatcher.FindFirstStream(this.file.getPathForWin32Calls());
      if (firstStream != null) {
        l = firstStream.handle();
        try {
          String str = firstStream.name();
          if (!str.equals("::$DATA")) {
            String[] arrayOfString = str.split(":");
            arrayList.add(arrayOfString[1]);
          } 
          while ((str = WindowsNativeDispatcher.FindNextStream(l)) != null) {
            String[] arrayOfString = str.split(":");
            arrayList.add(arrayOfString[1]);
          } 
        } finally {
          WindowsNativeDispatcher.FindClose(l);
        } 
      } 
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(this.file);
    } 
    return Collections.unmodifiableList(arrayList);
  }
  
  private List<String> listUsingBackupRead() throws IOException {
    l = -1L;
    try {
      int i = 33554432;
      if (!this.followLinks && this.file.getFileSystem().supportsLinks())
        i |= 0x200000; 
      l = WindowsNativeDispatcher.CreateFile(this.file.getPathForWin32Calls(), -2147483648, 1, 3, i);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(this.file);
    } 
    nativeBuffer = null;
    ArrayList arrayList = new ArrayList();
    try {
      nativeBuffer = NativeBuffers.getNativeBuffer(4096);
      long l1 = nativeBuffer.address();
      l2 = 0L;
      try {
        while (true) {
          WindowsNativeDispatcher.BackupResult backupResult = WindowsNativeDispatcher.BackupRead(l, l1, 20, false, l2);
          l2 = backupResult.context();
          if (backupResult.bytesTransferred() == 0)
            break; 
          int i = unsafe.getInt(l1 + 0L);
          long l3 = unsafe.getLong(l1 + 8L);
          int j = unsafe.getInt(l1 + 16L);
          if (j > 0) {
            backupResult = WindowsNativeDispatcher.BackupRead(l, l1, j, false, l2);
            if (backupResult.bytesTransferred() != j)
              break; 
          } 
          if (i == 4) {
            char[] arrayOfChar = new char[j / 2];
            unsafe.copyMemory(null, l1, arrayOfChar, Unsafe.ARRAY_CHAR_BASE_OFFSET, j);
            String[] arrayOfString = (new String(arrayOfChar)).split(":");
            if (arrayOfString.length == 3)
              arrayList.add(arrayOfString[1]); 
          } 
          if (i == 9)
            throw new IOException("Spare blocks not handled"); 
          if (l3 > 0L)
            WindowsNativeDispatcher.BackupSeek(l, l3, l2); 
        } 
      } catch (WindowsException windowsException) {
        throw new IOException(windowsException.errorString());
      } finally {
        if (l2 != 0L)
          try {
            WindowsNativeDispatcher.BackupRead(l, 0L, 0, true, l2);
          } catch (WindowsException windowsException) {} 
      } 
    } finally {
      if (nativeBuffer != null)
        nativeBuffer.release(); 
      WindowsNativeDispatcher.CloseHandle(l);
    } 
    return Collections.unmodifiableList(arrayList);
  }
  
  public List<String> list() throws IOException {
    if (System.getSecurityManager() != null)
      checkAccess(this.file.getPathForPermissionCheck(), true, false); 
    return this.file.getFileSystem().supportsStreamEnumeration() ? listUsingStreamEnumeration() : listUsingBackupRead();
  }
  
  public int size(String paramString) throws IOException {
    if (System.getSecurityManager() != null)
      checkAccess(this.file.getPathForPermissionCheck(), true, false); 
    fileChannel = null;
    try {
      HashSet hashSet = new HashSet();
      hashSet.add(StandardOpenOption.READ);
      if (!this.followLinks)
        hashSet.add(WindowsChannelFactory.OPEN_REPARSE_POINT); 
      fileChannel = WindowsChannelFactory.newFileChannel(join(this.file, paramString), null, hashSet, 0L);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(join(this.file.getPathForPermissionCheck(), paramString));
    } 
    try {
      long l = fileChannel.size();
      if (l > 2147483647L)
        throw new ArithmeticException("Stream too large"); 
      return (int)l;
    } finally {
      fileChannel.close();
    } 
  }
  
  public int read(String paramString, ByteBuffer paramByteBuffer) throws IOException {
    if (System.getSecurityManager() != null)
      checkAccess(this.file.getPathForPermissionCheck(), true, false); 
    fileChannel = null;
    try {
      HashSet hashSet = new HashSet();
      hashSet.add(StandardOpenOption.READ);
      if (!this.followLinks)
        hashSet.add(WindowsChannelFactory.OPEN_REPARSE_POINT); 
      fileChannel = WindowsChannelFactory.newFileChannel(join(this.file, paramString), null, hashSet, 0L);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(join(this.file.getPathForPermissionCheck(), paramString));
    } 
    try {
      if (fileChannel.size() > paramByteBuffer.remaining())
        throw new IOException("Stream too large"); 
      int i;
      for (i = 0; paramByteBuffer.hasRemaining(); i += j) {
        int j = fileChannel.read(paramByteBuffer);
        if (j < 0)
          break; 
      } 
      return i;
    } finally {
      fileChannel.close();
    } 
  }
  
  public int write(String paramString, ByteBuffer paramByteBuffer) throws IOException {
    if (System.getSecurityManager() != null)
      checkAccess(this.file.getPathForPermissionCheck(), false, true); 
    l = -1L;
    try {
      int i = 33554432;
      if (!this.followLinks)
        i |= 0x200000; 
      l = WindowsNativeDispatcher.CreateFile(this.file.getPathForWin32Calls(), -2147483648, 7, 3, i);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(this.file);
    } 
    try {
      HashSet hashSet = new HashSet();
      if (!this.followLinks)
        hashSet.add(WindowsChannelFactory.OPEN_REPARSE_POINT); 
      hashSet.add(StandardOpenOption.CREATE);
      hashSet.add(StandardOpenOption.WRITE);
      hashSet.add(StandardOpenOption.TRUNCATE_EXISTING);
      fileChannel = null;
      try {
        fileChannel = WindowsChannelFactory.newFileChannel(join(this.file, paramString), null, hashSet, 0L);
      } catch (WindowsException windowsException) {
        windowsException.rethrowAsIOException(join(this.file.getPathForPermissionCheck(), paramString));
      } 
    } finally {
      WindowsNativeDispatcher.CloseHandle(l);
    } 
  }
  
  public void delete(String paramString) throws IOException {
    if (System.getSecurityManager() != null)
      checkAccess(this.file.getPathForPermissionCheck(), false, true); 
    String str1 = WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
    String str2 = join(str1, paramString);
    try {
      WindowsNativeDispatcher.DeleteFile(str2);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(str2);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsUserDefinedFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */